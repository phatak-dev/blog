---
layout: post
title: "Introduction to Spark 3.0 - Part 8 : DataFrame Tail Function"
date : 2020-04-20
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.

This is the eighth post in the series where I am going to talk about dataframe tail function. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).

## DataFrame Head Function

In many times in our code, we would like to read few rows from the dataframe. This is typically used for exploratory use cases. For this, we use **head** function on top of the dataframe. This is same as **head** function on a scala list.

The below code example shows use of head function

{% highlight scala %}

val df = sparkSession.range(100)
println(df.head(2).toList)

{% endhighlight %}

In the above code, we are reading the first 2 rows of the dataset. 

Internally this is implemented by reading only needed number of items by accessing one partition at a time from beginning. This will limit the number of partitions that needed to be read.

## Need of Tail Function

In many use cases, particularly when data is sorted in some way, it will be useful to see the last few values. It will give an idea how the data is distributed. For these use cases, a tail function needed. This will behave same as Scala List tail function.

## Tail Function in Spark 3.0

In spark 3.0, a new function is introduced for reading values from the end of a dataframe. The below example shows the same

{% highlight text %}

println(df.tail(5).toList)

{% endhighlight %}

In above code, we are reading last 5 values.

## Implementation of Tail Function

To implement tail, spark does the reverse of head. Rather than reading from the first partition, in tail implementation spark starts reading from last partition. It will read till number of items are retrieved. 

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/sql/DataFrameTail.scala).

## References

[https://issues.apache.org/jira/browse/SPARK-30185](https://issues.apache.org/jira/browse/SPARK-30185).
