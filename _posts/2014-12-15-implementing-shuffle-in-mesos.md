---           
layout: post
title: "Implementing shuffle in Mesos"
date : 2014-12-13
categories: mesos scala
---

Many times in a distributed systems, need of shuffle arises. Map/Reduce implementations like Hadoop,Spark heavily depend upon effective shuffling to do the distributed processing. So whenever we build a new distributed system from scratch, it will be nice to have the ability to do shuffle.

This post talks how to implement shuffle on mesos.

This post extends the custom scala executor discussed here [post](/custom-mesos-executor-scala/). If you are new to mesos please go through that post before continuing.

tl;dr Access the complete code on [github](https://github.com/phatak-dev/blog/tree/master/code/MesosShuffle).

## What is shuffle?
Shuffle is an operation where result produced at one machine is moved to another machine over the network, in order to combine the results. The operations like reduceByKey, join in Hadoop or Spark uses shuffling.

## Implementing Shuffle in Mesos

The following steps are one of the ways to implement shuffle in mesos.This shuffle supports implementing operations like reduce,reduceByKey,groupByKey etc. The following implementation is inspired by the spark implementation.

## Shuffle interface

{% highlight scala %} 
 trait Shuffle[K,V,C]{
  def compute(list: List[List[(K, V)]],
  createCombiner: V => C,
  mergeValue: (C, V) => C,
  mergeCombiners: (C, C) => C,
  numberOfOutputSplit: Int,
  scheduler: TaskScheduler):List[(K,C)]
}
{% endhighlight %}

The trait has three types  

 * K - key type    

 * V - value type   

 * C - combined value type (Final result type)

 compute is an abstract method which takes the following functions

 * createCombiner - As name suggests, its a function creates combiner for each partition. It will start with initial value provided by v parameter.

 * mergeValue - for every value in partition, mergeValue will be used to update the combine

 * mergeCombiners - its a reduce side job of merging two different combiners

 numberOfOutputSplit allows us to control number of reduce tasks and final parameter scheduler allow us as to access the context.

## Local file based shuffle implementation

The following code explains the implementation of shuffle using local file system and http server.

### Step 1 : Implement Shuffle trait 
{% highlight scala %} 

  class LocalShuffle[K, V, C] extends Shuffle[K, V, C] with Serializable {
  override def compute(rows: List[List[(K, V)]], createCombiner: (V) => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numberOfOutputSplit: Int, scheduler: TaskScheduler):
  List[(K, C)] = {

{% endhighlight %}

The rows variable simulating a in memory collection with multiple rows. I am using an in memory collection to keep things simple. You can replace it with RDD or a hadoop file data.

### Step 2 : Number each split
{% highlight scala %} 
val numberedList = rows.zipWithIndex
{% endhighlight %} 

we number each split with index so that we can use it to identify it when we shuffle.

### Step 3: Create local combiner tasks
For each split, we create a forEachTask 

{% highlight scala %} 
 val tasks = numberedList.map(value => forEachTask(value))
{% endhighlight %} 

Implementation of *forEach* task.

{% highlight scala %} 
 def forEachTask(pair: (List[(K, V)], Int)) = {
   new FunctionTask[(Int, String)](() => {
{% endhighlight %} 

forEachTask returns a function task which does following.

{% highlight scala %} 
  
val tempDir = Utils.createTempDir("/tmp/shuffleLocal").getAbsoluteFile
println(s"using tempDir $tempDir")

val index = pair._2
val iterator = pair._1

{% endhighlight %} 

We create a temporary directory for each split, where we are going to write the output files for that split. 

{% highlight scala %} 

val buckets = Array.tabulate(numberOfOutputSplit)(_ => new HashMap[K, C])
for ((k, v) <- iterator) {
var bucketId = k.hashCode() % numberOfOutputSplit
if (bucketId < 0) {
  bucketId += numberOfOutputSplit
}          
val bucket = buckets(bucketId)
{% endhighlight %} 

We create buckets(partitions) using hashParitioner.The for each value, we determine which bucket it belongs to using *hashCode* method. We handle negative hash code case too.

{% highlight scala %} 

bucket(k) = bucket.get(k) match {
   case Some(c) => mergeValue(c, v)
   case None => createCombiner(v)
}

{% endhighlight %} 

Once we have the bucket, then we use createCombiner or mergeValue to run combining for that pair.


### Step 4 : Writing results to local disk

Once we have results for a given split, we are going to write them to the disk.

{% highlight scala %} 

val paths = (0 until numberOfOutputSplit).map {
          i =>
            val path = new File(tempDir, "%d-%d".format(index, i))
            val out = new ObjectOutputStream(new FileOutputStream(path))
            buckets(i).foreach(pair => out.writeObject(pair))
            out.close()
            path.getName
}

{% endhighlight %} 

### Step 5 : Serve the local files using a HTTP server

Once we write the files to disk, we start a http server which serves these output files to reduce tasks.

{% highlight scala %} 

val httpServer = new HttpServer(tempDir)
httpServer.start()
(index, httpServer.uri)

{% endhighlight %} 

### Step 6: Run forEach tasks

{% highlight scala %} 
  
val mapping = scheduler.runTasks(tasks: _*).map(_.get)

{% endhighlight %} 

### Step 7 : Map the splits to uri

After map side combiners are completed, we are going to create a hashmap which going to contain uri's of http servers and the different splits they contain.

{% highlight scala %} 

 val splitsByUri = new mutable.HashMap[String, mutable.ArrayBuffer[Int]]()

 for ((index, uri) <- mapping) {
      splitsByUri.getOrElseUpdate(uri, ArrayBuffer[Int]()) += index
 }

{% endhighlight %} 


### Step 8 : Generate reduce task for output split
We are going to create reduce tasks as specified by *numberOfOutputSplit*.

{% highlight scala %} 

val reduceTasks = (0 until numberOfOutputSplit).map(index => reduceTask(index,splitsByUri)).toList

{% endhighlight %} 

### Step 9: Implementation of reduce task
The following is the implementation of reduce.

{% highlight scala %} 
  def reduceTask(index: Int, splitsByUri: mutable.HashMap[String, ArrayBuffer[Int]]) = {
      new FunctionTask[HashMap[K, C]](() => {
        val combiners = new HashMap[K, C]
        for ((uri, splits) <- Utils.shuffle(splitsByUri)) {
          for (split <- splits) {
            val url = "%s/%d-%d".format(uri,split,index)
            val inputStream = new ObjectInputStream(new URL(url).openStream())
            try {
              while (true) {
                val (k, c) = inputStream.readObject().asInstanceOf[(K, C)]
                combiners(k) = combiners.get(k) match {
                  case Some(oldC) => mergeCombiners(oldC, c)
                  case None => c
                }
              }
            } catch {
              case e: EOFException => {}
            }
          }}
          combiners
        })
    }
{% endhighlight %} 

reduce task just downloads the uri's specific to the bucket and runs *mergeCombiners* to get the final reduce value for that bucket.

### Step 10 : Run the reduce tasks and return result

{% highlight scala %} 
 val reducedValues = scheduler.runTasks(reduceTasks:_*)
 reducedValues.map(_.get).flatten.toList
{% endhighlight %} 

runs the above created reduce tasks and returns the result as a list.


## Using the shuffle to implement hadoop word count

The following code uses our shuffle to implement word count. Here we do the mapping inline , only implement combiner and reducer functionality.

{% highlight scala %} 
  val paragraph = List("hello how are you how are you i am fine let's go till end how are you","hello how are you u asked me")

    
val twoSplits = paragraph.flatMap(value => value.split(" ")).map(value => (value, 1)).splitAt(10)
val finalList = List[List[(String, Int)]](twoSplits._1, twoSplits._2)

val createCombiner = (value: Int) => value
val mergeValue = (combiner: Int, value: Int) => combiner + value
val mergeCombiner = (c1: Int, c2: Int) => c1 + c2

val result = new LocalShuffle().compute(finalList,createCombiner,
mergeValue,mergeCombiner,2,scheduler)
println("result is" +result)

{% endhighlight %} 

## Building  and Running

Download source code from [github](https://github.com/phatak-dev/blog/tree/master/code/MesosShuffle).

Refer to [this](/distributing-third-party-libraries-in-mesos/#running) post for building and running instructions.




