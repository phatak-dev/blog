---
layout: post
title: "Data Source V2 API in Spark 3.0 - Part 5  : Anatomy of V2 Write API"
date : 2020-03-31
categories: scala spark spark-three datasource-v2-spark-three
---
Spark 3.0 is a major release of Apache Spark framework. It's been in preview from last December and going to have  a stable release very soon. As part of major release, Spark has a habit of shaking up API's to bring it to latest standards. There will be breaking changes also in these API's. One of such API is Data source V2 API.

Data Source V2 API, a new data source API for spark, was introduced in spark 2.3. Then it's been updated in spark 2.4. I have written detailed posts on same [here](/categories/datasource-v2-series).

This API is going to be completely changed in Spark 3.0. Spark rarely change an API this frequently in between releases. But as data source are heart of the framework, they are improved constantly. Also in spark 2.4, these API's were marked **evolving**. This means they are meant to be changed in future.

The usage of the data sources have not changed in 3.0. So if you are a user of the third party data sources you don't need to worry. These changes are geared mainly towards the developer of these sources. Also all the sources written V1 API going to work even in 3.0. So if your source is not updated, no need to panic. It's going to work without latest optimisations.

These new changes in V2 API brings more control to data source developer and better integration with spark optimiser. Moving to this API makes third party sources more performant. So in these series of posts I will be discussing the new Data source V2 API in 3.0.

This is fifth blog in the series where we discuss about different interfaces to write data in V2 API.You can read all the post in the series [here](/categories/datasource-v2-spark-three).

## Write API  with Transactions

One of the shortcoming of the data source V1 API was bare bone write API. It was not built to support complex down stream systems like databases. Also the interface has not exposed any transactional support. So it was all left to user to do all the complex handling of failures and cleanup.

Datasource V2 API addresses this shortcoming. It has transactional support in API level. This makes building more powerful connectors much easier.


## Write API Interfaces

Write API interfaces mimics the read interfaces which we saw in last few posts. The below are the different interfaces.

## SupportsWrite

This interface indicates that source supports write. This has one abstract method that needs to be overridden.

{% highlight scala %}

 def newWriteBuilder(logicalWriteInfo: LogicalWriteInfo): WriteBuilder = new MysqlWriterBuilder

{% endhighlight %}


## WriteBuilder

WriteBuilder  is interface which builds configuration. We need to override one interface for batch writing.

{% highlight scala %}

def buildForBatch(): BatchWrite

{% endhighlight %}


## BatchWrite

An interface which creates the factories for writes. 

{% highlight scala %}

def createBatchWriterFactory(physicalWriteInfo: PhysicalWriteInfo):DataWriterFactory

def commit(writerCommitMessages: Array[WriterCommitMessage]):Unit

def abort(writerCommitMessages: Array[WriterCommitMessage]): Unit

{% endhighlight %}


The Methods are

* ### createBatchWriterFactory 

Method which create data writer factories

* ### Commit

As we discussed before, the above method is part of transaction support in the writer. When all the writing is done, this method is called to commit. This is overall commit. Individual partition commit will be there in DataWriter interface which we will discuss below.

WriterCommitMessage is a message interface, that should be used by data source to define their own messages to indicate the status of each partition write.

* ### Abort

It's a mirror interface to commit. This is called whenever there is complete failure of job. This is used to cleanup the partially written data.

## DataWriterFactory

It's a factory class to create actual data writer. This code executes in each executor.

{% highlight scala %}

 def createDataWriter(partitionId:Int, taskId:Long): DataWriter[T]

{% endhighlight %}

As the name suggest, it creates data writers. The parameters are

* partitionId - Id of partition. This helps writer to understand which partition it's writing

* taskId -  Id of the task.

Method returns DataWriter. As of now, T can be Row only.

## DataWriter

Finally we have the interface which actually writes data. The below are methods 

{% highlight scala %}

def write(record: T): Unit

def commit(): WriterCommitMessage

def abort(): Unit

{% endhighlight %}

*write* method is the one which responsible for actual write. Other two methods are same as *BatchWriter* but now they work at the level of partition. These methods are responsible committing or handling write failures at partition level.

The *WriterCommitMessage* sent by commit method in this interface are the one which are sent to *BatchWrite*. This helps data source to understand status of each partition.

## Conclusion


