---
layout: post
title: "Introduction to Spark 3.0 - Part 6 : Min and Max By Functions"
date : 2020-04-05
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.

This is the sixth post in the series where I am going to talk about min and max by SQL functions. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).

## Finding Minimum and Maximum ID using It's Value

Let's say we have data as below  with a id and value columns

{% highlight scala %}

    val df = sparkSession.createDataFrame(Seq(
      ("1", 10),
      ("2", 20),
      ("3", 30),
      ("4", 40)
    )).toDF("id","value")
{% endhighlight %}

Let's say we want to find an id with least value. We can easily find minmimum value with **min** method but it's not easy to find it's associated id. We need to use complicated functions in Spark 2.x

## MinBy in Spark 2.x

The below code calculates the minimum id by it's value using a window spec.

{% highlight scala %}

val orderedDf = Window.orderBy(df.col("value"))
val rankedDf = df.withColumn("rank", dense_rank.over(orderedDf))
val minDf = rankedDf.filter("rank == 1")
minDf.show()

{% endhighlight %}

In above code, we first create a window where the values are ordered and then create a rank by **dense_rank** function. Then we filter the values by **rank==1**. This gives us the minimum id.

This is all way complicated for a simple operation. So there is a new easy way introduced in 3.0

## MinBy and MaxBy SQL Function in 3.0

Spark 3.0 add two function **min_by** and **max_by** to compute the min and max by a column. They are simple to use and doesn't need all the complexity of window operations.

Let's calculate the same with these new functions

{% highlight scala %}

df.createOrReplaceTempView("table")
val resultDf = sparkSession.sql("select max_by(id,value) max_id, min_by(id,value) min_id from table")

{% endhighlight %}

These functions take two parameters. The first parameter is minimum/maximumn we want to find and second parameter the value on which we want to find. It's that simple.

These functions greatly simplify calculating these things in spark.


## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/sql/MinAndMaxByExample.scala).

## References

[https://issues.apache.org/jira/browse/SPARK-27653](https://issues.apache.org/jira/browse/SPARK-27653)

