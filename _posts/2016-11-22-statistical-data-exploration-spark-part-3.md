---
layout: post
title: "Statistical Data Exploration using Spark 2.0 - Part 3 : Outlier Detection using Quantiles" 
date : 2016-11-22
categories: scala spark statistical-data-exploration
---
In our [first blog](/statistical-data-exploration-spark-part-1) of the series, we discussed about generating summary data using spark.This summary data included mean, standard deviation and quantiles. Quantiles gives pretty good idea about spread of data and are one of the robust measurements compared to mean.  

In this third blog of the series, we will be discussing about how to use quantiles to identify the outliers in our data. You can find all other blogs in the series [here](/categories/statistical-data-exploration).

TL;DR All code examples available on [github](https://github.com/phatak-dev/Statistical-Data-Exploration-Using-Spark-2.0).

## Outlier
For a given variable in data, outlier is a value distant from other values. Normally outlier is  introduced in data due to issue with measurements or some error. Outlier effects our inference of the data as they may skew the results.

So in statistics its important to identify the outliers in the data, before we use it for analysis.

## Outlier detection using Box-and-Whisker Plot 

There are many methods to identify outlier in statistics. In this blog, we are going to discuss about one of the method which uses quantiles. The logic of the algorithm as follows

Let's say we have Q1 as first quantile(25%) and Q3 as third quantile(75%) , the inter quantile range or IQR will be given as

{% highlight text %}
  IQR = Q3 - Q1
{% endhighlight %}

IQR gives the width of distribution of data between 25% and 75% of data. Using IQR we can identify the outliers. This method is known as Box and Whisker method.

In this method, any value smaller than Q1- 1.5 * IQR or any value greater than Q3+1.5 * IQR will be categorised as the outlier.

You can find more information on this method [here](http://www.purplemath.com/modules/boxwhisk3.htm).


## Outlier detection in Spark

Once we understand the method, we can implement it in spark. The following are the steps for implementing the same.


### Create Sample Data

First we create a sample dataset to work with and then convert into a spark dataframe.

{% highlight scala %}

val sampleData = List(10.2, 14.1,14.4,14.4,14.4,14.5,14.5,14.6,14.7,
           14.7, 14.7,14.9,15.1, 15.9,16.4)
   val rowRDD = sparkSession.sparkContext.makeRDD(sampleData.map(value => Row(value)))
   val schema = StructType(Array(StructField("value",DoubleType)))
   val df = sparkSession.createDataFrame(rowRDD,schema)
{% endhighlight %}

In above example, we have taken a list of values as sample data. If you observe the data, most of the values are around 14.1-14.7. From that we can assume mostly values 10.2, 16.4 are outliers. There is chance that 15.1 and 15.9 are also outliers but we are not fully sure.

### Calculate Quantiles and IQR

{% highlight scala %}
val quantiles = df.stat.approxQuantile("value",
           Array(0.25,0.75),0.0)
val Q1 = quantiles(0)
val Q3 = quantiles(1)
val IQR = Q3 - Q1

{% endhighlight %}

As we did in earlier posts, we are using *approxQuantile*  method to compute the quantiles needed. Once we have quantiles, we can calculate IQR.


### Filter Outliers 

{% highlight scala %}

val lowerRange = Q1 - 1.5*IQR
val upperRange = Q3+ 1.5*IQR

val outliers = df.filter(s"value < $lowerRange or value > $upperRange")
outliers.show()

{% endhighlight %}

In above code, we first calculate the ranges. Then we filter the data using data frame filters.

When we run this example, we get 10.2 and 16.4 as the outliers.

You can access complete example on [github](https://github.com/phatak-dev/Statistical-Data-Exploration-Using-Spark-2.0/blob/master/src/main/scala/com/madhukaraphatak/spark/dataexploration/OutliersWithIQR.scala).


## Conclusion

In this blog, we learned how to use quantiles to detect the outliers in data.  
