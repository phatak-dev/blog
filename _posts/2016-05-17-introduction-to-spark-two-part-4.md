---
layout: post
title: "Introduction to Spark 2.0 - Part 4 : Introduction to Catalog API"
date : 2016-05-17
categories: scala spark spark-two
---
Spark 2.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 2.0.

This is the fourth blog in series, where I will be discussing about catalog API. You can access all the posts in the series [here](/categories/spark-two/).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark2.0-examples).


## Catalog API

DataSet with  Dataframe API supports structured data analysis in spark. One of the important aspects of structured data analysis is managing metadata. It may be temporary metadata like temp table, registered udfs on SQL context or permanent metadata like Hive meta store or HCatalog. 

In earlier versions of spark, there was no standard API to access this metadata. Users used to use queries like *show tables* and others to query this metadata. These queries often needed raw string manipulation and used to differ depending upon the underneath meta store.

But it's changing in Spark 2.0.In Spark 2.0, spark has added a standard API called catalog for accessing metadata in spark SQL. This works both for spark sql and hive metadata. 

In this post I will be discussing about how to work with catalog API.


## Accessing Catalog

Catalog is available on spark session. The following code shows how to access catalog.

{% highlight scala %}
val catalog = sparkSession.catalog
{% endhighlight %}     

## Querying the databases

Once we have access to catalog, we can use it to query the databases. All the API's on catalog returns a dataset.
{% highlight scala %}
catalog.listDatabases().select("name").show()

{% endhighlight %}     

On catalog, *listDatabases* gives all the databases.By default, you will have only one database called *default*.In case of hive, it also access databases from the metastore. As the listDatabases returns a dataset, we can use all the operation available on dataset to query the metadata.


## Registering Dataframe with createTempView

In earlier versions of spark, we used to register a dataframe using *registerTempTable*. But in spark 2.0, this API is deprecated. The *registerTempleTable* API was one of the source of confusion as users used think it materializes the dataframe and saves as a temporary table which was not the case. So this API is replaced with *createTempView*.

*createTempView* can be used as follows.

{% highlight scala %}

df.createTempView("sales")

{% endhighlight %}     

Once we have registered a view, we can query it using listTables.

## Querying the tables

As we can query databases, we can query tables. It lists all the temporary table registered in case of spark sql. In hive case, it lists all the tables in the underneath metadata store.

{% highlight scala %}

catalog.listTables().select("name").show()

{% endhighlight %}     


## Checking is table cached or not

Catalog not only is used for querying. It can be used to check state of individual tables. Given a table, we can check is it cache or not. It's useful in scenarios to make sure we cache the tables which are accessed frequently. 

{% highlight scala %}
catalog.isCached("sales")
{% endhighlight %}   

You will get false as by default no table will be cache. Now we cache the table and query again.

{% highlight scala %}
df.cache()
catalog.isCached("sales")
{% endhighlight %}   

Now it will print true.

## Drop view

We can use catalog to drop views. In spark sql case, it will deregister the view. In case of hive, it will drops from the metadata store.

{% highlight scala %}
catalog.dropTempView("sales")
{% endhighlight %}   

## Query registered functions

Catalog API not only allow us to interact with tables, it also allows us to interact with udf's. The below code shows how to query all functions registered on spark session. They also include all built in functions.

{% highlight scala %}
catalog.listFunctions().
select("name","description","className","isTemporary").show(100)
{% endhighlight %}   

You can access complete code [here](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/CatalogExample.scala).

Catalog is new API in spark 2.0 which allows us to interact with metadata of spark sql. This is a much better interface to metadata compared to earlier versions of spark.


##References

Jira for catalog API [https://issues.apache.org/jira/browse/SPARK-13477](https://issues.apache.org/jira/browse/SPARK-13477).

