---
layout: post
title: "Introduction to Spark 2.0 - Part 1 : Spark Session API"
date : 2016-05-11
categories: scala spark spark-two
---
Spark 2.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 2.0.

This is the first blog in series, where I will be discussing about new entry point in the framework. You can access all the posts in the series [here](/categories/spark-two/).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark2.0-examples).


### Dataset - New Abstraction of Spark

For long, RDD was the standard abstraction of Spark. But from Spark 2.0, Dataset will become the new abstraction layer for spark. Though RDD API will be available, it will become low level API, used mostly for runtime and library development. All user land code will be written against the Dataset abstraction and it's subset Dataframe API.

Dataset is a superset of Dataframe API which is released in Spark 1.3. Dataset together with Dataframe API brings better performance and flexibility to the platform compared to RDD API. Dataset will be also replacing RDD as an abstraction for streaming in future releases.

### SparkSession - New entry point of Spark

In earlier versions of spark, spark context was entry point for Spark. As RDD was main API, it was created and manipulated using context API's. For every other API,we needed to use different contexts.For streaming, we needed StreamingContext, for SQL sqlContext and for hive HiveContext. But as DataSet and Dataframe API's are becoming new standard API's we need an entry point build for them. So in Spark 2.0, we have a new entry point for DataSet and Dataframe API's called as Spark Session.

SparkSession is essentially combination of SQLContext, HiveContext and future StreamingContext. All the API's available on those contexts are available on spark session also. Spark session internally has a spark context for actual computation.

So in rest of our post, we will discuss how to create and interact with Spark session.

### Creating SparkSession 

SparkSession follows builder factory design pattern. The below is the code to create a spark session.

{%highlight scala %}

 val sparkSession = SparkSession.builder.
      master("local")
      .appName("spark session example")
      .getOrCreate()

{%endhighlight %}

The above is similar to creating an SparkContext with local and creating an SQLContext wrapping it. If you need to create, hive context you can use below code to create spark session with hive support.


{%highlight scala %}

  val sparkSession = SparkSession.builder.
      master("local")
      .appName("spark session example")
      .enableHiveSupport()
      .getOrCreate()

{%endhighlight %}

**enableHiveSupport** on factory enables hive support which is similiar to HiveContext.

Once we have created spark session, we can use it to read the data.

## Read data using Spark Session

The below code is reading data from csv using spark session.

{%highlight scala %}
    val df = sparkSession.read.option("header","true").
    csv("src/main/resources/sales.csv")

{%endhighlight %}

It looks like exactly like reading using SQLContext. You can easily replace all your code of SQLContext with SparkSession now.

You can access complete code [here](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/SparkSessionExample.scala).

### Is SQLContext and HiveContext going away?

Not really. Spark is big on backward compatibility. So these API's are still available. Though from documentation it's clear that they want developers to use SparkSession going forward.


So in this post, we learnt about new spark API called SparkSession. This will be our new entry point of spark code in future.


## References

Apache Spark 2.0 presented by Databricks co-founder Reynold Xin - [https://www.brighttalk.com/webcast/12891/202021](https://www.brighttalk.com/webcast/12891/202021)







