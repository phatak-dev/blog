---
layout: post
title: "Migrating to Spark 2.0 - Part 8 : Catalog API" 
date : 2017-06-20
categories: scala spark spark-two-migration-series
---

Spark 2.0 brings a significant changes to abstractions and API's of spark platform. With performance boost, this version has made some of non backward compatible changes to the framework. To keep up to date with the latest updates, one need to migrate their spark 1.x code base to 2.x. In last few weeks, I was involved in migrating one of fairly large code base and found it quite involving process. In this series of posts, I will be documenting my experience of migration so it may help all the ones out there who are planning to do the same.

This is the eighth post in this series.In this post we will discuss about catalog support in spark sql. You can access all the posts [here](/categories/spark-two-migration-series).

TL;DR You can access all the code on [github](https://github.com/phatak-dev/spark-two-migration).

## Catalog API 

In spark 1.x, spark heavily depended  on hive for all metastore related operations. Even though sqlContext supported few of the DDL operations, most of them
were very basic and not complete. So spark documentation often recommended using HiveContext over SQLContext. Also whenever user uses HiveContext, spark
support for interacting with hive metastore was limited. So most of the metastore operation's often done as embedded hive queries.

Spark 2.x changes all of this. It has exposed a full fledged user facing catalog API which works for both spark SQL and hive. Not only it supports
the spark 1.x operations, it has added many new ones to improve the interaction with metastore. 

In below sections, we will be discussing about porting earlier metastore operations to new catalog API.

## Creating Table

Before we do any DDL operations, we need to create a table. For our example, we will use temporary tables.

### Spark 1.x

{% highlight scala %}

val loadedDf = sqlContext.read.format("com.databricks.spark.csv").
  option("header", "true").load("../test_data/sales.csv")

loadedDf.registerTempTable("sales")
{% endhighlight %}

We use *registerTempTable* API for creating table in in-memory catalog.

### Spark 2.x

{% highlight scala %}

val loadedDf = sparkSession.read.format("csv").option("header", "true").load("../test_data/sales.csv")

loadedDf.createOrReplaceTempView("sales")

{% endhighlight %}

In 2.x, *registerTempTable* API is deprecated. We should use *createOrReplaceTempView* for the same.
 
## List Tables

Once we have table registered, first catalog operation is listing tables.

### Spark 1.x

{% highlight scala %}

sqlContext.tables.show() 

{% endhighlight%}

In spark 1.x, catalog API's were added to context directly. The below is the output

{% highlight text %}
+---------+-----------+
|tableName|isTemporary|
+---------+-----------+
|    sales|       true|
+---------+-----------+
{% endhighlight %}

In output, it specifies the name of the table and is it temporary or not. When we run same operation on hive metastore, *isTemporary* will be false.

### Spark 2.x 

In spark 2.x, there is separate API called *catalog* on spark session. It exposes all needed API's.

{% highlight scala %}

sparkSession.catalog.listTables.show()

{% endhighlight %}

The output looks like below

{% highlight text %}

+-----+--------+-----------+---------+-----------+
| name|database|description|tableType|isTemporary|
+-----+--------+-----------+---------+-----------+
|sales|    null|       null|TEMPORARY|       true|
+-----+--------+-----------+---------+-----------+

{% endhighlight %}

From output it is apparent that output of new catalog API is more richer than old one. Also from spark 2.x, it has added database information as
part of the catalog which was missing in earlier API's.

### List Table Names

In spark 1.x, there is a API for listing just the name of the tables. The code looks below

{% highlight scala %}

sqlContext.tableNames()

{% endhighlight %}

In spark 2.x, there is no separate API for getting database names. As listTables, returns a Dataset we can use normal spark sql API's for getting the name.

{% highlight scala %}

sparkSession.catalog.listTables.select("name").collect

{% endhighlight %}

## Caching

As part of the catalog API, we can check is given table is cached or not. 

### Spark 1.x

{% highlight scala %}

println(sqlContext.isCached("sales"))

{% endhighlight %}

### Spark 2.x

{% highlight scala %}

println(sparkSession.catalog.isCached("sales"))

{% endhighlight %}

### External Tables

Till now, we worked with tables which we creating using dataframes. Let's say we need to create table directly from a file without going through data source API. These often known as external tables. 

### Spark 1.x

In spark 1.x, SQLContext didn't support creating external tables. So we need to use hivecontext for do that.

{% highlight scala %}

val hiveContext = new HiveContext(sparkContext)
 hiveContext.setConf("hive.metastore.warehouse.dir", "/tmp")
 hiveContext.createExternalTable("sales_external", "com.databricks.spark.csv", Map(
   "path" -> "../test_data/sales.csv",
   "header" -> "true"))
 hiveContext.table("sales_external").show()
{% endhighlight %}

In above code, first we create *HiveContext*. Then we need to set the warehouse directory so hive context knows where to keep the data. Then we use
*createExternalTable* API to load the data to *sales_external* table.

### Spark 2.x

{% highlight scala %}
sparkSession.catalog.createExternalTable("sales_external", "com.databricks.spark.csv", Map(
 "path" -> "../test_data/sales.csv",
 "header" -> "true"))
sparkSession.table("sales_external").show()

{% endhighlight %}

In spark 2.x, creating external table is part of catalog API itself. We don't need to enable hive for this functionality.


## Additional API's

The above examples showed the porting of spark 1.x code to spark 2.x. But there are additional API's in spark 2.x catalog which are useful
in day to day development. Below sections discusses few of them.

## List Functions

The below code list all the functions, built in and user defined. It helps us to know what are the Udfs, Udaf's available in current session.

{% highlight scala %}

sparkSession.catalog.listFunctions.show()

{% endhighlight%}

The sample output looks as below

{% highlight text %}

+--------------------+--------+-----------+--------------------+-----------+
|                name|database|description|           className|isTemporary|
+--------------------+--------+-----------+--------------------+-----------+
|                   !|    null|       null|org.apache.spark....|       true|
|                   %|    null|       null|org.apache.spark....|       true|
|                   &|    null|       null|org.apache.spark....|       true|
|                acos|    null|       null|org.apache.spark....|       true|
|          add_months|    null|       null|org.apache.spark....|       true|
|                 and|    null|       null|org.apache.spark....|       true|
|approx_count_dist...|    null|       null|org.apache.spark....|       true|
+--------------------+--------+-----------+--------------------+-----------+

{% endhighlight %}

## List Columns

We can also list the columns of a table.

{% highlight scala %}

sparkSession.catalog.listColumns("sales").show()

{% endhighlight %}

The below is the output for our *sales* table.

{% highlight text %}

+-------------+-----------+--------+--------+-----------+--------+
|         name|description|dataType|nullable|isPartition|isBucket|
+-------------+-----------+--------+--------+-----------+--------+
|transactionId|       null|  string|    true|      false|   false|
|   customerId|       null|  string|    true|      false|   false|
|       itemId|       null|  string|    true|      false|   false|
|   amountPaid|       null|  string|    true|      false|   false|
+-------------+-----------+--------+--------+-----------+--------+

{% endhighlight %}


## Complete code

You can access complete code for spark 1.x [here](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-one/src/main/scala/com/madhukaraphatak/spark/migration/sparkone/CatalogExample.scala).

You can access complete code for spark 2.x [here](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-two/src/main/scala/com/madhukaraphatak/spark/migration/sparktwo/CatalogExample.scala).

## Conclusion

In this blog we have discussed about improvements in catalog API. Using new catalog API, you can get information of tables much easier than before.
