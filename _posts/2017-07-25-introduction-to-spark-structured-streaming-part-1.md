---
layout: post
title: "Introduction to Spark Structured Streaming - Part 1 : DataFrame Abstraction to Stream"
date : 2017-07-25
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

This is the first post in the series. In this post, we discuss about the structured streaming abstractions. You
can read all the posts in the series [here](/categories/introduction-structured-streaming).


## Unified Dataset Abstraction in Spark 2.0

In Spark 2.0, spark has replaced RDD with Dataset as single abstraction for all user facing API's. Dataset is an abstraction for structured 
world which combines best of both RDD and Dataframe world.Dataset has already replaced batch, machine learning and graph processing RDD API's.

With structured streaming, dataset abstractions are coming to streaming API's also. It's going to be replacing RDD based
streaming API of 1.x

## Stream as Dataset/Dataframe 

In spark 1.x, a stream is viewed as collection of RDD, where each RDD is created for minibatch. It worked well initially, as it allowed
users of spark to reuse rdd abstractions for streaming also. But over time developers started noticing the limitations of this approach.

## Limitations of DStream API

### Batch Time Constraint

As stream is represented as faster batch processing, batch time become critical component of stream design. If you have coded in Dstream API,
you know that you need to specify the batch time at the time of StreamingContext creation. This application level batch time becomes an issue,
when different streams  have different speeds. So often users resorted to window functions to normalise the streams which often lead to poor 
performance.

### No Support for Event Time

As batch time dictates the central clock for stream, we cannot change it from stream to stream. So this means we can't use time embedded in the stream
, known as event time, for processing. Often in stream processing, we will be interested more in event time than process time i.e time of event generation at source rather than time at which event has reached the processing system. So it was not possible to expose the event time capability with Dstream API's.

### Weak Support for Dataset/DataFrame abstractions

As more and more code in spark moved to Dataframe/Dataset abstractions, it was desirable to do the same for streaming also. Often dataset based code
resulted in performant code due to catalyst and code generations than RDD based ones.

It's possible to create a dataset from RDD. But that doesn't work well with streaming API's. So users often stuck with RDD API's for streaming where rest of
libraries enjoyed better abstractions.

### No Custom Triggers

As stream processing becomes complex, it's often desirable to define custom triggers to track interesting behaviours. One of the typical use case for custom
triggers are user sessions. Typically a user session is part of stream where user has logged in and using the services till he logged out or login expired. Many stream processing tasks like to define a window which captures this information.

One of the challenge of session is, it's not bounded by time. Some sessions can be small but some may go for long. So we can't use processing time
or even event time to define this. As dstream API's was only capable of defining window using time, user were not able define session based processing
easily in existing API's.

### Updates

DStream API models stream as a continuous new events. It treats each event atomically and when we write the result of any batch, it doesn't remember
the state of earlier batches. This works well for simple ETL workloads. But often there are scenarios, where there is an event which indicates the update
to the event which was already processed by the system. In that scenarios, it's often desirable to update the sink ( a place to which we write the output 
of the stream processing), rather than adding new records. But Dstream API, doesn't expose any of those semantics.


## Advantages of Representing Stream as DataFrame

To solve the issues mentioned above, spark has remodeled the stream as infinite dataset, rather than a collection of RDD's. This makes a quite
departure from the mini batch model employed in earlier API. Using this model, we can address issues of earlier API's.

### Trigger is specific to Stream

In structured streaming, there is no batch time. It's replaced with triggers which can be both time based and non-time based. Also
trigger is specific to stream, which makes modeling event time and implementing sessionization straight forward in this new API.

### Supports event time

As triggers are specific stream, new API has native support for event time.

### Dataset is native abstraction

No more conversion from RDD to Dataframes. It's native abstraction in structured streaming. Now we can leverage rest of dataset based
libraries for better performance. It also brings the complete SQL support for the stream processing.

### Supports different output modes

In structured streaming, there is concept of output modes. This allows streams to make decision on how to output whenever there is updates
in stream.

## Conclusion

Structured Streaming is a complete rethinking of stream processing in spark. It replaces earlier fast batch processing model with true
stream processing abstraction. 
