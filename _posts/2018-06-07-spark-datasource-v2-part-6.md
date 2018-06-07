---
layout: post
title: "Exploring Spark DataSource V2 - Part 6  : Anatomy of V2 Write API"
date : 2018-06-07
categories: scala spark  datasource-v2-series
---
In spark, data source is one of the foundational API for structured data analysis. Combined with dataframe and spark SQL abstractions, it makes spark one of the most complete structured data engine out there. You can read more about structured data analysis in spark [here](/categories/datasource-series).

Data source API was introduced in spark 1.3 along with dataframe abstraction. After that release, spark has undergone tremendous change. Spark has moved to custom memory management and with 2.0 we got Dataset , a better dataframe, abstraction. With these tremendous changes data source API needed to revisited.

So in 2.3 version, spark has released new version of data source API known as as data source V2. This API reflects all the learning spark developers learnt in last few releases. This API will be foundation for next few years of spark data source connectivity.

In this series of posts, I will be discussing about different parts of the API. We will be learning API by building data sources for different sources like flat file, relational databases etc.

This is sixth blog in the series where we discuss about different interfaces to write data in V2 API.You can read all the post in the series [here](/categories/datasource-v2-series).

## Write API  with Transactions

One of the shortcoming of the data source V1 API was bare bone write API. It was not built to support complex down stream systems like databases. Also the interface has not exposed any transactional support. So it was all left to user to do all the complex handling of failures and cleanup.

Datasource V2 API addresses this shortcoming. It has transactional support in API level. This makes building more powerful connectors much easier.

## Write API Interfaces

Write API interfaces mimics the read interfaces which we saw in last few posts. The below are the different interfaces.

## DataSourceV2 

DataSourceV2 is a marker interface. It doesn't have any methods to override. Extending this interface we indicate that we are implementing support for V2 datasource.

## WriterSupport 

WriterSupport is interface which indicates that data source supports write.

It has single method with below signature

{% highlight scala %}

def createWriter(jobId: String, schema: StructType, mode: SaveMode, 
options: DataSourceOptions): Optional[DataSourceWriter]
{% endhighlight %}

From the above signature it's clear that,it's an entry point method for data source. The below are different options

* jobId - Id of the job in which this write is happening.

* schema - Schema of DataFrame/Dataset that need to be written

* SaveMode - Different modes of write like overwrite, update etc. This is same as earlier API

* options - Map containing all the options passed to connect to the underneath source.

The method returns an optional *DataSourceWriter*. *Optional* type is a Java 8 equivalent of scala *Option*. The return type is optional here, because data source can decide not to do anything depending upon the *SaveMode*.

## DataSourceWriter

DataSourceWriter is the primary interface for actually responsible for writing. It exposes below methods 

* ### DataWriterFactory

{% highlight scala %}

 def createWriterFactory:DataWriterFactory[Row]

{% endhighlight %}

DataWriterFactory is a factory class to create actual data writers. For each partition of data it is created and sent to executors. 

It's little bit different compared to read path. In read, data source returned a list of factory objects. The reason for that is, the list indicated how many partitions needs to be created. But in case of write, spark already knows number of partitions. So no need to of list here.

* ### Commit

{% highlight scala %}

def commit(messages: Array[WriterCommitMessage]): Unit
{% endhighlight %}

As we discussed before, the above method is part of transaction support in the writer. When all the writing is done, this method is called to commit. This is overall commit. Individual partition commit will be there in DataWriter interface which we will discuss below.

WriterCommitMessage is a message interface, that should be used by data source to define their own messages to indicate the status of each partition write.

* ### Abort

{% highlight scala %}

def abort(messages: Array[WriterCommitMessage]): Unit
{% endhighlight %}

It's a mirror interface to commit. This is called whenever there is complete failure of job. This is used to cleanup the partially written data.

## DataWriterFactory

It's a factory class to create actual data writer. This code executes in each executor.

{% highlight scala %}

 def createDataWriter(partitionId:Int, attemptNumber:Int): DataWriter[T]

{% endhighlight %}

As the name suggest, it creates data writers. The parameters are

* partitionId - Id of partition. This helps writer to understand which partition it's writing

* attemptNumber - In case of writing, there can be multiple attempts. These attempts can be due to speculative run or because
  of failures

Method returns DataWriter. As of now, T can be Row only.

## DataWriter

Finally we have the interface which actually writes data. The below are methods 

{% highlight scala %}

def write(record: T): Unit

def commit(): WriterCommitMessage

def abort(): Unit

{% endhighlight %}

*write* method is the one which responsible for actual write. Other two methods are same as *DataSourceWriter* but now they work at the level of partition. These methods are responsible committing or handling write failures at partition level.

The *WriterCommitMessage* sent by commit method in this interface are the one which are sent to *DataSourceWriter*. This helps data source to understand status of each partition.

## Conclusion

Datasource V2 brings transaction support to data source writes. This makes API more powerful and flexible.
