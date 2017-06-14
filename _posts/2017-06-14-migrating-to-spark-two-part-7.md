---
layout: post
title: "Migrating to Spark 2.0 - Part 7 : SubQueries" 
date : 2017-06-14
categories: scala spark spark-two-migration-series
---

Spark 2.0 brings a significant changes to abstractions and API's of spark platform. With performance boost, this version has made some of non backward compatible changes to the framework. To keep up to date with the latest updates, one need to migrate their spark 1.x code base to 2.x. In last few weeks, I was involved in migrating one of fairly large code base and found it quite involving process. In this series of posts, I will be documenting my experience of migration so it may help all the ones out there who are planning to do the same.

This is the seventh post in this series.In this post we will discuss about subquery support in spark sql. You can access all the posts [here](/categories/spark-two-migration-series).

TL;DR You can access all the code on [github](https://github.com/phatak-dev/spark-two-migration).

## Spark SQL in Spark 2.0

Spark SQL has been greatly improved in 2.0 to run all [99 queries of TPC-DS](https://databricks.com/blog/2016/07/26/introducing-apache-spark-2-0.html), a standard benchmark suit for popular sql implementations. To support this benchmark and to provide a complete OLAP sql engine, spark has added many features to it's sql query language which were missing earlier. This makes spark sql more powerful than before.

One of the big feature they added was support for subqueries. Subquery is query inside the another query. It's a powerful feature of SQL which makes writing multi level aggregation much easier and more performant.

In below sections, we will discuss how you can port your earlier complex 1.x sql queries into simpler and performant subqueries.

## Scalar SubQueries

There are different types of sub queries. One of those are scalar subqueries. They are called scalar as they return single result for query. There are two types
of scalar queries

* Uncorrelated  Scalar SubQueries
* Correlated  Scalar SubQueries

## Uncorrelated Scalar SubQueries

Let's take an example. Let's say we have loaded *sales* data which we have used in earlier blogs. Now we want to figure out, how each item is doing
compared to max sold item. For Ex: If our max value is 600, we want to compare how far is each of our sales to that figure. This kind of information
is very valuable to understand the distribution of our sales.

So what we essential want to do is to add max amount to each row of the dataframe.

### Query in Spark 1.x

In spark 1.x, there was no way to express this in one query. So we need to do as a two step. In first step we calculate the max *amountPaid* and then 
in second step we add that to each row as a literal. The code looks like below

{% highlight scala %}
val max_amount = sqlContext.sql("select max(amountPaid) as max_amount from sales").first.getDouble(0)
val dfWithMaxAmount = sqlContext.sql(s"select *, ($max_amount) as max_amount from sales")
{% endhighlight %} 

Even though it works, it's not elegant. If we want to add more aggregations, this doesn't scale well.

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-one/src/main/scala/com/madhukaraphatak/spark/migration/sparkone/SubQueries.scala).

### Query in 2.x

With uncorrelated scalar sub query support, the above code can be rewritten as below in 2.x

{% highlight scala %}

val dfWithMaxAmount = sparkSession.sql("select *, (select max(amountPaid) from sales) max_amount from sales")

{% endhighlight %}

In this query, we write query inside query which calculates max value and adds to the dataframe. This code is much easier to write
and maintain. The subquery is called uncorrelated because it returns same value for each row in the dataset.

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-two/src/main/scala/com/madhukaraphatak/spark/migration/sparktwo/SubQueries.scala).

### Correlated Sub Queries

Let's say we want to write same logic but per item. It becomes much more complicated in spark 1.x, because it's no more single value for dataset. We need to calculate
max for each group of items and append it to the group. So let's see how sub queries help here.

### Query in 1.x

The logic for this problem involves a left join with group by operation. It can be written as below

{% highlight scala %}

val dfWithMaxPerItem = sqlContext.sql("""select A.itemId, B.max_amount  
from sales A left outer join ( select itemId, max(amountPaid) max_amount
from sales B group by itemId) B where A.itemId = B.itemId""")
 
{% endhighlight %}

Again it's complicated and less maintainable. 

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-one/src/main/scala/com/madhukaraphatak/spark/migration/sparkone/SubQueries.scala).

### Query in 2.x
Now we can rewrite the above code without any joins in subquery as below

{% highlight scala %}
val dfWithMaxPerItem = sparkSession.sql("select A.itemId, 
(select max(amountPaid) from sales where itemId=A.itemId) max_amount from sales A")
{% endhighlight%}

This looks much cleaner than above. Internally spark converts above code into a left outer join. But as a user, we don't need to worry about it.

The query is called correlated because it depends on outer query for doing the where condition evaluation of inner query.

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-two/src/main/scala/com/madhukaraphatak/spark/migration/sparktwo/SubQueries.scala).

## Conclusion

Spark SQL is improved quite a lot in spark 2.0. We can rewrite many complicated spark 1.x queries using simple sql constructs like subqueries. This makes code 
more readable and maintainable.

## References

* [SQL Subqueries in Apache Spark 2.0](https://databricks.com/blog/2016/06/17/sql-subqueries-in-apache-spark-2-0.html)
