---
layout: post
title: "Introduction to Spark 3.0 - Part 2 : Multiple Column Feature Transformations in Spark ML"
date : 2020-03-24
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.


This is the first post in the series where I am going to talk about multiple column feature transformation in Spark ML. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).

## Multiple Column Feature Transformation in Spark 2.3

Spark introduced multiple column support for Spark ML transformations in 2.3. But that time it was only limited for few transformation. You can read about the same [here](/multi-column-feature-transformation-spark-ml).


## Multiple Column Feature Transformation in Spark 3.0

From Spark 3.0, all the Spark ML transformation going to be supporting multiple columns. One of those important transformation is **StringIndexer** which was not supported before.

The below code shows how to use the same


{% highlight scala %}

val inputColumns = Array("workclass","education")

val outputColumns = Array("workclass_indexed", "education_indexed")

val stringIndexer = new StringIndexer()
stringIndexer.setInputCols(inputColumns)
stringIndexer.setOutputCols(outputColumns)

{% endhighlight %}

String indexer now exposes new methods like **setInputCols** and **setOutputCols** additional to single column counter parts. This allows spark developer to run string indexing on multiple columns together.


## Performance

Having support multiple column support in all the transformations bring a huge improvement for the datasets with lot of columns.

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/ml/MultiColumnTransformer.scala).

## References

[https://issues.apache.org/jira/browse/SPARK-11215](https://issues.apache.org/jira/browse/SPARK-11215).

