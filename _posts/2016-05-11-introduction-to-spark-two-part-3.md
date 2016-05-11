---
layout: post
title: "Introduction to Spark 2.0 - Part 3 : Porting Code from RDD API to Dataset API"
date : 2016-05-11
categories: scala spark spark-two
---
Spark 2.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 2.0.

This is the third blog in series, where I will be discussing about how to port your RDD based code to Dataset API. You can access all the posts in the series [here](/categories/spark-two/).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark2.0-examples).


## RDD to Dataset

Dataset API combines best of RDD and DataFrame API's in one API. Many API's in Dataset mimic the RDD API though they differ a lot in the implementation. So most of RDD code can be easily ported to Dataset API. In this post, I will be sharing few code snippets to show how a given code in RDD API can be written in Dataset API.


##1. Loading Text Data

### RDD

{% highlight scala %}
val rdd = sparkContext.textFile("src/main/resources/data.txt")
{% endhighlight %}      

### Dataset

{% highlight scala %}
val ds = sparkSession.read.text("src/main/resources/data.txt")
{% endhighlight %}     

## 2. Calculating count 

###RDD

{% highlight scala %}
rdd.count()
{% endhighlight %}      

### Dataset

{% highlight scala %}
ds.count()
{% endhighlight %}     


## 3. WordCount Example

### RDD

{% highlight scala %}
 val wordsRDD = rdd.flatMap(value => value.split("\\s+"))
 val wordsPair = wordsRDD.map(word => (word,1))
 val wordCount = wordsPair.reduceByKey(_+_)
{% endhighlight %}      

### Dataset

{% highlight scala %}
import sparkSession.implicits._
val wordsDs = ds.flatMap(value => value.split("\\s+"))
val wordsPairDs = wordsDs.groupByKey(value => value)
val wordCountDs = wordsPairDs.count()
{% endhighlight %}     

## 4. Caching 

### RDD

{% highlight scala %}
rdd.cache()
{% endhighlight %}      

### Dataset

{% highlight scala %}
ds.cache()
{% endhighlight %}     


## 5. Filter

### RDD

{% highlight scala %}
val filteredRDD = wordsRDD.filter(value => value =="hello")
{% endhighlight %}      

### Dataset

{% highlight scala %}
val filteredDS = wordsDs.filter(value => value =="hello")
{% endhighlight %}     

## 6. Map Partitions

### RDD
{% highlight scala %}
val mapPartitionsRDD = rdd.mapPartitions(iterator => List(iterator.count(value => true)).iterator)
{% endhighlight %}      

### Dataset

{% highlight scala %}
val mapPartitionsDs = ds.mapPartitions(iterator => List(iterator.count(value => true)).iterator)
{% endhighlight %}  

## 7. reduceByKey 

### RDD
{% highlight scala %}
val reduceCountByRDD = wordsPair.reduceByKey(_+_)
{% endhighlight %}      

### Dataset

{% highlight scala %}
val reduceCountByDs = wordsPairDs.mapGroups((key,values) =>(key,values.length))
{% endhighlight %}  


## 7. Conversions

### RDD
{% highlight scala %}
 val dsToRDD = ds.rdd
{% endhighlight %}      

### Dataset

Converting a RDD to dataframe is little bit work as we need to specify the schema. Here we are showing how to convert RDD[String] to DataFrame[String].

{% highlight scala %}
val rddStringToRowRDD = rdd.map(value => Row(value))
val dfschema = StructType(Array(StructField("value",StringType)))
val rddToDF = sparkSession.createDataFrame(rddStringToRowRDD,dfschema)
val rDDToDataSet = rddToDF.as[String]
{% endhighlight %}  



##8. Double Based Operations

### RDD
{% highlight scala %}
val doubleRDD = sparkContext.makeRDD(List(1.0,5.0,8.9,9.0))
val rddSum =doubleRDD.sum()
val rddMean = doubleRDD.mean()
{% endhighlight %}      

### Dataset

{% highlight scala %}
val rowRDD = doubleRDD.map(value => Row.fromSeq(List(value)))
val schema = StructType(Array(StructField("value",DoubleType)))
val doubleDS = sparkSession.createDataFrame(rowRDD,schema)

import org.apache.spark.sql.functions._
doubleDS.agg(sum("value"))
doubleDS.agg(mean("value"))
{% endhighlight %}  


##9. Reduce API

### RDD
{% highlight scala %}
val rddReduce = doubleRDD.reduce((a,b) => a +b)
{% endhighlight %}      

### Dataset

{% highlight scala %}
val dsReduce = doubleDS.reduce((row1,row2) =>Row(row1.getDouble(0) + row2.getDouble(0)))
{% endhighlight %}  

You can access complete code [here](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/RDDToDataSet.scala).

The above code samples show how to move your RDD based code base to new Dataset API. Though it doesn't cover complete RDD API, it should give you fair idea about how RDD and Dataframe API's are related.