---
layout: post
title: "Introduction to Flink Streaming - Part 5 : Window API in Flink"
date : 2016-03-14
categories: scala flink flink-streaming
---

In recent years, there is been a lot of discussion on stateful stream processing. When initial open source stream processor like storm came along, stream processing was viewed as the faster batch processing. The API's were more geared towards the stateless ETL pipelines. But as realtime/ stream processing became more and more important having stateful processing became necessary. So all modern stream processing frameworks have varied degree of support to do stateful operations. 

Window is one of the way to define continuous state across the stream. So in the fifth blog of the series, I will be discussing about window support in flink API. You can access all the posts in the series [here](/categories/flink-streaming).

**TL;DR** All code is written using Flink's scala API and you can access it on [github](https://github.com/phatak-dev/flink-examples).

## Window in Streaming

Window is a mechanism to take a snapshot of the stream. This snapshot can be based on time or other variables. For example, if we create a window for 5 seconds then it will be all the records which arrived in the that time frame. You can define the window based on no of records or other stream specific variables also.


## Types of window in Flink

Flink support wide variety of window operations. The different windows supported in flink are

* Time Windows
    * Tumbling Windows
    * Sliding Windows
* Count Windows

## Creating KeyedDataStream

Before we discuss about each of above windows, we need to be aware of one fact. Most of the window operations are encouraged to be used on KeyedDataStream. A KeyedDataStream is a datastream which is partitioned by the key. This partitioning by key allows window to be distributed across machines resulting in good performance. The following code shows how to create a KeyedDataStream from data coming from the socket.

{%highlight scala %}

 val source = env.socketTextStream("localhost",9000)

 val values = source.flatMap(value => value.split("\\s+")).map(value => (value,1))

 val keyValue = values.keyBy(0)

{%endhighlight%}

In above example, we are reading data and creating a stream named *source*. Once we receive the data we are extracting the words using *flatMap* and *map* operators. Once we have a tuple, we are creating a KeyedDataStream using keyBy operation. Once we have a KeyedDataStream, we can start defining the window.

You can also define windows on non keyed stream. But they often result in poor performance. So I will be not discussing them here.

## Tumbling Window 

We have already seen this window on our earlier [post](introduction-to-flink-streaming-part-2). In this section we will be discussing little more. 

A tumbling window is a time based window which tumbles once the window is evaluated. In essence, all the state and records of the window will be purged once the window evaluates. This kind of window is very useful for dividing stream in to multiple discrete batches.

The below code shows how to create a tumbling window

{%highlight scala %}

val tumblingWindow = keyValue.timeWindow(Time.seconds(15))

{%endhighlight%}

To create a tumbling window, we use timeWindow API. In above example, stream will be evaluated for every 15 seconds. Here we are calculating the word counts for every 15 seconds.

## Sliding window

Sliding window is one of most known windowing in streaming. They usually used for keeping running count for a distant past. They allow us to answer questions like "what is word count for last 5 seconds"?.

Sliding window is also a time based window. The only difference with tumbling window is, an element can belong to multiple windows in sliding window compared to only one window in case of tumbling window. So sliding window normally creates overlapping windows compared to discrete windows in tumbling window.

{%highlight scala %}
val slidingWindow = keyValue.timeWindow(Time.seconds(15),Time.seconds(15))
{%endhighlight%}

In above example, we are calculating wordcount for last 15 seconds, in each 5 second interval. 

## Count based windows

The above two windows were based on time. But in flink we can define windows on other properties also. One of them is count windows. As name suggests, count window are evaluated when no of records received hits the threshold.

{%highlight scala %}
  val countWindow = keyValue.countWindow(5)
{%endhighlight%}

The above code defines a count window which fires for after every 5 records. Please note that as the stream is keyed, for each key it tracks no records not across the multiple keys.

You can access complete code for all the three window [here](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/WindowExample.scala).

## Running with examples

Window operations are hard to wrap mind around without examples. So in the next few sections we are going to discuss how to run these examples with sample data and understand their behavior. You can run this example from IDE or in local mode. But before running you need to make sure comment out the non necessary outputs. For example, when you are running tumbling window make sure you comment out the below lines in the code.

{%highlight scala %}
slidingWindow.sum(1).name("slidingwindow").print()
countWindow.sum(1).name("count window").print()
{%endhighlight%}

We comment out these lines just to make sure they don't interfere with our output. Follow the same for other two also.

All the input is entered in stadin of nc command. You can start socket for the program using below command

{%highlight sh %}
 nc -lk localhost 9000
{%endhighlight%}

## Running tumbling window

We are going to run tumbling window in this section. Enter the below lines one by one with in 15 seconds on nc stdin.

{%highlight text %}
hello how are you
hello who are you
{%endhighlight%}

You will observe the below result once 15 seconds are done.

{%highlight text %}
(hello,2)
(you,2)
(are,2)
(who,1)
(how,1)
{%endhighlight%}

If you wait for sometime and enter same lines you will observe that the count is reset and you get same result as above. So this shows how tumbling window discretized the stream.

## Running sliding window

In this section we are going to run sliding window. 

If you send the same lines as above in the beginning you will see the result is printed thrice. The reason being, we created a window for 15 seconds which is three times of the 5 seconds. So when window evaluates every 5 seconds, it recalculates the same result. You can add more rows in between to observe how count changes.


## Running count window
In this section, we are going to run count based windows.

If you send those two lines, nothing will be printed. The reason is no key has a count of 5.

Enter the same lines again. Nothing will be printed. Again we have not yet hit the threshold.

Enter the first line again. Now you will see the below result.

{%highlight text %}
(are,5)
(hello,5)
(you,5)
{%endhighlight%}

So as soon as the the count hits 5 window will be triggered. So from the example it's apparent the count is kept for a key not across keys.

So window API in flink is very powerful compared to other frameworks. These constructs should allow you to express your application logic elegantly and efficiently.

## Compared to Spark Streaming API

This section is only applicable to you, if you have done spark streaming before. If you are not familiar with Apache Spark feel free to skip it.

You can simulate the tumbling window using sliding window operation available in spark. If both window duration and sliding duration is same, you get tumbling window. 

Sliding windows are supported as first class citizens. 

Count based window is not supported in spark streaming. As windowing system of spark is tightly coupled with time, no builtin support for other types of window are there as of now.

## References

Introducing Stream Windows in Apache Flink - [https://flink.apache.org/news/2015/12/04/Introducing-windows.html](https://flink.apache.org/news/2015/12/04/Introducing-windows.html).










