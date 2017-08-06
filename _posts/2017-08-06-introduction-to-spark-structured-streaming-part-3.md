---
layout: post
title: "Introduction to Spark Structured Streaming - Part 3 : Stateful WordCount"
date : 2017-08-06
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

## Word Count

Word count is a hello world example of big data. Whenever we learn new API's, we start with simple example which shows important aspects of the API. Word count is unique in that sense, it shows how API handles single row and multi row operations. Using this simple example, we can understand many different aspects of the structured streaming API.

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
we need split the whole line to multiple words. Doing that in dataframe dsl or sql is tricky. The logic is easy to implement in functional API like *flatMap*.

So rather than working with dataframe abstraction, we can work with dataset abstraction which gives us good functional API's. We know the dataframe
has single column *value* of type string. So we can represent it using *Dataset[String]*. 

{% highlight scala %}
import sparkSession.implicits._
val socketDs = socketStreamDf.as[String]
{% endhighlight %}

The above code creates a dataset *socketDs*. The implicit import makes sure we have right encoders for string to convert to dataset.

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
val countDs = wordsDs.groupBy("value").count()
{% endhighlight %}

## Run using Query

Once we have the logic implemented, next step is to connect to a sink and create query. We will be using console sink as last post.

{% highlight scala %}
val query =
      countDs.writeStream.format("console").outputMode(OutputMode.Complete())

query.start().awaitTermination()
{% endhighlight %}

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/SocketWordCount.scala).

## Output Mode

In the above code, we have used output mode complete. In last post, we used we used *append* mode. What are these signify?.

In structured streaming, output of the stream processing is a dataframe or table. The output modes of the query signify how
this infinite output table is written to the sink, in our example to console.

There are three output modes, they are

* **Append**  - In this mode, the only records which arrive in the last trigger(batch) will be written to sink.
This is supported for simple transformations like select, filter etc. As these transformations don't change the rows
which are calculated for earlier batches, appending the new rows work fine.

* **Complete** - In this mode, every time complete resulting table will be written to sink. Typically used with 
aggregation queries. In case of aggregations, the output of the result will be keep on changing as and when
the new data arrives.

* **Update** - In this mode, the only records that are changed from last trigger will be written to sink. We
will talk about this mode in future posts.

Depending upon the queries we use , we need to select appropriate output mode. Choosing wrong one result in
run time exception as below.

{% highlight scala %}

org.apache.spark.sql.AnalysisException: Append output mode not supported when there are streaming aggregations on 
streaming DataFrames/DataSets without watermark;

{% endhighlight %}

You can read more about compatibility of different queries with different output modes [here](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#output-modes).

## State Management

Once you run the program, you can observe that whenever we enter new lines it updates the global wordcount. So every time
spark processes the data, it gives complete wordcount from the beginning of the program. This indicates spark is keeping
track of the state of us. So it's a stateful wordcount.

In structured streaming, all aggregation by default stateful. All the complexities involved in keeping state across the stream
and failures is hidden from the user. User just writes the simple dataframe based code and spark figures out the intricacies  of the
state management.

It's different from the earlier DStream API. In that API, by default everything was stateless and it's user responsibility to
handle the state. But it was tedious to handle state and it became one of the pain point of the API. So in structured streaming
spark has made sure that most of the common work is done at the framework level itself. This makes writing stateful stream
processing much more simpler.

## Conclusion

We have written a stateful wordcount example using dataframe API's. We also learnt about output types and state management.
