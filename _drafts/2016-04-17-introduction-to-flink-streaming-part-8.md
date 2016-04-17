---
layout: post
title: "Introduction to Flink Streaming - Part 8 : Understanding Time in Flink"
date : 2016-04-17
categories: scala flink flink-streaming
---

Time plays an important role in streaming applications. Using time, we can group, correlate different events happening in the stream. Some of the constructs like window, heavily use the time component. Most of the streaming frameworks supports a single meaning of time, which is mostly tied to the processing time. Processing time is a clock which keep tracks of the time passed from the beginning of the process. But flink supports much richer semantics for time which is not just processing time. This makes it to stand apart from the rest of the streaming framework.

In this eight post in the series, we will be talking different time abstractions supported in flink. You can find all the other posts in the series here.


## Concept of time in streaming application

A streaming application is a always running application. So in order to understand the behavior of the application over time, we need to take snapshots of the stream in various points. Normally these various point are defined using a time component.

Time in streaming application is way to correlate different events in the stream to extract some meaningful insights. For example, when we say count of words in a word count example for last 10 seconds, we normally mean to collect all the records arrived in that point of time and run a word count on it.

Normally most of the streaming frameworks like storm, spark-streaming only support one concept of time. But flink does support multiple different ones.

## Time in Flink

When we say, last 10 seconds what it means? Flink say it depends. It can be one of three following

* ### Processing Time

This concept of time is very familiar to most of the user. In this, time is tracked using a clock run by the processing engine. So in this time, last 10 seconds means the records arrived in last 10 seconds of the processing. Here we only use the semantics of when the records came for processing.

Though processing time is good time measure to have it's not always enough. For example, if we want to calculate state of sensors at given point of time, we want to collect events that happened in that time range. But if the events arrive lately to processing system due to various reasons, we may miss some of the events as processing clock does not care about the actual time of events. To address this, flink support another kind of time called event time.

* ### Event Time

Event time is the time embed in the data that is coming into the system. This time is independent of the clock that is kept by the processing engine. Since it's independent of the clock, source needs to provide additional information how much time has passed between the records so that it can track the flow of time. Event time is extremely useful handling the late arrival events.


* ### Ingestion Time

Ingestion time is the time when events ingested into the system. This time is in between of the event time and processing time. Normally in processing time, each machine in cluster is used to assign the time stamp. This may result in little inconsistent view of the data, as there may be delays in time across the cluster. But ingestion time, timestamp is assigned in ingestion so that all the machines in the cluster have exact same view. Very few cases in the real world we end up using this time.


## WaterMarks 

As flink supports multiple concept of time, how flink keep tracks of time? Because in the normal processing time, a system clock can be used. But you cannot use the system clock in case of the event time and ingestion time. So there has to be a generic mechanism to signify this.

Watermarks is the mechanism used by the flink in order to signify the passing of time in stream. Watermarks are the special control events which are part of the stream itself.

![WaterMarks in Flink](https://ci.apache.org/projects/flink/flink-docs-release-1.0/apis/streaming/fig/stream_watermark_in_order.svg)

The above diagram shows, the water mark events as part of the stream. These water marks can be generated using system clock in case of processing time, source in case of processing time. This flexibility of keeping track of time allows flink to support different time.


## Compared to Spark Streaming API

This section is only applicable to you, if you have done spark streaming before. If you are not familiar with Apache Spark feel free to skip it.

Spark streaming only supports the processing time as of now. The time is tracked using the internal clock in the spark driver.

## References

Event time in Flink Docs - [https://ci.apache.org/projects/flink/flink-docs-release-1.0/apis/streaming/event_time.html](https://ci.apache.org/projects/flink/flink-docs-release-1.0/apis/streaming/event_time.html).




