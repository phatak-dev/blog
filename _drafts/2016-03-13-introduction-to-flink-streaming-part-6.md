---
layout: post
title: "Introduction to Flink Streaming - Part 6 : De constructing window API in Flink"
date : 2016-03-13
categories: scala flink flink-streaming
---
In last few blog in the series, we have discussed about window API in flink. In this blog, we will be taking a deeper dive into the internals of window API. By understanding the internals of window API helps us use them in efficient manner. Also it may help us to write our own custom windowing system for things like session etc.

So in this post, I will be focusing the inner details of a given window. For other blogs in the series refer this link.

## Anatomy of time window

Whenever you define a window, the following components are defined.

* ### Window Assigner

A function which is responsible for assigning given element to window. Depending upon the definition of window, one element can belong to several 
window at a time. 

* ### Trigger
An optional function which defines the condition for trigger window evaluation.

* ### Evictor

An optional function which defines the preprocessing before firing window operations.

The above 3 different functions are recipes for putting together the window.

## Understanding Count window

Once we understood the different components of window API, let's take one of our window and put together how it works. We are taking count window as an example.

* ### Window Assigner
When you are building an count based window, there is no start or end to the window. So for those kind of non time based windows, we have window assigner called GlobalWindow. For a given key, all values are filled into the same window. 

The below code shows how to assign the window assigner.

{% highlight scala %}
   keyValue.window(GlobalWindows.create())
{%endhighlight %}

*window* API allows us to add the window assigner to the window. Every window assigner has a default trigger. The default trigger for a global window is *NeverTrigger*. So this window assigner has to be assigned with a trigger.

* ### Count trigger
{% highlight scala %}
   trigger(CountTrigger.of(2))
{%endhighlight %}

Once we have the window assigner, now we have to define when the window needs to be trigger. In the above code, we add a trigger which evaluates the window for every two records.

## Running example




## Purging Trigger

From above example it's clear that even after triggering the elements are not cleared from the window. This is good for keeping history but it becomes bottlneck for the performance. So normally we want to clear out the the elements of window once it's evaluated. For this operation, we can use purging trigger.

Purging trigger is a trigger which normally wraps the other triggers. Purging trigger is responsible for purging all the values which are passed to the trigger from the window.

The below code shows how to use purging trigger with count trigger.

{% highlight scala %}
trigger(PurgingTrigger.of(CountTrigger.of[GlobalWindow](2)))
{%endhighlight %}

Once we added the purging trigger, then it will be give the tumbling effect.

## Running Purging Trigger example



In this post, we learned about different recipes that make up a window. In the post, we have used count window as example. Now you may be wondering how this works interms of time based window? Before we understand we need to understand the idea of time in Flink.





