---
layout: post
title: "Introduction to Spark 2.0 - Part 2 : Introduction to DataSet"
date : 2016-05-05
categories: scala spark spark-2.0
---
Spark 2.0 is next major release of popular big data processing framework Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release is setting tone for next year's direction. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different aspects of Spark 2.0.

This is the second blog in series, where I will be about Dataset abstraction of Spark. You can access all the posts in the series here.

TL;DR All code examples are available on github.


### Introduction to Dataset

Dataset is new abstraction in Spark introduced as alpha API in Spark 1.6. It's becoming stable in spark 2.0. It's new abstraction for all user land code in Spark. From the definition, Dataset is

"  
  A **Dataset** is a strongly typed collection of domain-specific objects that can be transformed
  in parallel using functional or relational operations. Each Dataset also has an untyped view
 called a **DataFrame**, which is a Dataset of **Row**.
"

which sounds similar to RDD definition
"
 RDD represents an immutable,partitioned collection of elements that can be operated on in parallel
"

The major difference from definition, is Dataset is collection of domain specific objects where as RDD is collection of any object. Domain object part of definition signifies the schema part of Dataset. So Dataset API is always strongly typed and optimized using schema where RDD is not.

Dataset definition also talks about Dataframes API. Dataframe is special Dataset where there is no compilation checks for schema. This makes DataSet new single abstraction replacing RDD from earlier version.


Once we understood the Dataset abstraction, in rest of post we will see how work with this abstraction.


## Dataset Wordcount example

As with any new API, we will learn API using how to use in WordCount example. The below is the code wordcount in dataset API.

### Step 1 : Create SparkSession

{% highlight scala %}
 val sparkSession = SparkSession.builder.
      master("local")
      .appName("example")
      .getOrCreate()
{% endhighlight %}      

### Step 2 : Read data and convert to Dataset

{% highlight scala %}
 import sparkSession.implicits._
val data = sparkSession.read.text("src/main/resources/data.txt").as[String]
{% endhighlight %}     


### Step 3 : Split and group by word
{% highlight scala %}
val words = data.flatMap(value => value.split("\\s+"))
val groupedWords = words.groupByKey(_.toLowerCase)
{% endhighlight %}

One of the thing, you may observed we don't create a key/value pair. The reason unlike RDD, Dataset works in row level abstraction. Each value is treated a row with multiple columns and any column can act as key for grouping like in database.

### Step 4 :  Count

{%highlight scala %}

val counts = groupedWords.count()

{% endhighlight %}


### Step 5 : Print results

{%highlight scala %}

counts.show()

{% endhighlight %}

