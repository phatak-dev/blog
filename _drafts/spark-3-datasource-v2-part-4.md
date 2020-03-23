---
layout: post
title: "Datasource V2 API in Spark 3.0 - Part 4  : In-Memory DataSource with Partitioning"
date : 2020-03-22
categories: scala spark datasource-v2-series spark-three datasource-v2-spark-three
---

<< Introduction >>

This is fourth blog in the series where we discuss about adding partition support for in-memory data source we built in last post.You can read all the posts in the series [here](/categories/datasource-v2-spark-three).

## Partitions
In spark, number of partitions for a given Dataframe is decided at the driver. As most of the partition logic is driven based on size of data, it's a metadata operation. 

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


## Passing Partitions to Partition Reader 

{% highlight scala %}

class SimplePartitionReaderFactory extends PartitionReaderFactory {
  override def createReader(partition: InputPartition): PartitionReader[InternalRow] = new
      SimplePartitionReader(partition.asInstanceOf[SimplePartition])
}

{% endhighlight %}

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
In this post, we discussed about how to add partitioning support for our in memory connector. The Scan API of the datasource v3 allows us to separate partitioning concerns between driver and executor. 

