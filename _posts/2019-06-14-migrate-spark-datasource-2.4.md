---
layout: post
title: "Migrating to Spark 2.4 Data Source API"
date : 2019-06-14
categories: spark scala  datasource-v2-series
---
In [earlier series](/categories/datasource-v2-series/) of posts, we have discussed about new data source API that was introduced in 2.3. In 2.3, it was in beta version. In 2.4, API have been updated and marked as stable. Some of these are breaking changes.So if you are porting the datasources to 2.4 version, you need to update your code to match the latest API.


In this post, we are going to discuss the changes that introduced in the API. Also all the code what we saw in earlier series is ported to new version. This allow you to see how to update your own code.

## Reader API

In this section, we are going to see the changes that introduced for Reader API.

### DataSourceReader Interface

In DataSourceReader interaface, the function 

{% highlight scala %}

def createDataReaderFactories: List[DataReaderFactory[T]]

{% endhighlight %}

is replaced by 

{% highlight scala %}

def planInputPartitions: List[InputPartition]

{% endhighlight %}

As you can see in this, factory is replaced by partition. 


### DataReaderFactory to InputPartition

**DataReaderFactory** interface is renamed to  **InputPartition**.

Also the method 

{% highlight scala %}

def createDataReader: DataReader[Row]

{% endhighlight %}

is replaced by

{% highlight scala %}

def createPartitionReader:InputPartitionReader[InternalRow]

{% endhighlight %}


### DataReader to PartitionReader

**DataReader** interface is renamed to **InputPartitionReader**.

Also the method

{% highlight scala %}

def createDataReader

{% endhighlight %}

is replaced by

{% highlight scala %}

def createPartitionReader

{% endhighlight %}


### Row to InternalRow

In earlier API's, the interfaces used to take *Row*. All that is changed to **InternalRow**. This allows all the data sources load data in more efficient way. 

## Writer API

Compared to Reader API, writer API has gone very minimal changes. In this section we are going to discuss about those changes.


### DataWriterFactory[Row] to DataWriterFactory[InternalRow]

As we discussed in earlier section, **Row** is changed to **InternalRow**

### DataWriterFactory Interface 

In DataWriterFactory interface, the method 

{% highlight scala %}

def createDataWriter(partitionId: Int, attemptNumber: Int)

{% endhighlight %}

is changed to

{%  highlight scala %}

def createDataWriter(partitionId: Int, taskId:Long,epochId: Long)

{% endhighlight %}

**epochId** is added to support spark structured streaming.


## Code

You can access the port of earlier code on [github](https://github.com/phatak-dev/spark2.0-examples/commit/1cfba393a09c2d4a012f29f7a6579ff5efc1dd53).

## Conclusion

Spark 2.4 has updated API's for newly introduced data source API. Now the API is marked as stable and going to serve the spark developers for long.
