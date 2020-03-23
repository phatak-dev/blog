---
layout: post
title: "Datasource V2 API in Spark 3.0 - Part 3  : In-Memory DataSource"
date : 2020-03-22
categories: scala spark datasource-v2-series spark-three datasource-v2-spark-three
---

<< Introduction >>

This is third blog in the series where we discuss about building simple in-memory datasource. You can read all the posts in the series [here](/categories/datasource-v2-spark-three).


## In-Memory DataSource

In this post we are going to build a datasource which reads the data from an array. It will have single partition. This simple example helps us to understand how to implement all interfaces we discussed in last blog.

The below are the steps to implement a simple in memory data source.

## Create Unique Package

All the datasources in spark are discovered using their packages. So as the first step we create a package for our datasource.

{% highlight scala %}

package com.madhukaraphatak.spark.sources.datasourcev2.simple

{% endhighlight %}

## DefaultSource

Spark searches for a class named *DefaultSource* in a given data source package. So we create *DefaultSource* class in the package. It should extend *TableProvider** interface

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

In above code, we are hard coding the schema. Our data will have single column called *value*. Also *newScanBuilder* return our scanner. Also we are specifing the source hs batch reading capabalities.

## SimpleScanBuilder

{%  highlight scala %}

class SimpleScanBuilder extends ScanBuilder {
  override def build(): Scan = new SimpleScan
}

{% endhighlight %}

This code creates simple scan builder without any pushdown etc.


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

As we have single partition in **planInputPartitions** there will be single partitition in dataframe. By overriding **toBatch** we signified that, we are using this scan for reading in the batch mode.

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

In above code, *SimplePartitionReader* implements data reader interfaces. The data for the datasource comes from *values* array. 

Reader tracks the reading using *index* variable. In get method, we create *Row* object with single value from the array.

## Using DataSource

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

In this post we discussed how to implement a simple in memory data source using datasource V2 API's.
