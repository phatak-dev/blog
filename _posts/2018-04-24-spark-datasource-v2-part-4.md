---
layout: post
title: "Exploring Spark DataSource V2 - Part 4  : In-Memory DataSource with Partitioning"
date : 2018-04-24
categories: scala spark datasource-v2-series
---

In spark, data source is one of the foundational API for structured data analysis. Combined with dataframe and spark SQL abstractions, it makes spark one of the most complete structured data engine out there. You can read more about structured data analysis in spark [here](/categories/datasource-series).

Data source API was introduced in spark 1.3 along with dataframe abstraction. After that release, spark has undergone tremendous change. Spark has moved to custom memory management and with 2.0 we got Dataset , a better dataframe, abstraction. With these tremendous changes data source API needed to revisited.

So in 2.3 version, spark has released new version of data source API known as as data source V2. This API reflects all the learning spark developers learnt in last few releases. This API will be foundation for next few years of spark data source connectivity.

In this series of posts, I will be discussing about different parts of the API. We will be learning API by building data sources for different sources like flat file, relational databases etc.


This is fourth blog in the series where we discuss about adding partition support for in-memory data source we built in last post. You can read all the posts in the series [here](/categories/datasource-v2-series).

## Partitions
In spark, number of partitions for a given Dataframe is decided at the driver. As most of the partition logic is driven based on size of data, it's a metadata operation. 

The in-memory data source which we built in last post had only one partition. In this post, we will see how to add multiple partitions and how to read them.

## Adding Multiple Partitions

As we discussed in last blog, number of partitions for a given dataframe is decided by number of data factory objects we create. That's why it was an array. As these factory objects are created at driver, they should not be using actual data itself.

{% highlight scala %}
class SimpleDataSourceReader extends DataSourceReader {

def readSchema() = StructType(Array(StructField("value", StringType)))

def createDataReaderFactories = {
    val factoryList = new java.util.ArrayList[DataReaderFactory[Row]]
    factoryList.add(new SimpleDataSourceReaderFactory(0, 4))
    factoryList.add(new SimpleDataSourceReaderFactory(5, 9))                                                               factoryList
  }
}

{% endhighlight %}

In above code, we have created two factories. Each one of them takes the index in the array which they need to read. This indexes make sure that we are reading distinct data in each partition.

As we created two factories, we will have two partitions in resulting dataframe.

## Reading From Partitions

Once we define the partitions, we need to update our *DatasourceReader* to read from specific partitions. The below is the code for the same

{% highlight scala %}
class SimpleDataSourceReaderFactory(var start: Int, var end: Int) 
         extends DataReaderFactory[Row] with DataReader[Row] {

  def createDataReader = new 
      SimpleDataSourceReaderFactory(start, end)
                                                                                                                             val values = Array("1", "2", "3", "4", 
                "5", "6", "7", "8", "9", "10")
  var index = 0

  def next = start <= end

  def get = {
    val row = Row(values(start))
    start = start + 1
    row
  }

  def close() = Unit
}

{% endhighlight %}

In the above code, we get start and end from the factory objects. These will be start and end indexes. Using these two we will know which are the indexes to read.

Now each of these readers will read distinct parts of the array thus giving partition level parallelism.

You can access complete code of the connector on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/datasourcev2/SimpleMultiDataSource.scala).

## Printing Number of Partitions

The below is the code to read the data using above connector and checking the number of partitions. When you run the code it should print 2.

{% highlight scala %}

val simpleMultiDf = sparkSession.read.
    format("com.madhukaraphatak.examples.sparktwo.datasourcev2.simplemulti")
    .load()            
simpleMultiDf.show()
println("number of partitions in simple 
       multi source is "+simpleMultiDf.rdd.getNumPartitions)
{% endhighlight %}


## Conclusion
In this post, we discussed about how to add partitioning support for our in memory connector. The factory API of the datasource v2 allows us to separate partitioning concerns between driver and executor. 
