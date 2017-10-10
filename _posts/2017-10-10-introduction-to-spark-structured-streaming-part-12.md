---
layout: post
title: "Introduction to Spark Structured Streaming - Part 12 : Watermarks"
date : 2017-10-10
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

This is the twelfth post in the series. In this post, we discuss about watermarks. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Unbounded State for Late Events

In last [post](/introduction-to-spark-structured-streaming-part-11/) we discussed about the event time abstraction. By default, spark remembers all the windows forever and waits for the late events forever. This may be good for the small volume data, but as volume increases keeping around all the state becomes problematic. As the time goes, the number of windows increases and resource usage will shoot upward. So we need a mechanism which allows us to control state in bounded way. Watermarks are one of those mechanisms.

## Watermarks
Watermarks is a threshold , which defines the how long we wait for the late events. Combining watermarks with automatic source time tracking ( event time) spark can automatically drop the state and keep it in bounded way. When you enable watermarks, for a specific window starting at time T, spark will maintain state and allow late data to update the state until **(max event time seen by the engine - late threshold > T)**. In other words, late data within the threshold will be aggregated, but data later than the threshold will be dropped.

## Analysing Stock Data 

In this example, we analyse the stock data using event time abstraction as last post. As each stock event comes with a timestamp, we can use that time to define aggregations. We will be using socket stream as our source.

### Specifying the Watermark

The below code is to define watermark on stream.

{% highlight scala %}
val windowedCount = stockDs
  .withWatermark("time", "500 milliseconds")
  .groupBy(
    window($"time", "10 seconds")
  )
  .sum("value")

{% endhighlight %}

In above example, whenever we create window on event time, we can specify the watermark with *withWatermark*. In our example, we specified watermark as *500 
milliseconds*. So spark will wait for that time for late events. Make sure to use update output mode. Complete mode doesn't honor watermarks.

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/WaterMarkExample.scala).

## Running the Example

Enter the below records in socket console. These are records for AAPL with time stamps. As we are using update output mode, result will only show changed windows.

### First Event

The first records is for time Wed, 27 Apr 2016 11:34:22 GMT.

{% highlight text %}
1461756862000,"aapl",500.0
{%endhighlight %}

Spark outputs below results which indicates start of window 

{% highlight text %}

-------------------------------------------
Batch: 0
-------------------------------------------
+---------------------------------------------+----------+
|window                                       |sum(value)|
+---------------------------------------------+----------+
|[2016-04-27 17:04:20.0,2016-04-27 17:04:30.0]|500.0     |
+---------------------------------------------+----------+

{% endhighlight %}

### Event after 5 seconds

Now we send the next record, which is after 5 seconds. This signifies to spark that, 5 seconds have passed in source. So spark will be updating the same window. This event is for time Wed, 27 Apr 2016 11:34:27 GMT 

{% highlight text %}
1461756867001,"aapl",600.0
{%endhighlight %}

The output of the spark will be as below. You can observe from output, spark is updating same window.

{% highlight text %}

-------------------------------------------
Batch: 1
-------------------------------------------
+---------------------------------------------+----------+
|window                                       |sum(value)|
+---------------------------------------------+----------+
|[2016-04-27 17:04:20.0,2016-04-27 17:04:30.0]|1100.0    |
+---------------------------------------------+----------+

{% endhighlight %}

### Event after 11 seconds

Now we send another event, which is after 6 seconds from this time. Now spark understands 11 seconds have been passed. This event is for Wed, 27 Apr 2016 11:34:32 GMT

{% highlight text %}
1461756872000,"aapl",400.0
{%endhighlight %}

Now spark completes the first window and add the above event to next window.

{% highlight text %}
-------------------------------------------
Batch: 2
-------------------------------------------
+---------------------------------------------+----------+
|window                                       |sum(value)|
+---------------------------------------------+----------+
|[2016-04-27 17:04:30.0,2016-04-27 17:04:40.0]|400.0     |
+---------------------------------------------+----------+
{%endhighlight %}

### Late Event

Let's say we get an event which got delayed. It's an event is for Wed, 27 Apr 2016 11:34:27 which is 5 seconds before the last event.

{% highlight text %}
1461756867001,"aapl",200.0
{%endhighlight %}

If you observe the spark result now, there are no updated window. This signifies that late event is dropped.

{% highlight text %}
-------------------------------------------
Batch: 3
-------------------------------------------
+---------------------------------------------+----------+
|window                                       |sum(value)|
+---------------------------------------------+----------+
+---------------------------------------------+----------+

{% endhighlight %}


## Conclusion

In this post we understood how watermarks help us to define bounded state and handle late events efficiently. 
