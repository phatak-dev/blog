---
layout: post
title: "Data Source V2 API in Spark 3.0 - Part 4 : In-Memory Data Source with Partitioning"
date : 2020-03-26
categories: scala spark spark-three datasource-v2-spark-three
---
Spark 3.0 is a major release of Apache Spark framework. It's been in preview from last December and going to have  a stable release very soon. As part of major release, Spark has a habit of shaking up API's to bring it to latest standards. There will be breaking changes also in these API's. One of such API is Data source V2 API.

Data Source V2 API, a new data source API for spark, was introduced in spark 2.3. Then it's been updated in spark 2.4. I have written detailed posts on same [here](/categories/datasource-v2-series).

This API is going to be completely changed in Spark 3.0. Spark rarely change an API this frequently in between releases. But as data source are heart of the framework, they are improved constantly. Also in spark 2.4, these API's were marked **evolving**. This means they are meant to be changed in future.

The usage of the data sources have not changed in 3.0. So if you are a user of the third party data sources you don't need to worry. These changes are geared mainly towards the developer of these sources. Also all the sources written V1 API going to work even in 3.0. So if your source is not updated, no need to panic. It's going to work without latest optimisations.

These new changes in V2 API brings more control to data source developer and better integration with spark optimiser. Moving to this API makes third party sources more performant. So in these series of posts I will be discussing the new Data source V2 API in 3.0.

This is fourth post in the series where we discuss about adding partition support for in-memory data source we built in last post.You can read all the posts in the series [here](/categories/datasource-v2-spark-three).

## Partitions
In spark, number of partitions for a given Dataframe is decided at the driver. As most of the partition logic is driven based on size of data, it's a meta data operation. 

The in-memory data source which we built in last post had only one partition. In this post, we will see how to add multiple partitions and how to read them.

## Adding Multiple Partitions

As we discussed in last blog, number of partitions for a given dataframe is decided by number of input partitions we create. That's why it was an array. As these objects are created at driver, they should not be using actual data itself.


{% highlight scala %}

class SimplePartition(val start:Int, val end:Int) extends InputPartition

class SimpleScan extends Scan with Batch{
  override def readSchema(): StructType =  StructType(Array(StructField("value", StringType)))

  override def toBatch: Batch = this

  override def planInputPartitions(): Array[InputPartition] = {
    Array(new SimplePartition(0,4),
      new SimplePartition(5,9))
  }
  override def createReaderFactory(): PartitionReaderFactory = new SimplePartitionReaderFactory()
}

{% endhighlight %}

In above code, we have **SimplePartition** which remembers it's start and end.

Then in **planInputPartitions** we are creating two partitions with start and end indices. This signals to spark that we need two partitions in dataframe.


## Passing Partitions to Partition Reader 

{% highlight scala %}

class SimplePartitionReaderFactory extends PartitionReaderFactory {
  override def createReader(partition: InputPartition): PartitionReader[InternalRow] = new
      SimplePartitionReader(partition.asInstanceOf[SimplePartition])
}

{% endhighlight %}

In above code, by casting **InputPartition* to **SimplePartition** we are making sure that right partition is sent.

## Reading From Partitions

Once we define the partitions, we need to update our *PartitionReader* to read from specific partitions. The below is the code for the same

{% highlight scala %}

class SimplePartitionReader(inputPartition: SimplePartition) extends PartitionReader[InternalRow] {

  val values = Array("1", "2", "3", "4", "5","6","7","8","9","10")

  var index = inputPartition.start

  def next = index <= inputPartition.end

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

In the above code, we get start and end from the partition objects. These will be start and end indexes. Using these two we will know which are the indexes to read.

Now each of these readers will read distinct parts of the array thus giving partition level parallelism.

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/sources/datasourcev2/SimpleMultiDataSource.scala).

## Conclusion
In this post, we discussed about how to add partitioning support for our in memory connector. The Scan API of the data source v3 allows us to separate partitioning concerns between driver and executor. 

