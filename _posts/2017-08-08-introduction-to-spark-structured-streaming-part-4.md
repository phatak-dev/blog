---
layout: post
title: "Introduction to Spark Structured Streaming - Part 4 : Stateless Aggregations"
date : 2017-08-08
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

This is the fourth post in the series. In this post, we discuss about the stateless aggregations. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Stateful Aggregations
In structured streaming, all aggregations are stateful by default. As we saw in last [post](/introduction-to-spark-structured-streaming-part-3) when we do groupBy and count on dataframe, spark remembers the state from the beginning. Also we write the complete output every time when we receive the data as state keeps on changing.

## Need of Stateless Aggregations

Though most of the time scenarios of stream processing need code to be stateful, it comes with the cost of state management and state recovery in the case of failures. So if we are doing simple ETL processing on stream, we may not need state to be kept across the stream. Sometime we want to keep
the state just for small batch and then reset.

For example, let's take wordcount. Let's say we want to count the words for every 5 seconds. Here the aggregation is done on the data which
is collected for last 5 seconds. The state is only kept for those 5 seconds and the forgotten. So in case of failure, we need to recover data only for last 5 seconds. Though this example looks simple, it's applicable to many real world scenarios.

In the following part of the post we will be discussing about how to implement the stateless wordcount using structured streaming API.

## Reading Data and Creating Words

As in last post, we will read from the socket and create words

{% highlight scala %}

val socketStreamDf = sparkSession.readStream
  .format("socket")
  .option("host", "localhost")
  .option("port", 50050)
  .load()

import sparkSession.implicits._
val socketDs = socketStreamDf.as[String]
val wordsDs = socketDs.flatMap(value ⇒ value.split(" "))

{% endhighlight %}

## flatMapGroups API

In last post we used dataframe groupBy and count API's to do word count. But they are stateful. So rather than using those we will use dataset
groupByKey and flatMapGroups API to do the aggregation as below.

{% highlight scala %}

val countDs = wordsDs.groupByKey(value ⇒ value).flatMapGroups{
  case (value, iter) ⇒ Iterator((value, iter.length))
}.toDF("value", "count")

{% endhighlight %}

Rather than using *groupBy* API of dataframe, we use *groupByKey* from the dataset. As we need to group on words, we just pass the same
value to grouping function. If you have complex object, then you can choose which column you want to treat as the key.

flatMapGroups is an aggregation API which applies a function to each group in the dataset. It's only available on grouped dataset. This
function is very similar to *reduceByKey* of RDD world which allows us to do arbitrary aggregation on groups.

In our example, we apply a function for every group of words, we do the count for that group. 

One thing to remember is flatMapGroups is slower than count API. The reason being flatMapGroups doesn't support the partial aggregations which increase shuffle overhead. So use this API only to do small batch aggregations. If you are doing aggregation across the stream, use the stateful operations.

## Specifying the Trigger

As we want to aggregate for every 5 seconds, we need to pass that information to query using trigger API. Trigger API is used to specify the frequency of computation. This separation of frequency from the stream processing is one of the most important part of structured streaming. This separation allows us to be flexible in computing different results in different speed.

{% highlight scala %}

val query =
 countDs.writeStream.format("console").outputMode(OutputMode.Append()).
   trigger(Trigger.ProcessingTime("5 seconds"))

{% endhighlight %}

In the above code, we have specified the trigger using processing time. This analogous to the batch time of DStream API. Also observe that, we have specified output mode as *append*. This means we are doing only batch wise aggregations rather than full stream aggregations.

When you run this example, you will observe that the aggregation will be running on data entered in last 5 seconds.

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/StatelessWordCount.scala).

## Conclusion

You can run stateless aggregations on stream using *flatMapGroups* API.

