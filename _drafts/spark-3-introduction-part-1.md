---
layout: post
title: "Introduction to Spark 3.0 - Part 1 : Multi Character Delimiter in CSV Source"
date : 2020-03-24
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.


This is the first post in the series where I am going to talk about improvements in built in csv source. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).

## CSV Source

CSV is one of most used data source in Apache Spark. So from spark 2.0, it has become bultin source. 

Spark 3.0 brings one of the important improvement to this source by allowing user to specify the multi character delimiter.


## Delimiter Support in Spark 2.x

Till Spark 3.0, spark allowed only single character as the delimiter in CSV. 

Let's try to load the below csv which has **\|\|** as it's seperator

{% highlight text %}

a||b||c||d
1||2||3||4
5||6||7||8

{% endhighlight %}

using below code 

{% highlight scala %}

val df  = sparkSession.read
      .option("delimiter","||")
      .option("header","true")
      .csv("src/main/resources/multicharacterseperator.csv")

{% endhighlight %}

When you run the above code, you will get below exception

{% highlight text %}

throws java.lang.IllegalArgumentException: Delimiter cannot be more than one character: ||

{% endhighlight %}


As you can see from the exception, spark only supports single character as the delimited. This made **user to process the csv outside the spark** which is highly convinient.

## Multiple Character Delimiter Support in Spark 3.0

Spark 3.0 has added an improvment now to support multiple characters in csv. So when you run the same code in 3.0, you will get below output

{% highlight scala %}

+---+---+---+---+
|  a|  b|  c|  d|
+---+---+---+---+
|  1|  2|  3|  4|
|  5|  6|  7|  8|
+---+---+---+---+

{% endhighlight %}

Even though it looks as a small improvement, it now alievates need of doing these kind of processing outside of spark which will be huge improvement for larger datasets.

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/sources/MultiCharacterDelimiterCSV.scala).

## References

[https://issues.apache.org/jira/browse/SPARK-24540](https://issues.apache.org/jira/browse/SPARK-24540).

