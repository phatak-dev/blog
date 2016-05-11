---
layout: post
title: "Introduction to Spark 2.0 - Part 3 : Moving from RDD API to Dataset API"
date : 2016-05-05
categories: scala spark spark-2.0
---
Spark 2.0 is next major release of popular big data processing framework Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release is setting tone for next year's direction. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different aspects of Spark 2.0.

This is the third blog in series, where I will be discussing about how to move your RDD based code to Dataset based code. You can access all the posts in the series here.

TL;DR All code examples are available on github.


### RDD to Dataset

Dataset API combines best of RDD and Dataset API in one API. Lot of API's in Dataset they mimic the RDD API though they differ a lot in the implementation. So most of the code, written in RDD can be easily ported. In this post, I will be sharing different code snippets to show to how a given code in RDD can be represented in Dataset API.


##  1. Loading text data

### RDD code

{% highlight scala %}
 val rdd = sparkContext.textFile("src/main/resources/data.txt")
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
val ds = sparkSession.read.text("src/main/resources/data.txt")
{% endhighlight %}     

## 2. Calculating count 

### RDD code

{% highlight scala %}
 rdd.count()
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
ds.count()
{% endhighlight %}     


## 3. WordCount

### RDD code

{% highlight scala %}
 val wordsRDD = rdd.flatMap(value => value.split("\\s+"))
 val wordsPair = wordsRDD.map(word => (word,1))
 val wordCount = wordsPair.reduceByKey(_+_)
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
import sparkSession.implicits._
val wordsDs = ds.flatMap(value => value.split("\\s+"))
val wordCountDs = wordsDs.groupByKey(value => value).count
{% endhighlight %}     

## 4. Caching 

### RDD code

{% highlight scala %}
 rdd.cache()
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
ds.cache()
{% endhighlight %}     


## 5. Filter

### RDD code

{% highlight scala %}
 val filteredRDD = wordsRDD.filter(value => value =="hello")
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
ds.count()
{% endhighlight %}     

## 6. Map partitions

### RDD code
{% highlight scala %}
 val mapPartitionsRDD = rdd.mapPartitions(iterator => List(iterator.count(value => true)).iterator)
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
val mapPartitionsDs = ds.mapPartitions(iterator => List(iterator.count(value => true)).iterator)
{% endhighlight %}  

## 7. Converting from each other

### RDD code
{% highlight scala %}
 val dsToRDD = ds.rdd
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
val rddStringToRowRDD = rdd.map(value => Row(value))
val dfschema = StructType(Array(StructField("value",StringType)))
val rddToDF = sparkSession.createDataFrame(rddStringToRowRDD,dfschema)
val rDDToDataSet = rddToDF.as[String]
{% endhighlight %}  


## 7. Double based operations

### RDD code
{% highlight scala %}
  val doubleRDD = sparkContext.makeRDD(List(1.0,5.0,8.9,9.0))
  val rddSum =doubleRDD.sum()
  val rddMean = doubleRDD.mean()
{% endhighlight %}      

### Dataframe code

{% highlight scala %}
val rowRDD = doubleRDD.map(value => Row.fromSeq(List(value)))
    val schema = StructType(Array(StructField("value",DoubleType)))
    val doubleDS = sparkSession.createDataFrame(rowRDD,schema)

    import org.apache.spark.sql.functions._
    doubleDS.agg(sum("value")).show()
    doubleDS.agg(mean("value")).show()
{% endhighlight %}  


