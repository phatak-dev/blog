---
layout: post
title: "Introduction to Spark Structured Streaming - Part 11 : Event Time"
date : 2017-09-12
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

This is the eleventh post in the series. In this post, we discuss about event time abstraction. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Event Time Abstraction

In last [post](/introduction-to-spark-structured-streaming-part-10/) we discussed about the ingestion time abstraction. In this post, we will discuss about event time abstraction.

Event time, as name suggests, is the time when event is generated. Normally the data which we collect from sources like sensors, logs have a time embedded in them. This time signifies when a given event is generated at the source. Structured streaming allows us to work with this time, with event time support in the framework level. 

Whenever we talk about time, normally we need to address two different components of it. They are

 * When a given event is generated?

 * How long ago a given event is generated?

The first question is about where a given event fits in event time line. The second question deals about tracking  of passing of time. Answering these two questions helps us to define how event time works in structured streaming.

## Analysing Stock Data using Event Time

In this example, we analyse the stock data using event time abstraction. As each stock event comes with a timestamp, we can use that time to define aggregations. We will be using socket stream as our source.

### Reading Data From Socket

The below code is to read the data from socket.

{% highlight scala %}
val socketStreamDs = sparkSession.readStream
  .format("socket")
  .option("host", "localhost")
  .option("port", 50050)
  .load()
  .as[String]

{% endhighlight %}


### Extracting Time from Stream

{% highlight scala %}
case class Stock(time:Long, symbol:String,value:Double)

val stockDs = socketStreamDs.map(value => {
  val columns = value.split(",")
  Stock(new Timestamp(columns(0).toLong), columns(1), columns(2).toDouble)
})
{% endhighlight %}

In above code, we declared a model which tracks stock price in a given point of time. The first field is timestamp of stock, then second is the symbol and third one is the value of the stock at that point of given time. Normally stock price analysis depends on event time rather than processing time as they want to correlate the change in stock prices when they happened in the market rather than they ended up in our processing engine.

So once we define the model, we convert of string network stream into model which we want to use. So the time in the model, signifies when this stock reading is done.

### Defining Window on Event Time

{% highlight scala %}
    val windowedCount = stockDs
      .groupBy(
        window($"time", "10 seconds")
      )
      .sum("value")
{% endhighlight %}

The above code defines the window which aggregates the stock value for last 10 seconds.

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/EventTimeExample.scala).

## Passing of time

Whenever we say, we want to calculate max of a stock in last 10 seconds, how spark knows all the records for that 10 seconds are reached? It's the way of saying how spark knows the passage of time in the source? We cannot use system clocks because there will be delay between these two systems.

As we discussed in previous post, watermarks are the solution to this problem. Watermark signify the passage of time in source which will help to spark to understand flow in time. 

By default spark uses the window time column to track the passing of time with option of infinite delay. So in this model all windows are remembered as state,so that even if the event delays long time, spark will calculate the right value.But this creates an issue.As time goes number of windows increases and they use more and more resources. We can limit the state, number of windows to be remembered, using custom watermarks. We will discuss more about it in next post.

## Running the Example

Enter the below records in socket console. These are records for AAPL with time stamps. 

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
|[2016-04-27 17:04:20.0,2016-04-27 17:04:30.0]|1100.0    |
|[2016-04-27 17:04:30.0,2016-04-27 17:04:40.0]|400.0     |
+---------------------------------------------+----------+
{%endhighlight %}

### Late Event

Let's say we get an event which got delayed. It's an event is for Wed, 27 Apr 2016 11:34:27 which is 5 seconds before the last event.

{% highlight text %}
1461756867001,"aapl",200.0
{%endhighlight %}

If you observe the spark result now, you can observe that it's added it to right window.

{% highlight text %}
-------------------------------------------
Batch: 3
-------------------------------------------
+---------------------------------------------+----------+
|window                                       |sum(value)|
+---------------------------------------------+----------+
|[2016-04-27 17:04:20.0,2016-04-27 17:04:30.0]|1300.0    |
|[2016-04-27 17:04:30.0,2016-04-27 17:04:40.0]|400.0     |
+---------------------------------------------+----------+

{% endhighlight %}


## Conclusion

In this post we understood how to use event time abstraction in structured streaming.
