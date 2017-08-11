---
layout: post
title: "Introduction to Spark Structured Streaming - Part 5 : File Streams"
date : 2017-08-11
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

This is the fifth post in the series. In this post, we discuss about working with file streams. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## File Streams

In last few posts, we worked with the socket stream. In this post, we will discuss about another common type of stream called file stream. File stream is
a stream of files that are read from a folder. Usually it's useful in scenarios where we have tools like flume dumping the logs from a source to HDFS folder continuously. We can treat that folder as stream and read that data into spark structured streaming.

## Support for File Types

One of the strength of batch data source API is it's support for reading wide variety of structured data. It has support for reading csv, json, parquet natively. As structured streaming extends the same API, all those files can be read in  the streaming also. You can extend the support for the other files using third party libraries.

## Csv File Stream

In our example, we will be reading data from csv source. The spark supports the csv as built in source. The below is the sample data from a file

{% highlight text %}
+-------------+----------+------+----------+
|transactionid|customerid|itemid|amountpaid|
+-------------+----------+------+----------+
|          111|         1|     1|     100.0|
|          112|         2|     2|     505.0|
|          113|         3|     3|     510.0|
|          114|         4|     4|     600.0|
|          115|         1|     2|     500.0|
+-------------+----------+------+----------+

{% endhighlight %}

The below are the steps to create a csv file stream and process the data.

### 1. Defining the Schema
As we discussed in our earlier posts, structured streaming doesn't support schema inference. So if we are reading data from csv or other sources, we need to explicitly define the schema in our program.

The below code defines a schema for csv file which we saw earlier. It uses standard dataframe schema API to do so.

{% highlight scala %}

val schema = StructType(
  Array(StructField("transactionId", StringType),
        StructField("customerId", StringType),
        StructField("itemId", StringType),
        StructField("amountPaid", StringType)))

{% endhighlight %}

### 2. Creating the Source Dataframe

Once we have schema defined, we can now define the source using *readStream* API.

{% highlight scala %}

val fileStreamDf = sparkSession.readStream
  .option("header", "true")
  .schema(schema)
  .csv("/tmp/input")

{% endhighlight %}

The above code looks very similar to reading csv data in batch API. Also it supports the same options like header.

Using *schema* method on API, we pass user defined schema. Then in *csv* method we pass the folder from which we will be reading the file.

### 3. Creating Query

Once we have source defined, we will print the data to console.

{% highlight scala %}

 val query = fileStreamDf.writeStream
      .format("console")
      .outputMode(OutputMode.Append()).start()

{% endhighlight %}

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/FileStreamExample.scala).


## Running the example

Create folder at '/tmp/input' in local or hdfs. Then start putting the csv files. As and when you put a file, you can observe that it's getting processed by the program.

## Frequency of Collection

As we have not specified any trigger, as and when new file appears in the folder, the processing will start. You can limit number of files per trigger using option *maxFilesPerTrigger*.

## Conclusion

Reading a collection of files as a stream in the structured streaming is straight forward. It supports all the file types supported by batch data source API.

