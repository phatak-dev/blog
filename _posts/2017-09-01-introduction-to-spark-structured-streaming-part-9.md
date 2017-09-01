---
layout: post
title: "Introduction to Spark Structured Streaming - Part 9 : Processing Time Window"
date : 2017-09-01
categories: scala spark introduction-structured-streaming
---
Structured Streaming is a new streaming API, introduced in spark 2.0, rethinks stream processing in spark land. It models stream
as an infinite table, rather than discrete collection of data. It's a radical departure from models of other stream processing frameworks like
storm, beam, flink etc. Structured Streaming is the first API to build stream processing on top of SQL engine.

Structured Streaming was in alpha in 2.0 and 2.1. But with release 2.2 it has hit stable status. In next few releases,
it's going to be de facto way of doing stream processing in spark. So it will be right time to make ourselves familiarise
with this new API.

In this series of posts, I will be discussing about the different aspects of the structured streaming API. I will be discussing about
new API's, patterns and abstractions to solve common stream processing tasks. 

This is the ninth post in the series. In this post, we discuss about processing time abstraction. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Processing Time Abstraction

In last [post](/introduction-to-spark-structured-streaming-part-8/) we discussed about the different time abstractions. One of those abstractions were processing time. As name signifies, processing time is the time tracked by processing engine regarding when data was arrived for processing. This is same as time definition of older DStream API. In this abstraction of time, time passed is signified by the central clock maintained at the driver.

In this post, we will discuss how to define a window on processing time. This allows us to see how we can mimic the DStream window API functionality in structured streaming API.

## Windowed WordCount using Processing Time

The below are the steps to implement windowed word count using processing time abstraction.

### Reading Data From Socket

The below code is to read the data from socket.

{% highlight scala %}

val socketStreamDf = sparkSession.readStream
  .format("socket")
  .option("host", "localhost")
  .option("port", 50050)
  .load()
 
{% endhighlight %}

### Adding the Time Column

To define a window in structured streaming, we need to have a column in dataframe of the type *Timestamp*. As we are working with processing time,
we will use *current_timestamp()* function of spark SQL to add processing time to our data. 

{% highlight scala %}

val currentTimeDf = socketStreamDf.withColumn("processingTime",current_timestamp())

{% endhighlight %}

In above code, we are adding a column named *processingTime* which captures the current time of the processing engine.

### Extracting Words

The below code extracts the words from socket and creates words with timestamp.

{% highlight scala %}

import sparkSession.implicits._
val socketDs = currentTimeDf.as[(String, Timestamp)]
val wordsDs = socketDs
  .flatMap(line => line._1.split(" ").map(word => (word, line._2)))
  .toDF("word", "processingTime")

{% endhighlight %}

### Defining Window

Once we have words, we define a tumbling window which aggregates data for last 15 seconds.

{% highlight scala %}

val windowedCount = wordsDs
  .groupBy(
    window($"processingTime", "15 seconds")
  )
  .count()
  .orderBy("window")

{% endhighlight %}

In above code, we define window as part of groupby. We group the records that we received in last 15 seconds. 

Window API takes below parameters

  * Time Column - Name of the time column. In our example it's *processingTime*.

  * Window Time - How long is the window. In our example it's *15 seconds* .
  
  * Slide Time - An optional parameter to specify the sliding time. As we are implementing tumbling window, we have skipped it.

Once we do groupBy, we do the count and sort by *window* to observe the results.

### Query

Once we have defined the window, we can setup the execution using query.

{% highlight scala %}

val query =
  windowedCount.writeStream
    .format("console")
    .option("truncate","false")
    .outputMode(OutputMode.Complete())

query.start().awaitTermination()
 
{% endhighlight %}


You access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/ProcessingTimeWindow.scala).

## Output

When we run the example, we will observe the below results

{% highlight text %}

+---------------------------------------------+-----+
|window                                       |count|
+---------------------------------------------+-----+
|[2017-09-01 12:44:00.0,2017-09-01 12:44:15.0]|2    |
|[2017-09-01 12:44:15.0,2017-09-01 12:44:30.0]|8    |
+---------------------------------------------+-----+

{% endhighlight %}

As you can observe from the above output, we have count for each fifteen second interval. This makes sure that our window function is working.

In upcoming posts, we will discuss about other time abstractions.
