---
layout: post
title: "Introduction to Spark Structured Streaming - Part 3 : WordCount"
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

This is the third post in the series. In this post, we discuss about the aggregation on stream using word count example. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Word Cout

Word count is a hello world example of big data. Whenever we learn new API's, we start with simple example which shows important aspects of the API. Wordcount
is unique in the sense, it shows how API handles single row and multi row operation. Using this simple example, we can understand many different aspects of
the structured streaming API.

## Reading data

As we did in last post, we will read our data from socket stream. The below is the code to read from socket and create a dataframe.

{% highlight scala %}

val socketStreamDf = sparkSession.readStream
  .format("socket")
  .option("host", "localhost")
  .option("port", 50050)
  .load()

{% endhighlight %}

## Dataframe to Dataset

In the above code, *socketStreamDf* is a dataframe. Each row of the dataframe will be each line of the socket. To implement the word count, first
we need split the whole line to multiple words. Doing in dataframe dsl and sql is tricky. The logic is easy to implement in functional API like *flatMap*.

So rather than working with dataframe abstraction, we can work with dataset abstraction which gives us good functional API's. We know the dataframe
has single column *value* of type string. So we can represent it using *Dataset[String]*. 

{% highlight scala %}
import sparkSession.implicits._
val socketDs = socketStreamDf.as[String]
{% endhighlight %}

The above creates a dataset *socketDs*. The implicit import makes sure we have right encoders for string to convert to dataset.

## Words 

Once we have the dataset, we can use flatMap to get words.

{% highlight scala %}

val wordsDs =  socketDs.flatMap(value => value.split(" "))
   
{% endhighlight %}

## Group By and Aggregation

Once we have words, next step is to group by words and aggregate. As structured streaming is based on dataframe abstraction, we can
use sql group by and aggregation function on stream. This is one of the strength of moving to dataframe abstraction. We can use all
the batch API's on stream seamlessly.

{% highlight scala %}

val wordsDs =  socketDs.flatMap(value => value.split(" "))

{% endhighlight %}

## Creating Query

Once we have the logic implemented, next step is to connect to a sink and create query. We will be using console sink as last post.

{% highlight scala %}
val query =
      countDs.writeStream.format("console").outputMode(OutputMode.Complete())

{% endhighlight %}
