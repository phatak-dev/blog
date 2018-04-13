---
layout: post
title: "Exploring Spark DataSource V2 - Part 3  : In Memory Datasource"
date : 2018-03-28
categories: scala spark datasource-series datasource-v2-series
---

<< Introduction>>

This is third blog in the series where we discuss about building simple in memory datasource. You can read all the post in the series [here](/categories/datasource-v2-series).

## InMemory DataSource

In this post we are going to build a datasource which reads the data from an array. It will have single partition. This simple example helps us to understand how to implement all interfaces we discussed in last blog.

The below are the steps to implement a simple in memory data source.

## Create Unique Package

All the datasources in spark are discovered using their packages. So as the first step we create a package for our datasource.

{% highlight scala %}

package com.madhukaraphatak.examples.sparktwo.datasourcev2.simple

{% endhighlight %}

## DefaultSource

Spark searches for a class named *DefaultSource* in a given data source package. So we create *DefaultSource* class in the package. It should extend *DatasourceV2* and *ReaderSupport*.

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
    factoryList                                                                                                                     }

}

{% endhighlight %}

In above code, we are hardcoding the schema. Our data will have single column called *value*. Also *createDataReaderFactories* return single data factory which signifies there is single partition.

## SimpleDataSourceReaderFactory

{% highlight scala %}

class SimpleDataSourceReaderFactory extends DataReaderFactory[Row] with DataReader[Row] {
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

In our code, *SimpleDataSourceReaderFactory* implements both factory and data reader interfaces. The data for the datasource comes from *values* array. 

DataReader tracks the reading using *index* variable. Other part of code is mostly self explanatory. 

## Using DataSource

The below is the code to read the data from this source

{% highlight scala %}

val simpleDf = sparkSession.read.format("com.madhukaraphatak.examples.sparktwo.datasourcev2.simple")
      .load() 
{% endhighlight %}

From above code it's apparent that reading from v2 source is exactly same as v1 source. So user of these connectors it will be transparent.

We can print the data and number partitions with below code

{% highlight scala %}

    simpleDf.show()
    println("number of partitions in simple source is "+simpleDf.rdd.getNumPartitions)
{% endhighlight %} 

## Conclusion

In this post we discussed how to implement a simple in memory data source using datasource V2 API's.
