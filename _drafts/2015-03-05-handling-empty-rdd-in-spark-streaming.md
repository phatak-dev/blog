---           
layout: post
title: "Handling empty batches in Spark streaming"
date : 2015-03-05
categories: spark
---
Spark streaming works in idea of batches. For given interval, spark streaming generates new batch and run some processing. Each of these batch data is represented as RDD internally. But what happens there is no data? Spark generates a special kind of RDD called *EmptyRDD*.

This empty RDD makes sure that your processing is always called. But having an empty RDD may create issues. For example, let's say you want to save the data of a stream to hdfs you can write

{% highlight scala %}

val ssc = new StreamingContext(args(0),"test",Seconds(10))
val socketStream = ssc.socketTextStream("localhost",50050)
val outputDir = args(1)
socketStream.foreachRDD(rdd => {
  rdd.saveAsTextFile(outputDir)
})
{% endhighlight %}

The above code generates empty files for empty batches. If you have many empty batches, unnecessarily too many folders created. To avoid this, you can check is passed RDD is an empty rdd 

{% highlight scala %}
 if(!rdd.partitions.isEmpty)
   rdd.saveAsTextFile(outputDir)
{% endhighlight %}

The empty rdd has no partitions. Using this logic, we can make sure empty batches don't create issues.