---
layout: post
title: "Exploring Spark Data Source V2 - Part 1  : Limitations of Data Source V1 API"
date : 2018-04-11
categories: scala spark  datasource-v2-series
---

In spark, data source is one of the foundational API for structured data analysis. Combined with dataframe and spark SQL abstractions, it makes spark one of the most complete structured data engine out there. You can read more about structured data analysis in spark [here](/categories/datasource-series).

Data source API was introduced in spark 1.3 along with dataframe abstraction. After that release, spark has undergone tremendous change. Spark has moved to custom memory management and with 2.0 we got Dataset , a better dataframe, abstraction. With these tremendous changes data source API needed to revisited.

So in 2.3 version, spark has released new version of data source API known as as data source V2. This API reflects all the learning spark developers learnt in last few releases. This API will be foundation for next few years of spark data source connectivity.

In this series of posts, I will be discussing about different parts of the API. We will be learning API by building data sources for different sources like flat file, relational databases etc.

This is first blog in the series where we discuss about the limitations of existing data source API. It will helps us to understand the motivation for new API.You can read all the post in the series [here](/categories/datasource-v2-series).


## Limitations of Data Source V1 API


### 1. Dependence on SQL Context and DataFrame 

Typically data sources are lowest level abstractions in a data analysis stack. In data source v1, this layer depended upon higher level API likes SQLContext , Dataframes and RDD.

The below are few of the interfaces of v1 API.

{% highlight scala %}

override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation

override def buildScan(): RDD[Row]

{% endhighlight %}

As you can see in the above code, these API's are using higher level user facing API's.

In spark 2.0 SQLContext got deprecated. It got replaced by SparkSession. Also DataFrame superseded by Dataset API. But spark has not able to update data source API to reflect these changes. 

So as we can see high level API's evolve over time. Having data source , which is a lower layer abstraction, depending upon the higher level abstractions is not a good idea.

### 2. Lack of Support for Columnar Read

As you can see from above *buildScan* API, spark data source reads data in Row format. Even though internal spark engine supports columnar data representation it's not exposed to data sources. But many data sources used for analytics are columnar by nature. So there is unnecessary translation of columnar data source to row in connector and back to columnar in spark engine.

To avoid this in internal columnar format like parquet spark uses internal API's. But it's not possible for third party libraries.This impacts their performance.

### 3. Lack of Partitioning and Sorting Info

In data source v1 API, a data source cannot pass the partition information to spark engine. This is not good for databases like Hbase/ Cassandra which have optimised for partition access. In data source V1 API , when spark reads data from these sources it will not try to co locate the processing with partitions which will result in poor performance.

Spark built in sources overcome this limitation using the internal API's. That's why spark inbuilt sources much more performant than third party ones

### 4. No transaction support in Write Interface

Current write interface is very generic. It was built primarily to support to store data in systems like HDFS. But more sophisticated sinks like databases needed more control over data write. For example, when data is written to partially to database and job aborts, it will not cleanup those rows. It's not an issue in HDFS because it will track non successful writes using *_successful* file . But those facilities are not there in databases. So in this scenario, database will be in inconsistent state. Databases handle these scenarios using transactions which is not supported in current data source API's.

### 5. Limited Extendability 

Current data source API only supports filter push down and column pruning. But many smart sources, data sources with processing power, do more capabilities than that. Currently data source API doesn't good mechanism to push more catalyst expressions to underneath source. 

As we can see from above points, current data source API is not adequate for these new generation data sources. 

## References

[Spark Jira For Data Source V2](https://issues.apache.org/jira/browse/SPARK-15689).

[DataBricks blog on 2.3](https://databricks.com/blog/2018/02/28/introducing-apache-spark-2-3.html).


## Conclusion

Data source API is one of the foundation API of spark. It need to be evolved to support new territory spark is going with 2.0 . In this post we discussed about different limitations of current data source API which will be motivation for new API. 
