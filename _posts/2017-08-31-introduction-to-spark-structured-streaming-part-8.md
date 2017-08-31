---
layout: post
title: "Introduction to Spark Structured Streaming - Part 8 : Time Abstraction"
date : 2017-08-31
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

This is the eighth post in the series. In this post, we discuss about time abstraction in structured streaming. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Concept of Time in Streaming Application

A streaming application is an always running application. So in order to understand the behavior of the application over time, we need to take snapshots of the stream in various points. Normally these various points are defined using a time component.

Time in streaming application is way to correlate different events in the stream to extract some meaningful insights. For example, when we say count of words in a word count example for last 10 seconds, we normally mean to collect all the records arrived in that point of time and run a word count on it.

In DStream API,spark supported one concept of time. But structured streaming API support multiple different ones.

## Time in Structured Streaming

When we say, last 10 seconds what it means in structured streaming? it depends. It can be one of three following

* ### Processing Time

This concept of time is very familiar to most of the users. In this, time is tracked using a clock run by the processing engine. So in this time, last 10 seconds means the records arrived in last 10 seconds for the processing. Here we only use the semantics of when the records came for processing. DStream supported this abstraction of time in it's API.

Though processing time is good time measure to have,it's not always enough. For example, if we want to calculate state of sensors at given point of time, we want to collect events that happened in that time range. But if the events arrive lately to processing system due to various reasons, we may miss some of the events as processing clock does not care about the actual time of events. To address this, structured streaming support another kind of time called event time.

* ### Event Time

Event time is the time embed in the data that is coming into the system. So here 10 seconds means, all the records generated in those 10 seconds at the source. These may come out of order to processing. This time is independent of the clock that is kept by the processing engine.Event time is extremely useful for handling the late arrival events.

* ### Ingestion Time

Ingestion time is the time when events ingested into the system. This time is in between of the event time and processing time. Normally in processing time, each machine in cluster is used to assign the time stamp to track events. This may result in little inconsistent view of the data, as there may be delays in time across the cluster. But ingestion time, time stamp is assigned in ingestion so that all the machines in the cluster have exact same view. These are useful to calculate results on data that arrive in order at the level of ingestion.

## WaterMarks 

As structured streaming supports multiple concept of time, how it keep tracks of time?. Because in the normal processing time, a system clock can be used. But you cannot use the system clock in case of the event time and ingestion time. So there has to be a generic mechanism to handle this.

Watermarks is the mechanism used by the structured streaming in order to signify the passing of time in stream. Watermarks are implemented using partial aggregations and the update output mode. Watermarks allow us to implement both tracking time and handle late events.We will discuss more about water marks in upcoming posts.

Now we understand different time abstractions supported by structured streaming. In upcoming blogs, we will discuss how to work with these different abstractions.

## Conclusion
Structured streaming has a rich time abstractions which makes modeling different stream processing applications much easier than earlier API.
