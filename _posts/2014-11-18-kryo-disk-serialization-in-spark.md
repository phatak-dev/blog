---           
layout: post
title: "Kryo disk serialization in Spark"
date : 18-11-2014
categories: spark scala
---

In apache spark, it's [advised](http://spark.apache.org/docs/latest/tuning.html#data-serialization) to use the kryo serialization over java serialization for big data applications. Kryo has less memory footprint compared to java serialization which becomes very important when you are shuffling and caching large amount of data.

Though kryo is supported for RDD caching and shuffling, it's not natively supported to serialize to the disk. Both methods, *saveAsObjectFile* on RDD and *objectFile* method on SparkContext supports only java serialization. 

As number of custom data types increases it's tedious to support multiple serialization's. So it will be nice if we can use  kryo serialization everywhere.

In this post, we are going to discuss about how to use kryo serialization to save and read from the disk.

tl;dr You can access complete example code [here](https://github.com/phatak-dev/blog/tree/master/code/kryoexample).

## Write
Normally we use *rdd.saveAsObjectFile* api to save the serialized object's into the disk. The following code shows how you can write your own *saveAsObjectFile* method which saves the objects in kryo serialization format.

{% highlight scala %}
def saveAsObjectFile[T: ClassTag](rdd: RDD[T], path: String) 
{% endhighlight %} 

We take rdd that to be written and output path as input parameters.

{% highlight scala %}
val kryoSerializer = new KryoSerializer(rdd.context.getConf)     
{% endhighlight %} 
*KryoSerializer* is a helper class provided by the spark to deal with kryo. We create a single instance of KryoSerializer which configures the required buffer sizes provided in the configuration.

{% highlight scala %}
 rdd.mapPartitions(iter => iter.grouped(10)
      .map(_.toArray))
      .map(splitArray => {}
{% endhighlight %} 

Every objectFile is saved as HDFS sequence files. So we loop over each of the rdd split and then convert those split into byte arrays.

{% highlight scala %}
val kryo = kryoSerializer.newKryo()
{% endhighlight %} 

For every *splitArray*, first we create an instance of kryo. kryo instances are not thread safe. That's why we create one for each map operation. When we call *kryoSerializer.newKryo()* it creates a new instance of kryo and also it calls our custom [registrator](http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.serializer.KryoRegistrator) if any.

{% highlight scala %}
//create output stream and plug it to the kryo output
val bao = new ByteArrayOutputStream()
val output = kryoSerializer.newKryoOutput()
output.setOutputStream(bao)
kryo.writeClassAndObject(output, splitArray)
output.close()
{% endhighlight %} 

Once we have the kryo instance, we create kryo output. Then we write class information and object to that output.

{% highlight scala %}
  val byteWritable = new BytesWritable(bao.toByteArray)
      (NullWritable.get(), byteWritable)
    }).saveAsSequenceFile(path)
{% endhighlight %} 

Once we have the byte representation from the kryo, we wrap that bytearray inside the BytesWritable and save as Sequence file.

So with few lines of code, now we can save our kryo objects into the disk.

You can access the complete code [here](https://github.com/phatak-dev/blog/tree/master/code/kryoexample/src/main/scala/com/madhu/spark/kryo/KryoExample.scala).

## Read

It's not enough if you are able to write to disk. You should be also able to create RDD from this data. Normally we use *objectFile* api on sparkContext to read from disk. Here we are going to write our own *objectFile* api to read kryo object files.


{% highlight scala %}
def objectFile[T](sc: SparkContext, path: String, minPartitions: Int = 1)(implicit ct: ClassTag[T]) = {
    val kryoSerializer = new KryoSerializer(sc.getConf)
    sc.sequenceFile(path, classOf[NullWritable], classOf[BytesWritable],
       minPartitions)
       .flatMap(x => {
       val kryo = kryoSerializer.newKryo()
       val input = new Input()
       input.setBuffer(x._2.getBytes)
       val data = kryo.readClassAndObject(input)
       val dataObject = data.asInstanceOf[Array[T]]
       dataObject
    })
  }
{% endhighlight %} 

Most of the steps are same as writing, only difference being we use input rather than using output. We read the bytes from BytesWritable and deserialize using *readClassAndObject* api.


## Example 

The following example uses above two methods to serialize and deserialize a custom object named Person.

{% highlight scala %}
 // user defined class that need to serialized
  class Person(val name: String)

 def main(args: Array[String]) {

    if (args.length < 1) {
      println("Please provide output path")
      return
    }
    val outputPath = args(0)

    val conf = new SparkConf().setMaster("local").setAppName("kryoexample")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sc = new SparkContext(conf)

    //create some dummy data
    val personList = 1 to 10000 map (value => new Person(value + ""))
    val personRDD = sc.makeRDD(personList)

    saveAsObjectFile(personRDD, outputPath)
    val rdd = objectFile[Person](sc, outputPath)
    println(rdd.map(person => person.name).collect().toList)
  }

{% endhighlight %} 

So if you are using kryo serialization in your project, now you can same serialization for saving into the disk also.



