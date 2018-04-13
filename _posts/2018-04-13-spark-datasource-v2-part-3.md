---
layout: post
title: "Exploring Spark DataSource V2 - Part 3  : In-Memory DataSource"
date : 2018-04-13
categories: scala spark datasource-v2-series
---

In spark, data source is one of the foundational API for structured data analysis. Combined with dataframe and spark SQL abstractions, it makes spark one of the most complete structured data engine out there. You can read more about structured data analysis in spark [here](/categories/datasource-series).

Data source API was introduced in spark 1.3 along with dataframe abstraction. After that release, spark has undergone tremendous change. Spark has moved to custom memory management and with 2.0 we got Dataset , a better dataframe, abstraction. With these tremendous changes data source API needed to revisited.

So in 2.3 version, spark has released new version of data source API known as as data source V2. This API reflects all the learning spark developers learnt in last few releases. This API will be foundation for next few years of spark data source connectivity.

In this series of posts, I will be discussing about different parts of the API. We will be learning API by building data sources for different sources like flat file, relational databases etc.


This is third blog in the series where we discuss about building simple in-memory datasource. You can read all the posts in the series [here](/categories/datasource-v2-series).


## In-Memory DataSource

In this post we are going to build a datasource which reads the data from an array. It will have single partition. This simple example helps us to understand how to implement all interfaces we discussed in last blog.

The below are the steps to implement a simple in memory data source.

## Create Unique Package

All the datasources in spark are discovered using their packages. So as the first step we create a package for our datasource.

{% highlight scala %}

package com.madhukaraphatak.examples.sparktwo.datasourcev2.simple

{% endhighlight %}

## DefaultSource

Spark searches for a class named *DefaultSource* in a given data source package. So we create *DefaultSource* class in the package. It should extend *DatasourceV2* and *ReaderSupport* interfaces.

{% highlight scala %}

class DefaultSource extends DataSourceV2 with ReadSupport {

  def createReader(options: DataSourceOptions) = new SimpleDataSourceReader()  
}

{% endhighlight %}

## SimpleDataSourceReader

{% highlight scala %}

class SimpleDataSourceReader extends DataSourceReader {

  def readSchema() = StructType(Array(StructField("value", StringType)))
                                        
  def createDataReaderFactories = {
    val factoryList = new java.util.ArrayList[DataReaderFactory[Row]]
    factoryList.add(new SimpleDataSourceReaderFactory())
    factoryList 
   }
}

{% endhighlight %}

In above code, we are hard coding the schema. Our data will have single column called *value*. Also *createDataReaderFactories* return single data factory which signifies that, there is single partition.

## SimpleDataSourceReaderFactory

{% highlight scala %}

class SimpleDataSourceReaderFactory extends 
         DataReaderFactory[Row] with DataReader[Row] {
  def createDataReader = new SimpleDataSourceReaderFactory()
  val values = Array("1", "2", "3", "4", "5")

  var index = 0

  def next = index < values.length

  def get = {
    val row = Row(values(index))
    index = index + 1
    row
  }

  def close() = Unit
}

{% endhighlight %}

In above code, *SimpleDataSourceReaderFactory* implements both factory and data reader interfaces. The data for the datasource comes from *values* array. 

DataReader tracks the reading using *index* variable. In get method, we create *Row* object with single value from the array.

## Using DataSource

The below is the code to read the data from this source

{% highlight scala %}

val simpleDf = sparkSession.read
               .format("com.madhukaraphatak.examples.
                       sparktwo.datasourcev2.simple")
      .load() 
{% endhighlight %}

From above code it's apparent that reading from v2 source is exactly same as v1 source. So for user of these connectors it will be transparent.

We can print the data and number partitions with below code

{% highlight scala %}

    simpleDf.show()
    println("number of partitions in simple source is "+simpleDf.rdd.getNumPartitions)
{% endhighlight %} 

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/datasourcev2/SimpleDataSource.scala).

## Conclusion

In this post we discussed how to implement a simple in memory data source using datasource V2 API's.
