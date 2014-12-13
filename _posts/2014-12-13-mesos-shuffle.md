---           
layout: post
title: "Implement shuffle in Mesos"
date : 2014-12-13
categories: mesos scala
---

Many times of the in a distributed systems, we will be having need of the shuffle which allows to implements things like grouping, joins etc. In heart of map reduce everything is driven by the shuffle only.

This post talks how to implement shuffle on mesos.

This post extends code discussed in this [post](). If you are new to mesos please go through that post before continuing.

tl;dr Access the complete code here.

### What is shuffle?
Shuffle is an operation where result produced at one machine moved to another machine to combine the results. The operations like reduceByKey, join in Hadoop or Spark requires shuffling.

The following are the steps to implement shuffling on mesos.

### Shuffle signature

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

 * mergeValue - for every value in split, mergeValue will be used to update the combine

 * mergeCombiners - its a reduce side job of merging two different combiners

 numberOfOutputSplit allows us to control number of reduce tasks and final parameter scheduler allow us as to access the context.


### Local file based shuffle implementation

The following code explains the implementation of shuffle using local file system and http server.

#### Step 1 : Implement Shuffle trait 
{% highlight scala %} 

  class LocalShuffle[K, V, C] extends Shuffle[K, V, C] with Serializable {
  override def compute(rows: List[List[(K, V)]], createCombiner: (V) => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numberOfOutputSplit: Int, scheduler: TaskScheduler):
  List[(K, C)] = {

{% endhighlight %}

The rows variable simulating a in memory collection with multiple rows. I am using a in memory collection to keep things simple. You can replace it with RDD or a hadoop file data.

### Step2 : Number each split
{% highlight scala %} 
val numberedList = rows.zipWithIndex
{% endhighlight %} 

we number each split with index so that we can use it to identify it when we shuffle.

### Step 3: Create local combiner tasks
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

Then we create buckets using hashParitioner

{% highlight scala %} 

val buckets = Array.tabulate(numberOfOutputSplit)(_ => new HashMap[K, C])
for ((k, v) <- iterator) {
var bucketId = k.hashCode() % numberOfOutputSplit
if (bucketId < 0) {
  bucketId += numberOfOutputSplit
}          
val bucket = buckets(bucketId)
{% endhighlight %} 

for each value, we determine which bucket it belongs to using *hashCode* method. We also handle negative hashCode condition.


{% highlight scala %} 

bucket(k) = bucket.get(k) match {
    case Some(c) => mergeValue(c, v)
   case None => createCombiner(v)
}

{% endhighlight %} 

Once we have the bucket, then we use createCombiner or mergeValue to run combining for that pair.


### Step 4 : Writing results to local disk

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

## Step 5 : Serve the local file using a HTTP server

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

{% highlight scala %} 

 val splitsByUri = new mutable.HashMap[String, mutable.ArrayBuffer[Int]]()

 for ((index, uri) <- mapping) {
      splitsByUri.getOrElseUpdate(uri, ArrayBuffer[Int]()) += index
 }

{% endhighlight %} 


### Step 8 : Generate reduce task for each map bucket

{% highlight scala %} 

val reduceTasks = (0 until numberOfOutputSplit).map(index => reduceTask(index,splitsByUri)).toList

{% endhighlight %} 

### Step 9: Implementation of reduce task

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

reduce task just download the uri's specific to the bucket and run *mergeCombiners* to get the final reduce value for that bucket.

### Step 10 : Run the reduce tasks and return result

{% highlight scala %} 
 val reducedValues = scheduler.runTasks(reduceTasks:_*)
 reducedValues.map(_.get).flatten.toList
{% endhighlight %} 

runs the above created reduce tasks and returns the result as a list.


## Using the shuffle to implement hadoop word count

The following code uses our shuffle to implement word count. Here we do the mapping inline and only use combiner and reducer functionality.

{% highlight scala %} 
  val paragraph = List("hello how are you how are you i am fine let's go till end how are you","hello how are you u asked me")


    
val twoSplits = paragraph.flatMap(value => value.split(" ")).map(value => (value, 1)).splitAt(10)
val finalList = List[List[(String, Int)]](twoSplits._1, twoSplits._2)

val createCombiner = (value: Int) => value
val mergeValue = (combiner: Int, value: Int) => combiner + value
val mergeCombiner = (c1: Int, c2: Int) => c1 + c2

val result = new LocalShuffle().compute(finalList,createCombiner,mergeValue,mergeCombiner,2,scheduler)
println("result is" +result)

{% endhighlight %} 






