---
layout: post
title: "Introduction to Spark Data Source API - Part 1"
date : 2015-05-25
categories: spark scala datasource-series
---
Data source is an API for handling structured data in Spark. It was introduced in Spark 1.2 as part of Spark SQL package.
It brings a new way of reading data apart from InputFormat API which was adopted from hadoop. In the next series of blog posts,  I will be discussing how to load and query different kind of structured data using data source API.

This is the first post in the series about how to get started and how to do json data handling.


## Reading Data in Spark

InputFormat was the only way to load data till Spark 1.1. Thought it's a great API, it is not
suited for all  data sources. Particularly structured data sources like JSON, JDBC where we need
a better integration for schema discovery and smart filtering. Data source API bring the tighter
integration with the structured sources which will improve developer productivity and also performance.

## Built in sources

The support for following sources are built into Spark-SQL.

* JSON
* Parquet
* JDBC

But you are not limited by that. There are many other data sources supported by community.
The following are the few

* [CSV](https://github.com/databricks/spark-csv)
* [MongoDB] (https://github.com/Stratio/spark-mongodb)

You can find more on [spark-packages](http://spark-packages.org/) website.

## Json Querying

In this section, we are going to look at load and query JSON data. JSON support is built in. The following section
gives you step by step instructions for that. You can find sample data and
complete project on [github](https://github.com/phatak-dev/blog/tree/master/code/DataSourceExamples).


### Step 1 : Creating SQLContext

{% highlight scala %}
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
{%endhighlight%}

You need to create SQLContext in order to access any data source API.

### Step 2 : Load function to load schema from source
{% highlight scala %}
val df = sqlContext.load("org.apache.spark.sql.json", Map("path" -> args(1)))
{%endhighlight%}

load is an universal way of loading data from any data source supported by data source API. The first
parameter takes the class name of source. In this example *org.apache.spark.sql.json* point to that
data source is JSON. Second parameter is a map options of data source. The parameter we are passing
here path of the JSON file. The return value will be a DataFrame.


### Step 3 : Printing schema

{% highlight scala %}
df.printSchema()
{%endhighlight%}

When you load the data, most of sources will automatically discover schema from the data. In this example,
JSON schema is interpreted from the json keys and value. So after load, we can print the schema.


### Step 4 : Querying JSON using sql
{% highlight scala %}
df.registerTempTable("sales")
val aggDF = sqlContext.sql("select sum(amountPaid) from sales")
{%endhighlight%}

In the above code, we register our dataframe as a temp table called *sales*. Once we have registered table,
we can run any sql query using sqlContext.


### Step 5 : Print the result
{% highlight scala %}
println(aggDF.collectAsList())
{%endhighlight%}

You can call collect get the results.
