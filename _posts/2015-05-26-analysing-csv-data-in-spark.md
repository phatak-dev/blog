---
layout: post
title: "Analysing CSV data in Spark : Introduction to Spark Data Source API - Part 2"
date : 2015-05-26
categories: spark scala datasource-series
---
Data source is an API for handling structured data in Spark. It was introduced in Spark 1.2 as part of Spark SQL package.
It brings a new way of reading data apart from InputFormat API which was adopted from hadoop. In the next series of blog posts,  I will be discussing how to load and query different kind of structured data using data source API.

This is the second post in the series in which we discuss how to handle csv data in spark. You can find other
blog posts of the series [here](/categories/datasource-series/).


### Spark-csv

[Spark-csv](https://github.com/databricks/spark-csv) is a community library provided by Databricks to
parse and query csv data in the spark. This library adheres to the data source API both for reading and
writing csv data.

### Csv Loading

In this section, we are going to look at how to load and query CSV data.
You can find sample [data](https://github.com/phatak-dev/blog/blob/master/code/DataSourceExamples/src/main/resources/sales.csv) and complete project on [github](https://github.com/phatak-dev/blog/tree/master/code/DataSourceExamples). For detailed steps
about data source API, please refer to this [post](/introduction-to-spark-data-source-api-part-1).


{% highlight scala %}
val sc = new SparkContext(args(0), "Csv loading example")
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val df = sqlContext.load("com.databricks.spark.csv", Map("path" -> args(1),"header"->"true"))
df.printSchema()

{%endhighlight%}

In the above code, we pass *com.databricks.spark.csv* to load method to signify that we want to read csv data. Also in the
second parameter, we pass *"header"->"true"* to tell that, the first line of the file is a header.


### Querying CSV Data
{% highlight scala %}
df.registerTempTable("sales")
val aggDF = sqlContext.sql("select sum(amountPaid) from sales")
println(aggDF.collectAsList())
{%endhighlight%}

Once we load data as dataframe, querying is exactly same as any other data source. You can access complete
code on [github](https://github.com/phatak-dev/blog/blob/master/code/DataSourceExamples/src/main/scala/com/madhukaraphatak/spark/datasource/CsvDataInput.scala).


### Save as CSV

In the previous section, we looked at how to load and query the data in CSV. In this section, we are going to
look how to save a dataframe as CSV file. In this example, we will load data from json and then save it as csv file.
{% highlight scala %}
val sc = new SparkContext(args(0), "Csv loading example")
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val df = sqlContext.load("org.apache.spark.sql.json", Map("path" -> args(1)))
df.save("com.databricks.spark.csv", SaveMode.ErrorIfExists,Map("path" -> args(2),"header"->"true"))
{%endhighlight%}

The first 3 lines of code loads json data. The last line saves the constructed data frame as a csv file.  The
*save* is an universal method in data source API to save to any source. The following are the parameters passed to save method.

* source - it same as *load* method. It says we want to save as csv.
* SaveMode - allows user to signify what has to be done if the given output path already exists. You can throw error, append or
 overwrite. In our example, we will thrown an error as we don't want to overwrite any existing file.
* Options - These options are same as what we passed to *load* method. Here we specify the output path and
  specify that the first line output file has to be header.

You can access complete code on [github](https://github.com/phatak-dev/blog/blob/master/code/DataSourceExamples/src/main/scala/com/madhukaraphatak/spark/datasource/CsvDataOutput.scala).


So from the above sections, it's obvious that data source API brings a very easy to use interface to structured data. You can
load, query and change between multiple sources with few lines of code.
