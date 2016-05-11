---
layout: post
title: "Introduction to Spark 2.0 - Part 2 : Wordcount in Dataset API"
date : 2016-05-11
categories: scala spark spark-two
---
Spark 2.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 2.0.

This is the second blog in series, where I will be discussing about dataset abstraction of Spark. You can access all the posts in the series [here](/categories/spark-two/).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark2.0-examples).


### Introduction to Dataset

Dataset is new abstraction in Spark introduced as alpha API in Spark 1.6. It's becoming stable API in spark 2.0. It's new single abstraction for all user land code in Spark. 

From Definition, 
*"  
  A **Dataset** is a strongly typed collection of domain-specific objects that can be transformed
  in parallel using functional or relational operations. Each dataset also has an untyped view
 called a **DataFrame**, which is a Dataset of **Row**.
"*

which sounds similar to RDD definition

*"
 RDD represents an immutable,partitioned collection of elements that can be operated on in parallel
"*

The major difference is, dataset is collection of domain specific objects where as RDD is collection of any object. Domain object part of definition signifies the schema part of dataset. So dataset API is always strongly typed and optimized using schema where RDD is not.

Dataset definition also talks about Dataframes API. Dataframe is special dataset where there is no compilation checks for schema. So this makes dataSet new single abstraction replacing RDD from earlier versions of spark.

Once we understood the dataset abstraction, in rest of post we will see how to work with this abstraction.


## Dataset Wordcount example

As with any new API, we will learn API using how to use in WordCount example. The below is the code for wordcount in dataset API.

### Step 1 : Create SparkSession

As we discussed in last blog, we use spark session as entry point for dataset API.

{% highlight scala %}
 val sparkSession = SparkSession.builder.
      master("local")
      .appName("example")
      .getOrCreate()
{% endhighlight %}      

### Step 2 : Read data and convert to Dataset

We read data using read.text API which is similar to *textFile* API of RDD. The *as[String]* part of code assigns the needed schema for dataset.

{% highlight scala %}
 import sparkSession.implicits._
val data = sparkSession.read.text("src/main/resources/data.txt").as[String]
{% endhighlight %}     

Here *data* will be of the type of *DataSet[String]*. Remember to import **sparkSession.implicits._** for all schema conversion magic.


### Step 3 : Split and group by word
Dataset mimics lot of RDD API's like map, groupByKey etc. The below code we are splitting lines to get words and group them by words.

{% highlight scala %}
val words = data.flatMap(value => value.split("\\s+"))
val groupedWords = words.groupByKey(_.toLowerCase)
{% endhighlight %}

One thing you may observed we don't create a key/value pair. The reason is unlike RDD, dataset works in row level abstraction. Each value is treated a row with multiple columns and any column can act as key for grouping like in database.

### Step 4 :  Count
Once we have grouped, we can count each word using count method. It's similar to *reduceByKey* of RDD.

{%highlight scala %}

val counts = groupedWords.count()

{% endhighlight %}


### Step 5 : Print results

Finally once we count, we need to print the result. As with RDD, all the above API's are lazy. We need to call an action to trigger computation. In dataset, show is one of those actions. It's show first 20 results. If you want complete result, you can use *collect* API.

{%highlight scala %}

counts.show()

{% endhighlight %}

You can access complete code [here](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/DataSetWordCount.scala).


Now we have written our first example in dataset abstraction. We will explore more about dataset API in future posts.