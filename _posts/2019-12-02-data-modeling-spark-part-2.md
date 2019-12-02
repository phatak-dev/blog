---
layout: post
title: "Data Modeling in Apache Spark - Part 2 : Working With Multiple Dates"
date : 2019-12-02
categories: spark data-modeling scala
---
Data modeling is one of the important aspect of data analysis. Having right kind of model, allows user to ask business questions easily. The data modeling techniques have been bedrock of the SQL data warehouses in past few decades. 

As Apache Spark has become new warehousing technology, we should be able to use the earlier data modeling techniques in spark also. This makes Spark data pipelines much more effective.

In this series of posts, I will be discussing different data modeling in the context of spark. This is the second post in the series which discusses about handling multiple dates. You can access all the posts in the series [here](/categories/data-modeling).

## Multiple Date Columns
In last post, we discussed how to handle date analysis for a single date column. Having single date column is common in many of the datasets. So the strategy discussed in earlier post works fine.

But there are datasets where we may want to analyse our data against multiple date columns. Then the strategy discussed in earlier post is not enough. So we need to extends date dimension logic to accommodate multiple date columns.

## Adding Issue Date to Stock Data

The below code adds a date column called **issue_date** to stock data to emulate the scenario of multiple dates. 

{% highlight scala %}

val appleStockDfWithIssueDate = appleStockDf.
      withColumn("issue_date",add_months(appleStockDf("Date"),-12))

{% endhighlight %}

Now if the user wants to analyse against **Date** column which signifies transaction date and **issue_date** which signifies the when a given stock is issued, we need to use multiple date dimensions.

## Date Dimension with New Prefix

To analyse multiple dates, we need to join date dimension multiple times. We need to make a view out of data dimension with different prefix which allows us to do the same.

{% highlight scala %}

val issueDateSchema = StructType(dateDf.schema.fields.map(value => 
value.copy(name = "issue_"+value.name)))

val issueDf = sparkSession.createDataFrame(dateDf.rdd, issueDateSchema)

{% endhighlight %}

In above code, we are creating new df called **issueDf** which adds prefix called **issue** for all the columns which signifies this date dimension is combined for **issue_date**.

## Three way Join

Once we have new date dimension ready, now we can join for both dates in stock data.

{% highlight scala %}

 val twoJoinDf = appleStockDfWithIssueDate.join(dateDf, appleStockDfWithIssueDate.col("Date") ===
       dateDf.col("full_date_formatted"))
             .join(issueDf, appleStockDfWithIssueDate.col("issue_date") === issueDf.col("issue_full_date_formatted"))

{% endhighlight %}

## Analysis on Issue Date

Once we have done joins, we can analyse on issue date as below 

{% highlight scala %}

twoJoinDf.groupBy("issue_year","issue_quarter").
      avg("Close").
            sort("issue_year","issue_quarter")
	          .show()
{% endhighlight %}

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/2.4/src/main/scala/com/madhukaraphatak/examples/sparktwo/datamodeling/DateHandlingExample.scala).
