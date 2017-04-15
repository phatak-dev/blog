---
layout: post
title: "Migrating to Spark 2.0 - Part 2 : Built-in CSV Connector" 
date : 2017-04-15
categories: scala spark spark-two-migration-series
---

Spark 2.0 brings a significant changes to abstractions and API's of spark platform. With performance boost, this version has made some of non backward compatible changes to the framework. To keep up to date with the latest updates, one need to migrate their spark 1.x code base to 2.x. In last few weeks, I was involved in migrating one of fairly large code base and found it quite involving process. In this series of posts, I will be documenting my experience of migration so it may help all the ones out there who are planning to do the same.

This is the second post in this series. In this post, we will discuss about built-in csv connector. You can access all the posts [here](/categories/spark-two-migration-series).

TL;DR You can access all the code on [github](https://github.com/phatak-dev/spark-two-migration).

## CSV Source Connector in Spark

In spark 1.x, csv connector was provided using, [spark-csv](/analysing-csv-data-in-spark), a third party library  by databricks. But in spark 2.0, they have made csv a built-in source. This decision is primarily driven by the fact that csv is one of the major data formats used in enterprises.So when you are migrating to spark 2.0 you need to move your code to use the built in csv source rather than using third party one.

## Migrating to New Connector

The steps for migrating from old connector to new one are as below.

* ### Removing Dependency

The first step to migrate code is to remove spark-csv dependency from the build. This makes sure that it doesn't conflict with built in connector.

* ### Migrating Code

The below code snippets show the changes need to migrate code. It's relatively small change as built-in connector preserves all the same options
that were available in spark-csv.

If you have below code in spark 1.x
{% highlight scala %}

val loadedDF = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").load("../test_data/sales.csv")

{% endhighlight %}

Migrate the code to spark 2.0 as below

{% highlight scala %}

val loadedDF = sqlContext.read.format("csv").option("header", "true").load("../test_data/sales.csv")
{% endhighlight %}

As you can see from the code, you need to replace the source from *com.databricks.spark.csv* to *csv*. This will migrate your code to use built in spark connector.

You can access complete code on github for [1.x](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-one/src/main/scala/com/madhukaraphatak/spark/migration/sparkone/CsvLoad.scala) and [2.x](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-two/src/main/scala/com/madhukaraphatak/spark/migration/sparktwo/CsvLoad.scala). 


## Advantages of Built-in CSV Connector

Now, if both connector provides the same API, you may wonder what's the advantage of the upgrading to built in source. The below are the some of the advantages.

* ### No third party dependency

As this connector is built in to spark, we don't have to depend upon any third party library jars. This is makes playing with csv much easier in spark-shell or any
other interactive tools. Also it simplifies the dependency graph of our projects.

* ### Better Performance in Schema Inference

Csv connector has an option to do schema inference. As third party library, earlier connector was pretty slow to do the schema inference. But
now the connector is built in to the spark it can use some of the optimised internal API's to do it much faster.

The below is the comparison for schema inference on 700mb data with 29 columns.I am using airlines data for year 2007 from [here](http://stat-computing.org/dataexpo/2009/the-data.html). It's zipped. When you unzip, you get csv file on which the tests are done. Test is done on spark-shell with master as *local*.

The results as below

**Spark-Csv connector   51s**

**Built-in  connector   20s**


As you can see from the results, built-in connector is almost twice as fast as earlier one.

* ### Support For Broadcast Join

Earlier spark-csv connector didn't support broadcast join. So joins are very slow when we combine big dataframes with small ones for csv data. But
now built-in connector supports the broadcast joins which vastly improves the performance of joins.

So I have created another small file with first 10000 rows of 2007.csv which is around 1mb. When we join the data on *Year* column using below code

#### Join code

{% highlight scala %}

val joinedDf = df.join(smalldf, df.col("Year") === smalldf.col("Year"))

joinedDf.count
{% endhighlight %}

Here *df* dataframe on 700mb data and *smalldf* on 1 mb. We are running count to force spark to do complete join.


I observed below results. 

**Spark-Csv connector 52 min**

**Built-in Connector 4.1 min**


As you can see, there is huge difference between join performance. The difference comes as spark built-in connector uses BroadcastJoin where as spark-csv uses SortMergeJoin. So when you migrate to built-in connector you will observe a significant improvement in the join performance.

## Conclusion

So whenever you are moving to spark 2.0, use built in csv connector. It preserves the same API  and gives better performance than older connector.

## What's Next?

In next blog, we will be discuss about migrating rdd based code in spark 2.0

