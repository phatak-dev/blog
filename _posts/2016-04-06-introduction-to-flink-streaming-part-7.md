---
layout: post
title: "Introduction to Flink Streaming - Part 7 : Implementing session windows using custom trigger"
date : 2016-04-06
categories: scala flink flink-streaming
---
In our last blog we discussed about the internals of window API in flink. As we discussed in the blog, understanding internals allows us to implement custom windows in flink API. This flexibility to define our own window logic helps us to implement business rules seamlessly on the stream data.

In this seventh post of the series, we are going to implement a custom window using trigger API. We will be implementing a window which allow us to understand user behavior across a specific session. This will be useful for applications where we want to analyze the data coming from an online portal where user logs in and perform some actions. You can find all other posts from the series [here](/categories/flink-streaming).

**TL;DR** All code is written using Flink's scala API and you can access it on [github](https://github.com/phatak-dev/flink-examples).

## User session
A session is often period of time that capture different interactions with an application from user. A session is set up or established at a certain point in time, and then torn down at some later point.

As an example, in an online portal session normally starts when user logs into the application. All the purchases made in this point of time are captured in this session. Session will be torn down when user logged out or it expires when there is no activity for some time.

## Session window

A session window, is a window which allows us to group different records from the stream for a specific session. This window will start when the session starts and evaluated when session is ended. This window also will support tracking multiple sessions at a same time.

Session windows are often used to analyze user behavior across multiple interactions bounded by session.

In flink, we only have built in windows for time and count based evaluation. But our session window doesn't depend upon any of these. So we need to create a custom window which can satisfy our requirement.


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

Once we modeled the our session, we can now build a trigger which works on this model.

## Session Trigger

As we discussed in earlier blog, trigger is a function which decides when a given window evaluates. In our example, we want the window to evaluate when endSignal for given session is sent. As we don't have any built in trigger for it, we are going to define our own custom trigger.

The below code is for the session trigger

{% highlight scala%} 

class SessionTrigger[W <: Window] extends Trigger[Session,W] {
  override def onElement(element: Session, timestamp: Long, window: W, ctx: TriggerContext): TriggerResult = {
    if(element.endSignal.isDefined) TriggerResult.FIRE
    else TriggerResult.CONTINUE
  }

  override def onProcessingTime(time: Long, window: W, ctx: TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }
  override def onEventTime(time: Long, window: W, ctx: TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }
}

{%endhighlight %}

In above code, we are extending Trigger. When we are extending, we are specifying the that data will be represented using *Session* model class.

Once we extend the trigger, we need to override the 3 methods. They are

* ### onElement

This is a callback method, which will be called whenever each record is added to the window. In our code, we check is the endSingal is present. If it's present we return *TriggerResult.FIRE* which signifies we need to fire the trigger. Otherwise we will return *TriggerResult.CONTINUE* which signifies we need to continue adding elements to window.

* ### onEventTime and onProcessingTime

The above two methods are used when we use a window which depends on time. As our session logic doesn't depend on time, we don't need to worry about them.

You can access complete code [here](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/sessionwindow/SessionTrigger.scala).

Now we have our custom trigger ready. Now we can create a window which evaluates when a given session completes.


## Putting all together

In this section, we glue different things we did earlier. The below are the steps.

### Step 1 : Read data from socket and convert to session

As the first step, we need to read from the source and model it as session object.

{% highlight scala%} 

val env = StreamExecutionEnvironment.getExecutionEnvironment

val source = env.socketTextStream("localhost", 9000)
    
val values = source.map(value => {
    val columns = value.split(",")
    val endSignal = Try(Some(columns(2))).getOrElse(None)
    Session(columns(0), columns(1).toDouble, endSignal)
})

{% endhighlight %}

### Step 2 : Create keyed stream based on sessionId

As we discussed earlier, we want to evaluate multiple sessions at a time. So we need to created keyedstream stream based on session id.

{% highlight scala%} 
val keyValue = values.keyBy(_.sessionId)
{% endhighlight %}

### Step 3 : Create session window
Once we have keyvalue stream, now we can define a window using our custom trigger. As we did with count window, we are going to use *GlobalWindow* as our window assigner and rather than using *CountTrigger* we are going to add our custom trigger. We use purging trigger for purging session once it's evaluated.

{% highlight scala%} 

val sessionWindowStream = keyValue.
        window(GlobalWindows.create()).
        trigger(PurgingTrigger.of(new SessionTrigger[GlobalWindow]()))
        
{% endhighlight %}

The above code created *sessionWindowStream* which tracks sessions.

### Step 4 : Aggregate and Print

Once we have the window, we need to define an aggregate function over window. In this example, we are going to sum the value over session and print to console.

{% highlight scala%} 
sessionWindowStream.sum("value").print()
{% endhighlight %}

You can access complete code [here](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/sessionwindow/SessionWindowExample.scala).

## Running the example
As we ran our earlier example, we will be entering data in the stdin of the socket. In our example, socket will be listening on port 9000.

Enter below two lines in the stdin

{% highlight text %}
session1,100
session2,200
{% endhighlight %}

In the above, we have started two sessions *session1* and *session2*. This will create two windows. As of now, no window will evaluate as session is not yet ended. 

Let's end *session1* using below line

{% highlight text %}
session1,200,end
{% endhighlight %}

Now you will observe the below result in flink

{% highlight text %}
Session(session1,300.0,None)
{% endhighlight %}

Window for session1 is evaluated now, as it is ended.

## References

[http://mail-archives.apache.org/mod_mbox/flink-user/201510.mbox/%3CC195B624-FB46-4D90-AE0F-B8782EB81951@apache.org%3E](http://mail-archives.apache.org/mod_mbox/flink-user/201510.mbox/%3CC195B624-FB46-4D90-AE0F-B8782EB81951@apache.org%3E)

[https://gist.github.com/aljoscha/a7c6f22548e7d24bc4ac](https://gist.github.com/aljoscha/a7c6f22548e7d24bc4ac)


