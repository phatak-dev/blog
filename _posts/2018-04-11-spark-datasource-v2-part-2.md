---
layout: post
title: "Exploring Spark DataSource V2 - Part 2  : Anatomy of V2 Read API"
date : 2018-04-11
categories: scala spark datasource-series datasource-v2-series
---
In spark, data source is one of the foundational API for structured data analysis. Combined with dataframe and spark SQL abstractions, it makes spark one of the most complete structured data engine out there. You can read more about structured data analysis in spark [here](/categories/datasource-series).

Data source API was introduced in spark 1.3 along with dataframe abstraction. After that release, spark has undergone tremendous change. Spark has moved to custom memory management and with 2.0 we got Dataset , a better dataframe, abstraction. With these tremendous changes data source API needed to revisited.

So in 2.3 version, spark has released new version of data source API known as as data source V2. This API reflects all the learning spark developers learnt in last few releases. This API will be foundation for next few years of spark data source connectivity.

In this series of posts, I will be discussing about different parts of the API. We will be learning API by building data sources for different sources like flat file, relational databases etc.

This is second blog in the series where we discuss about different interfaces to read data in V2 API.Understanding these interfaces helps us to understand the design and motivation of v2 API.You can read all the post in the series [here](/categories/datasource-v2-series).

## Java Interfaces

One of the characteristics of V2 API's is it's exposed in terms of Java interfaces rather than scala traits. The primary reason for this is better interop with java. 

The below are the basic interfaces to read the data in V2 API.

## DataSourceV2 

DataSourceV2 is a marker interface. It doesn't have any methods to override. Extending this interface we indicate that we are implementing support for V2 datasource.

## ReaderSupport 

ReaderSupport is interface which indicates that datasource supports reading that. There is similar interface for write also. In this post we only focus on the reading.

It has single method with below signature

{% highlight scala %}

 def createReader(options: DataSourceOptions):DataSourceReader

{% endhighlight %}

From the above signature it's clear that,it's an entry point method for data source. *DataSourceOptions* is a map which contains all the options specified by the  user to this data source. This method creates *DataSourceReader*.

## DataSourceReader

DataSourceReader is the primary interface for actually responsible for two things of reading. They are

* ### Schema Inference

{% highlight scala %}
 def readSchema():StructType

{%endhighlight %}

The above method is responsible for schema inference.

* ### DataReader Factory

{% highlight scala %}

 def createDataReaderFactories:List[DataReaderFactory]

{% endhighlight %}

DataReaderFactory is a factory class to create actual data readers. Number of data factories determines the number of partitions in the resulting DataFrame.

## DataReaderFactory

It's a factory class to create actual data readers. The data reader creation happens on the individual machines. It exposes single method.

{% highlight scala %}

 def createDataReader:DataReader 

{% endhighlight %}

As the name suggest, it creates data reader.

## DataReader

Finally we have the interface which actually reads the data. The below are methods 

{% highlight scala %}

def next : Boolean

def get : T

{% endhighlight %}

As you can see from interface, it looks as simple iterator based reader. Currently T can be only Row.


## No Depednecies on High Level API

One of the observation from above the API's, there is no more dependency on RDD, DataFrame etc. This means these API's can be easily evolved independent of change in user facing abstractions.This is one of the design goals of the API to overcome limitation of earlier API.

## References

[Spark Jira For Data Source V2](https://issues.apache.org/jira/browse/SPARK-15689).

[DataBricks blog on 2.3](https://databricks.com/blog/2018/02/28/introducing-apache-spark-2-3.html).


## Conclusion

In this post we discussed some of the important interfaces from datasource V2 API for reading data. You can see how this API is much different than earlier API.
