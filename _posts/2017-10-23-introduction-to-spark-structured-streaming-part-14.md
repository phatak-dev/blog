---
layout: post
title: "Introduction to Spark Structured Streaming - Part 14 : Session Windows using Custom State"
date : 2017-10-23
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

This is the fourteenth post in the series. In this post, we discuss about session windows. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## User session
A session is often period of time that capture different interactions with an application from user. A session is set up or established at a certain point in time, and then torn down at some later point.

As an example, in an online portal session normally starts when user logs into the application. All the purchases made in this point of time are captured in this session. Session will be torn down when user logged out or it expires when there is no activity for some time.

## Session window

A session window, is a window which allows us to group different records from the stream for a specific session. This window will start when the session starts and evaluated when session is ended. This window also will support tracking multiple sessions at a same time.

Session windows are often used to analyze user behavior across multiple interactions bounded by session.

In structured streaming, we only have built in windows for time based evaluation. But our session window doesn't solely depend upon time. So we need to create a custom window which can satisfy our requirement.

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

As session window is not tied to time, it needs to do custom state management. To create a session window, we will use *mapGroupWithState* API of structured streaming, which allows to work with state independent of the time restriction. 

## Session Window Example

The below are the steps for defining a session based window on socket stream.

### Read from the socket

As earlier examples, we will be reading the data from socket source.

{% highlight scala %}
    val socketStreamDf = sparkSession.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 50050)
      .load()
{% endhighlight %}


### Convert to Session Event

The data coming from socket is represented as single string in which values are separated by comma. So in below code,
we will be parsing given input to session case class.

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

Once we have parsed the input stream as session, we need to define models to track and output the session state.

{% highlight scala %}

case class SessionInfo(
                        totalSum: Double)

case class SessionUpdate(
                          id: String,
                          totalSum: Double,
                          expired: Boolean)
{% endhighlight %}

In above code, *SessionInfo* is a case class which tracks the information we store for a given session. In our example, we just keep a single value called *totalSum*. It tracks the total value seen in the session till it's expired.

*SessionUpdate* is a case class used to output the update for a given session for every batch. We output below details

  * id - Id of the session
  * totalSum - Total value of the session till that batch
  * expired - Is session expired or not

Using above models, we can track session in structured streaming API.

### Group Sessions by Session Id

Once we have defined the models, we group sessions using *sessionId*.

{% highlight scala %}

val sessionUpdates = events.groupByKey(_.sessionId)

{% endhighlight %}

### Define mapGroupWithState

*mapGroupState* as name indicates an API to map state for a given grouped input. In our case, we have sessions grouped by sessionId.

{% highlight scala %}

mapGroupsWithState[SessionInfo, SessionUpdate](GroupStateTimeout.NoTimeout()) {

{% endhighlight%}

From above code, API takes two models. One indicating the state we are tracking, *SessionInfo* and another indicating the return value of the function which is *SessionUpdate*. API also allows user to give a default time out, which allows user to close the state after timeout. In our example, we want the session to close after explicit user input. So we are specifying no timeout using *GroupStateTimeout.NoTimeout*.

### Update State for Events

As first part of implementing the map, we update the state for new events.

{% highlight scala %}
case (sessionId: String, eventsIter: Iterator[Session], state: GroupState[SessionInfo]) =>
val events = eventsIter.toSeq
val updatedSession = if (state.exists) {
  val existingState = state.get
  val updatedEvents = SessionInfo(existingState.totalSum + events.map(event => event.value).reduce(_ + _))
  updatedEvents
}
else {
  SessionInfo(events.map(event => event.value).reduce(_+_))
}
        
state.update(updatedSession)
{% endhighlight %}

In above code, the map takes below parameters

* sessionId - Column on which the group is created. In our example, it's sessionId

* eventsIter - All the events for this backs

* state - Current State

In the code, we check is any state exist for the session. If so , we add new events sum to existing one . Otherwise create new entries. We update the state using *state.update* API.

### Handle Completion of the Session

Once we handled the update, we need to handle the sessions which are complete.

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

In above code, we check for endSignal. If there is endSignal we use *state.remove* to remove the state for that session id. Then we output the right session update.

### Output to Console Sink

All the session updates are printed to console sink.

{% highlight scala %}
    val query = sessionUpdates
      .writeStream
      .outputMode("update")
      .format("console")
      .start()

{% endhighlight %}

You can access complete example on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/SessionisationExample.scala).

## Running the Example

Enter the below records in socket console.

### First sessions

We start two sessions with id session1 and sessions2 using below input

{% highlight text %}
session1,100
session2,200
{%endhighlight %}

Spark outputs below results which indicates start of windows 

{% highlight text %}
-------------------------------------------
Batch: 0
-------------------------------------------
+--------+--------+-------+
|      id|totalSum|expired|
+--------+--------+-------+
|session1|   100.0|  false|
|session2|   200.0|  false|
+--------+--------+-------+

{% endhighlight %}

### Additional Event for Session 1

{% highlight text %}
session1,200
{%endhighlight %}

The output of the spark will be as below. You can observe from output, window for session1 is changed.

{% highlight text %}
-------------------------------------------
Batch: 1
-------------------------------------------
+--------+--------+-------+
|      id|totalSum|expired|
+--------+--------+-------+
|session1|   300.0|  false|
+--------+--------+-------+
{% endhighlight %}

### End Session 1 
Below message will end session 1.

{% highlight text %}
session1,200,end
{%endhighlight %}

Now spark completes the first session.

{% highlight text %}
-------------------------------------------
Batch: 2
-------------------------------------------
+--------+--------+-------+
|      id|totalSum|expired|
+--------+--------+-------+
|session1|   500.0|   true|
+--------+--------+-------+
{%endhighlight %}

### Starting new session1 and updating existing session 2
The below inputs will start new session1 as it was already completed and update existing session2.

{% highlight text %}
session1,100
session2,200
{%endhighlight %}

The below is spark output

{% highlight text %}

-------------------------------------------
Batch: 3
-------------------------------------------
+--------+--------+-------+
|      id|totalSum|expired|
+--------+--------+-------+
|session1|   100.0|  false|
|session2|   400.0|  false|
+--------+--------+-------+

{% endhighlight %}

From the output you can observe that session1 is started from scratch and session2 is updated.

## Conclusion

In this post we understood how to use custom state management to implement session windows in structured streaming.
