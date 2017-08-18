---
layout: post
title: "Introduction to Spark Structured Streaming - Part 7 : Checkpointing State"
date : 2017-08-18
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

This is the seventh post in the series. In this post, we discuss about checkpointing the state for recovery. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Need of Checkpointing

Many stream processing applications are stateful in nature. With built in support for stateful aggregations in structured streaming, it has become easier to build them.But whenever we build stateful applications, we need to be careful about preserving the state across restarts or failures. We don't want to loose our valuable state if processing units went down for some reason.

Checkpointing is one of the mechanism to preserve the state of the application across the restart of driver or executors. In spark, checkpointing is achieved using writing the state of the query to a HDFS folder. In this post, we will explore how to enable checkpointing and use it.

## Source Support

To enable the checkpointing, the source from which we read the data need to support it. Not all the sources in structured streaming support checkpointing. One of the example of is socket streams. Checkpointing sometime may need to replay some of the data from source in order to recover the state. In those cases we need a source which supports that kind of functionality. Socket doesn't support that.

Sources like file stream, kafka have ability to replay messages on offset. So they support checkpointing.

## Sales Aggregation

For this example, we take sales data from a file stream and do some aggregation on it. The below is the code

{% highlight scala %}

val schema = StructType(
  Array(StructField("transactionId", StringType),
        StructField("customerId", StringType),
        StructField("itemId", StringType),
        StructField("amountPaid", DoubleType)))

//create stream from folder
val fileStreamDf = sparkSession.readStream
  .option("header", "true")
  .schema(schema)
  .csv("/tmp/input")

val countDs = fileStreamDf.groupBy("customerId").sum("amountPaid")
 
{% endhighlight %}

In this example, we are aggregating the amount for a given customer. We like to checkpoint this state. It makes sure that amount doesn't start from zero whenever there is failure.

## Check Point Directory in Query

In structured streaming, checkpoint directory is specific to a query. We can have multiple queries writing state to multiple different directories. This makes easy
to scale checkpointing across multiple different processing.

{% highlight scala %}

val query =
  countDs.writeStream
    .format("console")
    .option("checkPoint", "/tmp/checkpoint")
    .outputMode(OutputMode.Complete())

{%endhighlight %}

In above code, we specify the checkpointing directory using *checkPoint* option on *writeStream*. This makes sure that the state is written the directory.

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/RecoverableAggregation.scala).

## Recovering State

Whenever there is checkpoint directory attached to query, spark goes through the content of the directory before it accepts any new data. This makes sure that spark recovers the old state before it starts processing new data. So whenever there is restart, spark first recovers the old state and then start processing new data from the stream.

## Running

Input the files to */tmp/input* in local or hdfs folder. After sometime, restart the driver. You should observe the recovery of state.

## Conclusion

With checkpointing support, we can build robust stateful stream processing applications in structured streaming.
