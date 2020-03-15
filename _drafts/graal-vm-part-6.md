---
layout: post
title: "Running Apache Spark from JavaScript"
date : 2020-03-14
categories: scala graal-vm javascript
---
GraalVM is a polygot VM which allows user to run multiple languages on same VM. Not only it supports multiple languages, it allows user to bring the libraries from different languages to one platform. You can read more about graalvm [here]()

One of the fascinating part of GraalVM is ability use Java libraries from any other supported languages. This made me thinking. Can I run the Apache Spark, which is a java framework, from a node.js code?

This is what we will be doing in this post.

## Setup

Download graalvm binary from here.


## Running Sample Javascript using Node



## Adding all the Spark JARS to the classpath



## Loading SqlContext Class

{% highlight js %}
var sparkSessionType = Java.type("org.apache.spark.sql.SparkSession")
{% endhighlight %}


## Creating SqlContext

{% highlight js %}
var sparkSession = sparkSessionType.builder().master("local[*]").appName("example").getOrCreate() 
{% endhighlight %}

## Loading Data

{% highlight js %}

var data = sparkSession.read().format("csv").option("header","true").load("/home/madhu/Dev/mybuild/spark-2.0-examples/src/main/resources/adult.csv")

{% endhighlight %}

## Printing Data


{% highlight js %}

data.show()

{% endhighlight %}


## Serving Schema Over Node.js http server



## Code




## Conclusion
