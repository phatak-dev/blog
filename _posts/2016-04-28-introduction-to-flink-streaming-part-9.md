---
layout: post
title: "Introduction to Flink Streaming - Part 9 : Event Time in Flink"
date : 2016-04-28
categories: scala flink flink-streaming
---
In the last post in our flink series , we discussed about different abstractions of time supported in flink. In this we are going to discuss about how to work with these different abstractions. I will be discussing about event time abstraction specifically as it will be pretty new to users who are coming from other stream processing systems.  You can find all the other posts in the series [here](/categories/flink-streaming).


## Event time in Flink

Event time, as name suggests, is the time when event is generated. Normally the data which we collect from sources like sensors, logs have a time embedded in them. This time signifies when a given event is generated at the source. Flink allows us to work with this time, with event time support in the framework level. 

Whenever we talk about time, normally we need to address two different components of it. They are

 * When a given event is generated?

 * How long ago a given event is generated?

 The first question is about where a given event fits in event time line. The second question deals about tracking  of passing of time. Answering these two questions helps us to define how event time works in Flink.


## Event time characteristic 

Before we start answering above questions, we need to understand how to tell to flink that we want to use event time abstraction. Flink uses processing time as default time abstraction. We can change it using below code.

{% highlight scala %}
 env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
{%endhighlight %}
 
In above line, we are setting in environment that we are going to use event time as our time characteristic.

## When an event is generated?

Once we have, set the time as event, we need to now say when a given event is occurred. There are few ways to do in flink. One of the easiest way is extract the time stamp embedded in the event itself.

{% highlight scala %}
 
  case class Stock(time:Long, symbol:String,value:Double)

  val source = env.socketTextStream("localhost",9000)
  
  val parsedStream = source.map(value => {
      val columns = value.split(",")
      Stock(columns(0).toLong, columns(1),columns(2).toDouble)
   })

{% endhighlight %}

In above code, we declared a model which tracks stock price in a given point of time. The first field is timestamp of stock, then second is the symbol and third one is the value of the stock at that point of given time. Normally stock price analysis depends on event time rather than processing time as they want to correlate the change in stock prices when they happened in the market rather than they ended up in our processing engine.

So once we define the model, we convert of string network stream into model which we want to use. So the time in the model, signifies when this stock reading is done.

## Passing of time

Whenever we say, we want to calculate max of a stock in last 5 seconds, how flink knows all the records for that 5 seconds are reached? It's the way of saying how flink knows the passage of time in the source? We cannot use system clocks because there will be delay between these two systems.

As we discussed in previous post, watermarks are the solution to this problem. Watermark signify the passage of time in source which will help to flink to understand flow in time. One of the easiest way of water marking is called ascending time. The below code show how to use ascending watermarks.

{% highlight scala %}

val timedValue = parsedStream.assignAscendingTimestamps(_.time)

{%endhighlight %}

The meaning of ascending timestamps is, whenever an event of t is occurred, it signifies that all the events happened before t are arrived to the system. This is very simplistic view of event time, as it doesn't have a robust support for the late events. Still it's good enough to understand overall event time abstraction. This will become much clearer when we run the example.

Once we have the both questions answered, we can now work with event time to group relevant stocks.

{% highlight scala %}

val keyedStream = timedValue.keyBy(_.symbol)

val timeWindow = keyedStream.timeWindow(Time.seconds(10)).max("value").name("timedwindow")

timeWindow.print.name("print sink")

{%endhighlight %}

The above code, calculates max of a given stock price in last 10 seconds.


You can access complete code on [github](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/EventTimeExample.scala).


## Running the example

Behavior of event time is best understood using an example. If you are still confused about ascending time stamps, this example should be able help you understand details. Make sure you run this example in local mode, rather from an IDE. For more information how to run flink examples in local mode, refer to this [post](/introduction-to-flink-streaming-part-3/).

Enter the below are the records in socket console. These are records for AAPL with time stamps. The first records is for time Wed, 27 Apr 2016 11:34:22 GMT.

{% highlight text %}
1461756862000,"aapl",500.0

{%endhighlight %}

Now we send the next record, which is after 5 seconds. This signifies to flink that, 5 seconds have passed in source. Nothing will evaluate yet, as we are looking for 10 seconds window. This event is for time Wed, 27 Apr 2016 11:34:27 GMT 

{% highlight text %}
1461756867000,"aapl",600.0
{%endhighlight %}

Now we send another event, which is after 6 seconds from this time. Now flink understands 11 seconds have been passed and will evaluate the window. This event is for Wed, 27 Apr 2016 11:34:32 GMT

{% highlight text %}
1461756872000,"aapl",400.0
{%endhighlight %}

Now flink prints, maximum value as below in 10 seconds. 

{% highlight text %}
1461756872000,"aapl",600.0
{%endhighlight %}

This shows how flink is keeping track of time using ascending watermarks to keep track of time in event time.