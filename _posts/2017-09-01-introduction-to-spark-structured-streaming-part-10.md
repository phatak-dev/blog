---
layout: post
title: "Introduction to Spark Structured Streaming - Part 10 : Ingestion Time"
date : 2017-09-07
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

This is the tenth post in the series. In this post, we discuss about ingestion time abstraction. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Ingestion Time Abstraction

In last [post](/introduction-to-spark-structured-streaming-part-9/) we discussed about the processing time abstraction. In this post, we will discuss about ingestion time abstraction.

Ingestion time, as name indicates, is a time captured at the ingestion of data. Sources like kafka, file stream capture the time of ingestion to provide the ordering guarantees. We can use this time captured at the source as mechanism for the processing data. 

Ingestion time abstraction is useful when each batch of data takes considerable amount to process and we still want to capture the matrices depending upon time of capture. Let's say we have some sensor data coming in and we want to know what happened in last 10 seconds. If our source captures the data as soon as it's sent from sensors, ingestion time will be closer to the real time. For some reason, stream processing took more time than usual. If we have used processing time abstraction, we would have lost some records for our metric calculation. But in the ingestion time abstraction we will still get all the right records.

In this post, we will discuss how to define a window on ingestion time with simple wordcount example.

## Windowed WordCount using Ingestion Time

The below are the steps to implement windowed word count using ingestion time abstraction.

### Reading Data From Socket with Ingestion Time

The below code is to read the data from socket.

{% highlight scala %}
val socketStreamDf = sparkSession.readStream
  .format("socket")
  .option("host", "localhost")
  .option("port", 50050)
  .option("includeTimestamp", true)
  .load()
{% endhighlight %}

In the above code, we have specified we need to access to ingestion time from source using *includeTimestamp* option. Ingestion time depends on source, as it needs to capture it and expose it.

### Data with Time Column

{% highlight scala %}
val socketDs = socketStreamDf.as[(String, Timestamp)]
{% endhighlight %}

As we specified the we wanted access to ingestion time, it's no more single column. We will get two columns, one indicating the value and one indicating the time at which it's ingested.

### Extracting Words with Delay

The below code extracts the words from socket and creates words with timestamp.

{% highlight scala %}
val wordsDs = socketDs
  .flatMap(line => line._1.split(" ").map(word => {
     Thread.sleep(15000)
    (word, line._2)
  }))
  .toDF("word", "timestamp")

{% endhighlight %}

In the code, we introduced a sleep of *15 seconds* to simulate the long processing. This means for every records it takes minimum 15 seconds.

### Defining Window

Once we have words, we define a tumbling window which aggregates data for last 15 seconds.

{% highlight scala %}
val windowedCount = wordsDs
  .groupBy(
    window($"timestamp", "15 seconds")
  )
  .count()
  .orderBy("window")

{% endhighlight %}

In above code, we define window as part of groupby. It looks exactly same as we saw in processing time example in last post. Only the difference is that this groups the data depending upon when it's ingested whereas earlier example groups the data upon when it's received for processing.

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


You access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/IngestionTimeWindow.scala).

## Output

Let's input the below data to socket

{% highlight text %}
hello world
hello world
{% endhighlight %}

We will observe the below results

{% highlight text %}
-------------------------------------------
Batch: 0
-------------------------------------------
+---------------------------------------------+-----+
|window                                       |count|
+---------------------------------------------+-----+
|[2017-09-07 18:52:45.0,2017-09-07 18:53:00.0]|2    |
+---------------------------------------------+-----+

-------------------------------------------
Batch: 1
-------------------------------------------
+---------------------------------------------+-----+
|window                                       |count|
+---------------------------------------------+-----+
|[2017-09-07 18:52:45.0,2017-09-07 18:53:00.0]|4    |
+---------------------------------------------+-----+

{% endhighlight %}

As you can observe from the above output, we have counted both records in same window, even though processing may have taken more than 30 seconds. This shows that spark is using ingested time for grouping than the time at which it has received data.

In upcoming posts, we will discuss about other time abstractions.
