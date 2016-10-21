---
layout: post
title: "Statistical Data Exploration using Spark 2.0 : Part 1 - Five Number Summary"
date : 2016-10-21
categories: scala spark statistical-data-exploration
---
Data exploration is an important part of data analysis to understand nature of data. Data scientists use various mathematical and statistics techniques to understand the distribution and shape of the data which comes handy to draw conclusions.

Recently I started going through the coursera course "Making Sense of Data" videos to understand the data exploration techniques. Its an excellent course which explains all the basics of theory and practical aspects of data exploration. Currently the course is not available at the coursera. But you can find recording of earlier course on [youtube](https://www.youtube.com/watch?v=rXZD3yVFN9w&list=PL7wD1yDs0UYYmTGN3ZJnZuwloaoEuHvmE).

Most of the examples of course are explained using R programming language. As Spark 2.0 and R share dataframe as common abstraction, I thought it will be interesting to explore possibility of using Spark dataframe/datasets abstractions to do explore the data. 

This series of blog posts are focused  on the data exploration using spark. I will be comparing the R dataframe capabilities with spark ones. I will be using Spark 2.0 version with Scala API and Zeppelin notebooks for visualizations.This is the first blog in series where we will be discussing how to derive summary statistics of a dataset. You can find all other blogs in the series [here](/categories/statistical-data-exploration).

TL;DR All code examples available on [github](https://github.com/phatak-dev/Statistical-Data-Exploration-Using-Spark-2.0).

## Loading Dataset
For our example, we will be using life expectancy dataset. This is a dataset which has average life expectancy of all countries across the world. Its space separated file with following three fields.

  * Country 
  * Life Expectancy
  * Region

The below code shows to how to read the data in R
{% highlight R %}
df <- read.table("LifeExpentancy.txt")
colnames(df) <- c("Country","LifeExp","Region")
{%endhighlight %}

We can load the same data in spark as below

{% highlight scala %}
val rawDF = sparkSession.read.format("csv").option("delimiter"," ").
   option("inferSchema","true").load("src/main/resources/LifeExpentancy.txt")
   //only extract the values we need
   val schema = StructType(Array(
           StructField("Country",StringType),
           StructField("LifeExp",DoubleType),
           StructField("Region",StringType)
   ))
   val selectedDF = rawDF.select("_c0","_c2","_c4")
   val lifeExpectancyDF = sparkSession.createDataFrame(selectedDF.rdd,schema)
   lifeExpectancyDF
 }
{% endhighlight %}

We have to do little bit more work in spark as spark-csv package doesn't handle double spaces properly.

Now we have data ready to explore.

## Five Number Summary

Five number summary is one of the basic data exploration technique where we will find how values of dataset columns are distributed. In our example, we are interested to know the summary of "LifeExp" column.

Five Number Summary Contains following information

 * Min - Minimum value of the column
 * First Quantile - The 25% th data
 * Median - Middle Value
 * Third Quartile - 75% of the value
 * Max - maximum value

The above values gives a fair idea about how the values are distributed for the column. For categorical columns it will be different. We will discuss about that in future blogs.

The below code in R allows to compute above values
{% highlight R %}
attach(df)
summary(LifeExp)
{% endhighlight %}

We get below result 

{% highlight text%}

 Min. 1st Qu.  Median    Mean 3rd Qu.    Max.
  47.79   64.67   73.24   69.86   76.65   83.39
{% endhighlight %}

R shows mean also as part of the summary.

## Five Number Summary in Spark

In Spark, we can get same information using *describe* method. 

{% highlight scala %}
lifeExpectancyDF.describe("LifeExp").show()
{% endhighlight %}

The below is the output

{% highlight text %}

+-------+-----------------+
|summary|          LifeExp|
+-------+-----------------+
|  count|              197|
|   mean|69.86281725888323|
| stddev|9.668736205594511|
|    min|           47.794|
|    max|           83.394|
+-------+-----------------+

{% endhighlight %}

If you observe the result, rather than giving quantiles values and median, spark gives standard deviation. The reason is, median and quantiles are costly to compute on large data. Both values need data to be in sorted order and result in skewed calculations.

## Calculating Quantiles in Spark

We can calculate quartiles using *approxQuantile* method introduced in spark 2.0. This allows us to find 25%, median and 75% values like R. The name of the method suggests that we can get approximate values whenever we specify the error rate. This makes calculations much faster compared to absolute value.

In our example we will be calculating the exact values so that we can compare to R.

{% highlight scala %}

val medianAndQuantiles = lifeExpectancyDF.stat.approxQuantile("LifeExp",
           Array(0.25,0.5,0.75),0.0)
{% endhighlight %}

In above code, the below are the parameter to *approxQuantile* method

* Name of the column
* Array of values signifying which quantile we want. In our example we are calculating 25%, 50% and 75%. 50% is same as median

* The last parameter signifies the error rate. 0.0 signifies we want exact value.

The below is the result 

{% highlight text %}
List(64.986, 73.339, 76.835)
{% endhighlight %}

If you compare the result, it matches with the R values.

You can access complete code on [github](https://github.com/phatak-dev/Statistical-Data-Exploration-Using-Spark-2.0/blob/master/src/main/scala/com/madhukaraphatak/spark/dataexploration/SummaryExample.scala).

## Conclusion

In this post we discussed how to get started with exploring data using statistics in spark 2.0. In future blogs we will discuss further more techniques and API's. 
