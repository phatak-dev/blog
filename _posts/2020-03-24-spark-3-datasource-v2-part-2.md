---
layout: post
title: "Data Source V2 API in Spark 3.0 - Part 2 : Anatomy of V2 Read API"
date : 2020-03-24
categories: scala spark spark-three datasource-v2-spark-three
---
Spark 3.0 is a major release of Apache Spark framework. It's been in preview from last December and going to have  a stable release very soon. As part of major release, Spark has a habit of shaking up API's to bring it to latest standards. There will be breaking changes also in these API's. One of such API is Data source V2 API.

Data Source V2 API, a new data source API for spark, was introduced in spark 2.3. Then it's been updated in spark 2.4. I have written detailed posts on same [here](/categories/datasource-v2-series).

This API is going to be completely changed in Spark 3.0. Spark rarely change an API this frequently in between releases. But as data source are heart of the framework, they are improved constantly. Also in spark 2.4, these API's were marked **evolving**. This means they are meant to be changed in future.

The usage of the data sources have not changed in 3.0. So if you are a user of the third party data sources you don't need to worry. These changes are geared mainly towards the developer of these sources. Also all the sources written V1 API going to work even in 3.0. So if your source is not updated, no need to panic. It's going to work without latest optimisations.

These new changes in V2 API brings more control to data source developer and better integration with spark optimiser. Moving to this API makes third party sources more performant. So in these series of posts I will be discussing the new Data source V2 API in 3.0.

This is the second post in the series where we discuss about different interfaces to read data in V2 API.You can read all the posts in the series [here](/categories/datasource-v2-spark-three).

## Java Interfaces

One of the characteristics of V2 API's is it's exposed in terms of Java interfaces rather than scala traits. The primary reason for this is better interop with Java. 

The below are the basic interfaces to read the data in V2 API.

## TableProvider

TableProvider trait signifies it's a source which can read or write a table. Here table is a structured dataset. It has single abstract method with below signature


{% highlight scala %}

override def getTable(options: CaseInsensitiveStringMap): Table

{% endhighlight %}

The method **getTable** takes parameters from user options. Then it returns a *Table*.

## Table

Table is an interface representing a logical structured data set of a data source. It exposes below three methods

{% highlight scala %}

def name:String

def schema:StructType

def capabilities: TableCapability

{% endhighlight %}


The different methods are

 * name : A name to identify the table.

 * schema : Table Schema. An empty schema can be returned if schema inference needed.

 * capabilities : Capabilities exposed by the table. This is one of the unique feature added in spark 3.0. This allows to specifying what kind of operation table supports. Some capabilities like BATCH_READ, BATCH_WRITE. This helps spark to verify these before attempting to run the operations.


## SupportsRead

This interface indicates that source supports read. This has one abstract method that needs to be overridden.


{% highlight scala %}

def newScanBuilder(options: CaseInsensitiveStringMap): ScanBuilder 

{% endhighlight %}


## ScanBuilder

An interface for building **Scan**. This interface can mix with filter push down to keep the needed information to push the filters to readers. The exposed abstract methods are

{% highlight scala %}

 def build(): Scan 

{% endhighlight %}


## Scan

Logical representation of a data source scan. This interface is used to provide logical information, like what the actual read schema is. This is a common scan for all different scanning like batch, micro batch etc. The needed methods to be overridden are

{% highlight scala %}

 def readSchema(): StructType

 def toBatch: Batch

{% endhighlight %}

The methods are

* readSchema - The actual schema of the source. This may look like repetitive from Table interface. The reason it is repeated because, after column pruning or other optimisation the schema may change or we may need inference of schema. This method returns actual schema of the data where as the Table one returns the initial schema.

* toBatch - This method needs to be overridden to indicate that this scan configuration should be used for batch reading.


## Batch

A physical representation of a data source scan for batch queries. This interface is used to provide physical information, like how many partitions the scanned data has, and how to read records from the partitions.

The methods that need to be overridden are

{% highlight scala %}

def planInputPartitions(): Array[InputPartition] 

def createReaderFactory(): PartitionReaderFactory 

{% endhighlight %}

The methods are

 * planInputPartitions : List of partitions for the table. This number decides number of partitions in Dataset.

 * createReaderFactory : Factory to create the readers
 

## PartitionReaderFactory

It's a factory class to create actual data readers. The data reader creation happens on the individual machines. It exposes single method.

{% highlight scala %}

 def createReader(partition: InputPartition): PartitionReader[InternalRow]

{% endhighlight %}

As the name suggest, it creates data reader.

## PartitionReader

Finally we have the interface which actually reads the data. The below are methods 

{% highlight scala %}

def next : Boolean

def get : T

def close() : Unit

{% endhighlight %}

As you can see from interface, it looks as simple iterator based reader. Currently T can be only InternalRow.

## References

[https://github.com/apache/spark/pull/22009](https://github.com/apache/spark/pull/22009)

## Conclusion

In this post we discussed some of the important interfaces from data source V2 API for reading data. You can see how this API is much different than earlier API.
