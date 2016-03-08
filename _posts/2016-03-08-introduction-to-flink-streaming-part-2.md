---
layout: post
title: "Introduction to Flink Streaming - Part 2 : Discretization of Stream using Window API"
date : 2016-03-08
categories: scala flink flink-streaming
---
In the last [blog](/introduction-to-flink-streaming-part-1), we discussed about how to do contentious stream processing using flink streaming API. Our wordcount example keeps on updating the counts as and when we received new data. This is good for some of the use cases. But in some use cases we want to aggregate some set of records in a given time interval, in order to keep track of variance over time. In those cases, we need to divide the stream into small batches. This discretization allow us to capture the change in aggregated value overtime. This discretized batches is also known as micro batches.

In this second blog, I will be discussing about how to discretized the stream using flink's window operation.We will be using same word count example in this post also. You can access all the blogs in the series [here](/categories/flink-streaming/). 

**TL;DR** All code is written using Flink's scala API and you can access it on [github](https://github.com/phatak-dev/flink-examples).

## Window in streaming

Window is a mechanism to take a snapshot of the stream. This snapshot can be based on time or other variables. For example, if we create a window for 5 seconds then it will be all the records which arrived in the that time frame. You can define the window based on no of records or other stream specific variables also.

Window allows us to understand change in stream data by taking snapshots in regular intervals. Flink API has wide variety of window support. In this post we are only going to focus on one kind of window known as Tumbling Windows.

## Tumbling Window in Flink Streaming

Tumbling window is one kind of windowing operation which will discretize the stream into non overlapping windows. This means every record in the stream only belongs to one window. This kind of discretization allows observing the change in the stream over fixed intervals. There are other kind of windows supported in the flink which we will discuss in the future posts.

## Windowed Wordcount example

In last blog, we saw how to calculate wordcount using Flink API. In this post, we will be calculating wordcount for every 15 seconds. So in this example, we will be dividing the stream for every 15 seconds. Once those 15 seconds passes, the count will be started from zero. 

This example shows to how to snapshot wordcount for each 15 seconds to analyze the trend over time to say how wordcount have changed. This is not possible when we have continuous updation of the count as in earlier example.

Most of the code to setup and run is same as earlier example. So I am only going to focus on how to add tumbling window to our stream

{% highlight scala %}

val keyValuePair = wordsStream.keyBy(0).timeWindow(Time.seconds(15))

val countStream = keyValuePair.sum(1)

{% endhighlight %}

The above code creates a window using timeWindow API. timeWindow API internally uses tumbling window API to do the windowing operation. In this case, the wordcount will be counted for each 15 seconds and then forgotten.

You can access complete code [here](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/WindowedStreamingWordCount.scala).

## Compared to Spark Streaming API

This section is only applicable to you, if you have done spark streaming before. If you are not familiar with Apache Spark feel free to skip it.

The tumbling windows in Flink are similar to microbatches in Spark. As in spark microbatch, tumbling windows are used for discretizing stream into independent batches.

## References

Introducing Stream Windows in Apache Flink - [https://flink.apache.org/news/2015/12/04/Introducing-windows.html](https://flink.apache.org/news/2015/12/04/Introducing-windows.html).