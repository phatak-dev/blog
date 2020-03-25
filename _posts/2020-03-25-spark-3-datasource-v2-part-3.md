---
layout: post
title: "Data Source V2 API in Spark 3.0 - Part 3 : In-Memory Data Source"
date : 2020-03-25
categories: scala spark spark-three datasource-v2-spark-three
---
Spark 3.0 is a major release of Apache Spark framework. It's been in preview from last December and going to have  a stable release very soon. As part of major release, Spark has a habit of shaking up API's to bring it to latest standards. There will be breaking changes also in these API's. One of such API is Data source V2 API.

Data Source V2 API, a new data source API for spark, was introduced in spark 2.3. Then it's been updated in spark 2.4. I have written detailed posts on same [here](/categories/datasource-v2-series).

This API is going to be completely changed in Spark 3.0. Spark rarely change an API this frequently in between releases. But as data source are heart of the framework, they are improved constantly. Also in spark 2.4, these API's were marked **evolving**. This means they are meant to be changed in future.

The usage of the data sources have not changed in 3.0. So if you are a user of the third party data sources you don't need to worry. These changes are geared mainly towards the developer of these sources. Also all the sources written V1 API going to work even in 3.0. So if your source is not updated, no need to panic. It's going to work without latest optimisations.

These new changes in V2 API brings more control to data source developer and better integration with spark optimiser. Moving to this API makes third party sources more performant. So in these series of posts I will be discussing the new Data source V2 API in 3.0.

This is third post in the series where we discuss about building simple in-memory data source. You can read all the posts in the series [here](/categories/datasource-v2-spark-three).


## In-Memory Data Source

In this post we are going to build a data source which reads the data from an array. It will have single partition. This simple example helps us to understand how to implement all interfaces we discussed in last blog.

The below are the steps to implement a simple in memory data source.

## Create Unique Package

All the data sources in spark are discovered using their packages. So as the first step we create a package for our data source.

{% highlight scala %}

package com.madhukaraphatak.spark.sources.datasourcev2.simple

{% endhighlight %}

## DefaultSource

Spark searches for a class named *DefaultSource* in a given data source package. So we create *DefaultSource* class in the package. It should extend *TableProvider** interface.

{% highlight scala %}

class DefaultSource extends TableProvider{

  override def getTable(options: CaseInsensitiveStringMap): Table = 
         new SimpleBatchTable()
}

{% endhighlight %}

## SimpleBatchTable

{% highlight scala %}

class SimpleBatchTable extends Table with SupportsRead {
  override def name(): String = this.getClass.toString

  override def schema(): StructType = StructType(Array(StructField("value", StringType)))

  override def capabilities(): util.Set[TableCapability] = Set(TableCapability.BATCH_READ).asJava

  override def newScanBuilder(options: CaseInsensitiveStringMap): ScanBuilder = new SimpleScanBuilder()
}

{% endhighlight %}

In above code, we are hard coding the schema. Our data will have single column called *value*. Also *newScanBuilder* return our scanner. Also we are specifying the source has batch reading capabilities.

## SimpleScanBuilder

{%  highlight scala %}

class SimpleScanBuilder extends ScanBuilder {
  override def build(): Scan = new SimpleScan
}

{% endhighlight %}

This code creates simple scan builder without any extra mixins.


## Scan and Batch

{% highlight scala %}

class SimplePartition extends InputPartition

class SimpleScan extends Scan with Batch{
  override def readSchema(): StructType =  StructType(Array(StructField("value", StringType)))

  override def toBatch: Batch = this

  override def planInputPartitions(): Array[InputPartition] = {
    Array(new SimplePartition())
  }
  override def createReaderFactory(): PartitionReaderFactory = new SimplePartitionReaderFactory()
}

{% endhighlight %}

As we have single partition in **planInputPartitions** there will be single partition in dataframe. By overriding **toBatch** we signified that, we are using this scan for reading in the batch mode.

##  PartitionReaderFactory

{% highlight scala %}

class SimplePartitionReaderFactory extends PartitionReaderFactory {
  override def createReader(partition: InputPartition)
            : PartitionReader[InternalRow] = new SimplePartitionReader
}

{% endhighlight %}

## Partition Reader

{% highlight scala %}

class SimplePartitionReader extends PartitionReader[InternalRow] {

  val values = Array("1", "2", "3", "4", "5")

  var index = 0

  def next = index < values.length

  def get = {
    val stringValue = values(index)
    val stringUtf = UTF8String.fromString(stringValue)
    val row = InternalRow(stringUtf)
    index = index + 1
    row
  }

  def close() = Unit

}

{% endhighlight %}

In above code, *SimplePartitionReader* implements data reader interfaces. The data for the data source comes from *values* array. 

Reader tracks the reading using *index* variable. In get method, we create *Row* object with single value from the array.

## Using Data Source

The below is the code to read the data from this source

{% highlight scala %}

val simpleDf = sparkSession.read
      .format("com.madhukaraphatak.spark.sources.datasourcev2.simple")
      .load()
{% endhighlight %}

From above code it's apparent that reading from v2 source is exactly same as v1 source. So for user of these connectors it will be transparent.

We can print the data and number partitions with below code

{% highlight scala %}

    simpleDf.show()
    println("number of partitions in simple source is "+simpleDf.rdd.getNumPartitions)
{% endhighlight %} 

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/sources/datasourcev2/SimpleDataSource.scala).

## Conclusion

In this post we discussed how to implement a simple in memory data source using new data source V2 API's.
