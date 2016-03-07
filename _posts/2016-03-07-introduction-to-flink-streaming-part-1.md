---
layout: post
title: "Introduction to Flink Streaming - Part 1 : WordCount"
date : 2016-03-07
categories: scala flink flink-streaming
---
Apache Flink is one of the new generation distributed systems which unifies batch and streaming processing. Earlier in my blog, I have [discussed](/introduction-to-flink-for-spark-developers-flink-vs-spark) about how it's different than Apache Spark and also given a introductory [talk](/introduction-to-flink-talk) about it's batch API. In batch world, Flink looks very similar to Spark API as it uses similar concepts from Map/Reduce. But in the case of streaming, flink is much different than the Spark or any other stream processing systems out there. 

So in these series of blogs, I will be discussing about how to get started with flink streaming API and using it's different unique features. Flink streaming API has undergone significant changes from 0.10 to 1.0 version. So I will be discussing latest 1.0 API. You can access all the blogs in the series [here](/categories/flink-streaming/).

In this first blog, I will be discussing about how to run word count example in flink streaming. If you are new to flink, I encourage you to watch my [introductory talk](/introduction-to-flink-talk) before continuing.

**TL;DR** All code is written using Flink's scala API and you can access it on [github](https://github.com/phatak-dev/flink-examples).

## Flink Streaming API

Flink provides a streaming API called as Flink DataStream API to process continuous unbounded streams of data in realtime. This API build on top of the pipelined streaming execution engine of flink.

Datastream API has undergone a significant change from 0.10 to 1.0. So many examples you see in the other blogs including flink blog have become obsolete. I will be discussing about Flink 1.0 API which is released in maven central and yet to be released in binary releases.

## Adding dependency

To start using Datastream API, you should add the following dependency to project. I am using sbt for build management. You can also use other build tools like maven.

{% highlight scala %}
"org.apache.flink" %% "flink-scala" % "1.0.0"
{%endhighlight%}

You can access complete build.sbt [here](https://github.com/phatak-dev/flink-examples/blob/master/build.sbt)

## Hello World Example

Whenever we learn any new API in big data, it has become custom to do word count. In this example, we are reading some lines from a socket and doing word count on them. 

The below are the steps to write an streaming example in datastream API.

### Step 1. Get Streaming Environment

In both batch and streaming example, first step is to create a pointer to environment on which this program runs. Flink can run same program in local or cluster mode. You can read more about modes [here](https://ci.apache.org/projects/flink/flink-docs-master/apis/common/index.html#anatomy-of-a-flink-program).

{% highlight scala %}
val env = StreamExecutionEnvironment.getExecutionEnvironment
{%endhighlight%}

If you are familiar with Spark, StreamExecutionEnvironment is similar to spark context. 

One of the things to remember when using scala API of Flink is to import the implicts. If you don't import them you will run into strange error messages.

You can import the implicts for streaming as below

{% highlight scala %}
import org.apache.flink.streaming.api.scala._
{%endhighlight%}

### Step 2. Create DataStream from socket

Once we have the pointer to execution environment, next step is to create a stream from socket. 

{% highlight scala %}
val socketStream = env.socketTextStream("localhost",9000)
{%endhighlight%}

*socketStream* will be of the type DataStream. DataStream is basic abstraction of flink's streaming API.

### Step 3. Implement wordcount logic

{% highlight scala %}

val wordsStream = socketStream.flatMap(value => value.split("\\s+")).map(value => (value,1))

    val keyValuePair = wordsStream.keyBy(0)

    val countPair = keyValuePair.sum(1)

{%endhighlight%}

The above is very standard code to do word count in map/reduce style. Notable differences are we are using keyBy rather than groupBy and sum for reduce operations. The value 0 and 1 in keyBy and sum calls signifies the index of columns in tuple to be used as key and values.

### Step 4. Print the word counts

Once we have wordcount stream, we want to call print, to print the values into standard output

{% highlight scala %}
 
countPair.print()

{%endhighlight%}

### Step 5. Trigger program execution

All the above steps only defines the processing, but do not trigger execution. This needs to be done explicitly using execute.

{% highlight scala %}
 
env.execute()

{%endhighlight%}

Now we have complete code for the word count example. You can access full code [here](https://github.com/phatak-dev/flink-examples/blob/master/src/main/scala/com/madhukaraphatak/flink/streaming/examples/StreamingWordCount.scala).

## Executing code

To run this example, we need to start the socket at 9000 at following command to 

{% highlight sh%}
 nc -lk 9000
{%endhighlight%}

Once you do that, you can run the program from the IDE and [command line interface](https://ci.apache.org/projects/flink/flink-docs-master/apis/cli.html). 

You can keep on entering the lines in nc command line and press enter. As you pass the lines you can observe the word counts printed on the stdout.

Now we have successfully executed the our first flink streaming example.

## Unbounded state

If you observe the result, as an when you pass more rows the count keeps increasing. This indicates that flink keeps updating the count state indefinitely. This may be desired in some examples, but most of the use cases we want to limit the state to some certain time. We will see how to achieve it using window functionality in the next blog in the series.

## Compared to Spark Streaming API

This section is only applicable to you, if you have done spark streaming before. If you are not familiar with Apache Spark feel free to skip it.

The above code looks a lot similar to Spark streaming's DStream API. Though syntax looks same there are few key differences. Some of them are 

### 1. No need of Batch Size in Flink

Spark streaming needs batch size to be defined before any stream processing. It's because spark streaming follows micro batches for stream processing which is also known as near realtime . But flink follows one message at a time way where each message is processed as and when it arrives. So flink doesnot need any batch size to be specified.

### 2. State management

In spark, after each batch, the state has to be updated explicitly if you want to keep track of wordcount across batches. But in flink the state is up-to-dated as and when new records arrive implicitly.

We discuss more differences in future posts.

## References

Apache Flink 1.0 Streaming Guide - [https://ci.apache.org/projects/flink/flink-docs-master/](https://ci.apache.org/projects/flink/flink-docs-master/)

Introducing Flink Streaming - [https://flink.apache.org/news/2015/02/09/streaming-example.html](https://flink.apache.org/news/2015/02/09/streaming-example.html)