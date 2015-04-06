---           
layout: post
title: "Handling empty batches in Spark streaming"
date : 2015-04-06
categories: spark scala
---
Spark streaming is a near real time tiny batch processing system. For given interval, spark streaming generates new batch and runs some processing. Each of these batch data is represented as RDD. But what happens there is no data for a given batch? Spark generates a special kind of RDD called *EmptyRDD*.

This empty RDD makes sure that processing is consistent across multiple batches. But having an empty RDD sometimes may create some issues. For example, let's say you want to save the data of a stream to HDFS. 

{% highlight scala %}

val ssc = new StreamingContext(args(0),"test",Seconds(10))
val socketStream = ssc.socketTextStream("localhost",50050)
val outputDir = args(1)
socketStream.foreachRDD(rdd => {
  rdd.saveAsTextFile(outputDir)
})
{% endhighlight %}

The above code generates empty files for empty batches. If you have many empty batches, unnecessarily too many empty folders will be created. 

We can avoid this by checking, is a given RDD is empty RDD. 

{% highlight scala %}
 if(!rdd.partitions.isEmpty)
   rdd.saveAsTextFile(outputDir)
{% endhighlight %}

The empty rdd has no partitions. Using this logic, we check for partition array of RDD. If partition array is empty, then its an EmptyRDD. This way we can avoid saving empty batches to HDFS.