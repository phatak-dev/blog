---
layout: post
title: "Introduction to Spark Structured Streaming - Part 13 : Session Windows"
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

This is the thirteenth post in the series. In this post, we discuss about session windows. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## User session
A session is often period of time that capture different interactions with an application from user. A session is set up or established at a certain point in time, and then torn down at some later point.

As an example, in an online portal session normally starts when user logs into the application. All the purchases made in this point of time are captured in this session. Session will be torn down when user logged out or it expires when there is no activity for some time.

## Session window

A session window, is a window which allows us to group different records from the stream for a specific session. This window will start when the session starts and evaluated when session is ended. This window also will support tracking multiple sessions at a same time.

Session windows are often used to analyze user behavior across multiple interactions bounded by session.

In structured streaming, we only have built in windows for time based evaluation. But our session window doesn't soley depend upon time. So we need to create a custom window which can satisfy our requirement.

## Modeling user session
Once we understood about the session and session window, we need to model the session in our code. I have a simple representation of session for explaining the example. Most of the real world session information will much more complicated than it.

{% highlight scala%} 
case class Session(sessionId:String, value:Double, endSignal:Option[String])
{%endhighlight %}

The above case class is model of our session. It has following three components

* ### sessionId 
An identifier which uniquely identifies a session. There can be multiple sessions active at same time. Each of these sessions will have unique session id.

* ### value

It's a value associated each interaction in the session. This signifies the information we want to track with in the session. In real world scenarios, it will be user interactions with the system. As an example, in a online store it may be the product user added to the cart. In our example, it's a simple double value.

* ### endSignal

It's an optional value in record. This signifies end of the session from the application side. It may be risen because user has logged out or may the session expired. It depends on the application to generate these signals.


## Custom State Management

As session window is not tied to time, it needs to do custom state management. To create a session window, we will use *mapGroupWithState* API of structured streaming, which allows to work with state indepent of the time restriction. 

## Session Window Example

The below are the steps for defining a session based window on socket stream.


### Read from the socket

{% highlight scala %}
    val socketStreamDf = sparkSession.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 50050)
      .load()
{% endhighlight %}


### Convert to Session Event

{% highlight scala %}

    import sparkSession.implicits._
    val socketDs = socketStreamDf.as[String]

    // events
    val events = socketDs.map(line => {
      val columns = line.split(",")
      val endSignal = Try(Some(columns(2))).getOrElse(None)
      Session(columns(0), columns(1).toDouble, endSignal)
    })

{% endhighlight %}


### Define Custom State Management Models

{% highlight scala %}

case class SessionInfo(
                        totalSum: Double)

case class SessionUpdate(
                          id: String,
                          totalSum: Double,
                          expired: Boolean)
{% endhighlight %}


### Group Sessions by Session Id

{% highlight scala %}

val sessionUpdates = events.groupByKey(_.sessionId)

{% endhighlight %}

### Define mapGroupWithState

{% highlight scala %}

mapGroupsWithState[SessionInfo, SessionUpdate](GroupStateTimeout.NoTimeout()) {

{% endhighlight%}

### Update State for Events

{% highlight scala %}
case (sessionId: String, eventsIter: Iterator[Session], state: GroupState[SessionInfo]) =>
 val events = eventsIter.toSeq
        val updatedSession = if (state.exists) {
          val existingState = state.get
          val updatedEvents = SessionInfo(existingState.totalSum + events.map(event => event.value).reduce(_ + _))
          updatedEvents
        }
        else {
          SessionInfo(events.length)
        }
        
state.update(updatedSession)
{% endhighlight %}

### Handle Completion of the Session

{% highlight scala %}

val isEndSignal = events.filter(value => value.endSignal.isDefined).length > 0
        if (isEndSignal) {
          state.remove()
          SessionUpdate(sessionId, updatedSession.totalSum, true)
        }
        else {
          SessionUpdate(sessionId, updatedSession.totalSum, false)
        }
{% endhighlight %}


### Output to Console Sink

{% highlight scala %}
    val query = sessionUpdates
      .writeStream
      .outputMode("update")
      .format("console")
      .start()

{% endhighlight %}


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
