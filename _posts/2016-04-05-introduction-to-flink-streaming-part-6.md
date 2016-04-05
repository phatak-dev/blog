---
layout: post
title: "Introduction to Flink Streaming - Part 6 : Anatomy of Window API"
date : 2016-04-05
categories: scala flink flink-streaming
---
In last few post in flink series, we have discussed about window API in flink. Window API is a powerful construct which allows us to express different stream processing concepts in elegant manner. In Flink, we can build windows using different recipes. Understanding these recipes helps us use them in efficient manner and also allows us to extend them to suit our custom requirements.

In this sixth blog of series, we will take a deep dive into window internals. You can access all the posts in the series [here](/categories/flink-streaming).

**TL;DR** All code is written using Flink's scala API and you can access it on [github](https://github.com/phatak-dev/flink-examples).


## Anatomy of Window API

From earlier blogs, window API in flink may look like a single monolithic API. But under the covers it's a modular API. A window in flink is constructed using few different functions. This modular nature of defining window gives user flexibility to define the windows for their use case. 

The different functions needed to define a window in flink are, 

* ### Window Assigner

A function which is responsible for assigning given element to window. Depending upon the definition of window, one element can belong to one or more window at a time.

* ### Trigger
Function which defines the condition for triggering window evaluation. This function control when a given window created by window assigner is evaluated.

* ### Evictor
An optional function which defines the preprocessing before firing window operations.

The above 3 different functions are recipes for putting together the window.

## Understanding Count window

Once we understood the different components of window API, let's take one of existing window and see how these functions are defined. We will be using count window as example.

### Window Assigner
When you are building an count based window, there is no start or end to the window. So for those kind of non time based windows, we have window assigner called GlobalWindow. For a given key, all values are filled into the same window. 

The below code shows how to assign the window assigner.

{% highlight scala %}
   keyValue.window(GlobalWindows.create())
{%endhighlight %}

*window* API allows us to add the window assigner to the window. Every window assigner has a default trigger. The default trigger for a global window is *NeverTrigger* which never triggers. So this window assigner has to be used with a custom trigger.

### Count trigger
{% highlight scala %}
   trigger(CountTrigger.of(2))
{%endhighlight %}

Once we have the window assigner, now we have to define when the window needs to be trigger. In the above code, we add a trigger which evaluates the window for every two records.

For this example, we are not using any evictor. 

## Aggregation

Once we create window, we need to run an aggregation on window to make it non lazy.

{% highlight scala %}
countWindowWithoutPurge.sum(1).print()
{%endhighlight %}

You can access full code [here](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/WindowAnatomy.scala).


## Running example

Let's run above count window with following data. Enter this data in socket stdin similar to our last [example](/introduction-to-flink-streaming-part-5/).

Input 

{% highlight text %}
hello how are you
hello who are you
{%endhighlight %}

Result 

{%highlight text %}
(you,2)
(are,2)
(hello,2)
(how,2)
{%endhighlight%}

The above result shows, flink created 4 windows for 4 keys. Now if you enter the below lines again

{% highlight text %}
hello how are you
hello who are you
{%endhighlight%}

you will be observe the below result. 

{%highlight text %}
(you,4)
(are,4)
(hello,4)
(how,4)
{%endhighlight%}

Flink still has 4 windows, but now count is 4 rather than 2. It's because
rather than evaluating just new window, flink has retained the records from the earlier window also. This is the default behavior of a trigger. But most of the cases we want to clear the records once window evaluates, rather than keeping forever. In those situations we need to use purge trigger with count trigger.

## Purging Trigger

Purging trigger is a trigger which normally wraps the other triggers. Purging trigger is responsible for purging all the values which are passed to the trigger from the window once window evaluates.

The below code shows how to use purging trigger with count trigger.

{% highlight scala %}
val countWindowWithPurge = keyValue.window(GlobalWindows.create()).
      trigger(PurgingTrigger.of(CountTrigger.of[GlobalWindow](2)))
{%endhighlight %}

Now *PurgingTrigger* wraps the *CountTrigger*. So purging trigger is always used in combination with other triggers.

If you run the code with same data, you will observe that result will be only calculated for latest window not across evaluations.

Now we understand anatomy of a window in Flink streaming API.





