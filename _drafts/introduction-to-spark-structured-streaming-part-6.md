---
layout: post
title: "Introduction to Spark Structured Streaming - Part 6 : Enriching Stream with Static Data"
date : 2017-08-08
categories: scala spark introduction-structured-streaming
---
Structured Streaming is a new streaming API, introduced in spark 2.0, rethinks stream processing in spark land. It models stream
as an infinite table, rather than discrete collection of data. It's a radical departure from models of other stream processing frameworks like
storm, beam, flink etc. Structured Streaming is the first API to build stream processing on top of SQL engine.

Structured Streaming was in alpha in 2.0 and 2.1. But with release 2.2 it has hit stable status. In next few releases,
it's going to be de facto way of doing stream processing in spark. So it will be right time to make ourselves familiarise
with this new API.

In this series of posts, I will be discussing about the different aspects of the structured streaming API. I will be discussing about
new API's, patterns and abstractions to solve common stream processing tasks. 

This is the sixth post in the series. In this post, we discuss about joining stream data with static data. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Data Enrichment using Static Data



## Unified Dataset Abstraction



## Reading Static Datjoining stream data with static data

{% highlight scala %}
case class Customer(customerId: String, customerName: String)

  val customerDs = sparkSession.read
      .format("csv")
      .option("header", true)
      .load("src/main/resources/customers.csv")
      .as[Customer]
   
{% endhighlight %}


## Reading Sales Stream Data

{% highlight scala %}

  val socketStreamDf = sparkSession.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 50050)
      .load()

  val dataDf = socketStreamDf.as[String].flatMap(value => value.split(" "))
    val salesDs = dataDf
      .as[String]
      .map(value => {
        val values = value.split(",")
        Sales(values(0), values(1), values(2), values(3).toDouble)
      })
      .toDF()
      .groupBy("customerId")
      .sum("amountPaid")

{% endhighlight %}


## Joins

{% highlight scala %}
val joinedDs = salesDs
      .join(customerDs, "customerId")
    
{% endhighlight%}

## Running the example




## Conclusion
