---
layout: post
title: "Migrating to Spark 2.0 - Part 9 : Hive Integration" 
date : 2017-06-23
categories: scala spark spark-two-migration-series
---

Spark 2.0 brings a significant changes to abstractions and API's of spark platform. With performance boost, this version has made some of non backward compatible changes to the framework. To keep up to date with the latest updates, one need to migrate their spark 1.x code base to 2.x. In last few weeks, I was involved in migrating one of fairly large code base and found it quite involving process. In this series of posts, I will be documenting my experience of migration so it may help all the ones out there who are planning to do the same.

This is the ninth post in this series.In this post we will discuss about hive integration in spark. You can access all the posts [here](/categories/spark-two-migration-series).

TL;DR You can access all the code on [github](https://github.com/phatak-dev/spark-two-migration).

## Hive Integration in Spark

From very beginning for spark sql, spark had good integration with hive. Hive was primarily used for the sql parsing in 1.3 and for 
metastore and catalog API's in later versions. In spark 1.x, we needed to use HiveContext for accessing HiveQL and the hive metastore.

From spark 2.0, there is no more extra context to create. It integrates directly with the spark session. Also the catalog API, which we discussed in last [post](/migrating-to-spark-two-part-8) will be available for hive metastore also.

In below sections, we will discuss how to use hive using spark 2.0 API's. This will help you to migrate your HiveContext code to the new code.

## Enabling Hive Support

By default spark session is not configured to connect to hive. We need to explicitley have to enable using *enableHiveSupport* at the time of
session creation.

{% highlight scala %}
  val sparkSession = SparkSession.builder.master("local").appName("mapexample").
        enableHiveSupport().getOrCreate()
{% endhighlight %}

Spark session looks for *hive-site.xml* for connecting to hive metastore.

## Hive State

Before we start running different operations on hive, make sure that you have hive installed and running. Also make sure you have *hive-site.xml* in the
spark classpath.

Currently my hive has single table *sales* which contains the *sales.csv* data which we have used in earlier posts. We can observe the same from hive command line
as below.

{% highlight sql %}

show tables;

{% endhighlight%}

{% highlight text %}

hive> show tables;
OK
sales
Time taken: 0.024 seconds, Fetched: 1 row(s)
{% endhighlight %}

## Catalog to List Tables

The first operation is to list tables in hive. We can use spark catalog *listTables* for listing it from hive metastore.

{% highlight scala %}
  sparkSession.catalog.listTables.show()
{% endhighlight %}

Output 

{% highlight text %}

+-----+--------+-----------+---------+-----------+
| name|database|description|tableType|isTemporary|
+-----+--------+-----------+---------+-----------+
|sales| default|       null|  MANAGED|      false|
+-----+--------+-----------+---------+-----------+

{% endhighlight %}

As you observe from the output it's quite different than we observed we queried in memory tables in last post. When we connected to hive the below additional information is filled up

* database - Name of the database in Hive
* tableType - MANAGED means it native hive table. It will be *EXTERNAL* for external tables.
* isTemporary - For spark view it is set to true. Since table is loaded from the hive, it's false.

## Loading Table

Once we queried the tables , we can now load the table from hive. We use *table* API on spark session to do the same.

{% highlight scala %}
val df = sparkSession.table("sales")
df.show()
{% endhighlight %}

Output

{% highlight text %}
+-------------+----------+------+----------+
|transactionid|customerid|itemid|amountpaid|
+-------------+----------+------+----------+
|          111|         1|     1|     100.0|
|          112|         2|     2|     505.0|
|          113|         3|     3|     510.0|
|          114|         4|     4|     600.0|
|          115|         1|     2|     500.0|
|          116|         1|     2|     500.0|
|          117|         1|     2|     500.0|
|          118|         1|     2|     500.0|
|          119|         2|     3|     500.0|
|          120|         1|     2|     500.0|
|          121|         1|     4|     500.0|
|          122|         1|     2|     500.0|
|          123|         1|     4|     500.0|
|          124|         1|     2|     500.0|
+-------------+----------+------+----------+
{% endhighlight %}


## Saving Dataframe as Hive Table

We can also save the dataframe to hive as table. It will create table metadata in hive metastore and save data in parquet format.

{% highlight scala %}

df.write.saveAsTable("sales_saved")

{% endhighlight %}

Observe output in the hive

{% highlight text %}

hive> select * from sales_saved;
OK
111     1       1       100.0
112     2       2       505.0
113     3       3       510.0
114     4       4       600.0
115     1       2       500.0
116     1       2       500.0
117     1       2       500.0
118     1       2       500.0
119     2       3       500.0
120     1       2       500.0
121     1       4       500.0
122     1       2       500.0
123     1       4       500.0
124     1       2       500.0
Time taken: 0.051 seconds, Fetched: 14 row(s)

{% endhighlight %}

## Complete Code

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-two/src/main/scala/com/madhukaraphatak/spark/migration/sparktwo/CatalogHiveExample.scala).

## Conclusion

Spark 2.0 unifies the hive integration with spark session and catalog API. We don't need to create multiple contexts and use different API to access hive anymore.
