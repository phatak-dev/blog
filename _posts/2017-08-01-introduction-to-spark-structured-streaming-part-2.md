---
layout: post
title: "Introduction to Spark Structured Streaming - Part 2 : Source and Sinks"
date : 2017-08-01
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

This is the second post in the series. In this post, we discuss about the source and sink abstractions. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Datasource API
In spark 1.3, with introduction of DataFrame abstraction, spark has introduced an API to read structured data from variety of sources.
This API is known as datasource API. Datasource API is an universal API to read structured data from different sources like databases, 
csv files etc. The data read from datasource API is represented as DataFrame in the program. So data source API has become de facto way
of creating dataframes in spark batch API.

You can read more about datasource API in my post [Introduction to Spark Data Source API](/introduction-to-spark-data-source-api-part-1).

## Extending Datasource API for streams

With Structured Streaming, streaming is moving towards dataframe abstraction. So rather than creating
a new API to create dataframe's for streaming, spark has extended the datasource API to support stream. It has added a new method *readStream*
which is similar to *read* method.

Having same abstraction for reading data in both batch and streaming makes code more consistent and easy to understand.

## Reading from Socket Stream 

As an example to show case the datasource API, let's read from socket stream. The below is the code to do that

{% highlight scala %}

  val socketStreamDf = sparkSession.readStream
   .format("socket")
   .option("host", "localhost")
   .option("port", 50050)
   .load()
{% endhighlight %}

As you can observe from above code, reading a stream has become very similar to reading static data. In the code, *readStream* indicates
we are reading continuous data rather than static data. 

If you have done spark streaming before, you may have observed there is no mention of batch time. This is because in structured streaming,
the rate of consumption of stream is determined by the sink not by the source. So when we create the source we don't need to worry about the
time information.

The result , *socketStreamDf* is a dataframe containing data from socket.

## Schema Inference in Structured Streaming

One of the important feature of data source API is it's support for the schema inference. This means it can go through the data 
to automatically understand the schema and fill that in for dataframe. This is better than specifying the schema manually. But
how that works for streams?

For streams, currently schema inference is not supported. The schema of the data has to be provided by the user or the
data source connector. In our socket stream example, the schema is provided by the socket connector. The schema contains
single column named *value* of the type *string*. This column contains the data from socket in string format.

So in structured streaming we will be specifying the schema explicitly contrast to schema inference of batch API.

## Writing to Sink

To complete a stream processing, we need both source and sink. We have created dataframe from socket source. Let's write
that to a sink.

{% highlight scala %}

val consoleDataFrameWriter = socketStreamDf.writeStream
      .format("console")
      .outputMode(OutputMode.Append())

{% endhighlight %}

To create a sink, we use *writeStream*. In our example, we are using *console* sink which just prints the data to the console. In the code,
we have specified output mode, which is similar to save modes in batch API. We will talk more about them in future posts.

The result of *writeStream* is a *DataStreamWriter*. Now we have connected the source and sink.

## Streaming Query Abstraction

Once we have connected the source and sink, next step is to create a streaming query. *StreamingQuery* is an abstraction for query that is executing continuously in the background as new data arrives. This abstraction is the entry point for starting the stream processing. All the steps before it was the setting up the stream computatiuons.

{% highlight scala %}
val query = consoleDataFrameWriter.start()

{% endhighlight %}

Above code creates a streaming query from the dataframe writer. Once we have the query object, we can run *awaitTermination* to keep it running.

{% highlight scala %}

query.awaitTermination()

{% endhighlight %}

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/SocketReadExample.scala).

## Running the example

Before we can run the example, we need to start the socket stream. You can do that by running below command

{% highlight sh %}

nc -lk localhost 50050

{% endhighlight %}

Now you can enter data in stdin, you can observe the result in the console. 

We have successfully ran our first structured streaming example.

## Batch Time

After successfully running the example, one question immediately comes in to mind. How frequently socket data is processed?. Another way of asking question is,
what's the batch time and where we have specified in code?

In structured streaming, there is no batch time. Rather than batch time, we use trigger abstraction to indicate the frequency of processing. Triggers can be
specified in variety of ways. One of the way of specifying is using processing time which is similar to batch time of earlier API.

By default, the trigger is *ProcessingTime(0)*. Here 0 indicates asap. This means as and when data arrives from the source spark tries to process it. This is very
similar to per message semantics of the other streaming systems like storm.

## Conclusion

Structured Streaming defines source and sinks using data source API. This unification of API makes it easy to move from batch world to
streaming world.
