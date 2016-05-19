---
layout: post
title: "Introduction to Spark 2.0 - Part 5 : Time Window in Spark SQL"
date : 2016-05-19
categories: scala spark spark-two
---
Spark 2.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 2.0.

This is the fifth blog in series, where I will be discussing about time window API. You can access all the posts in the series [here](/categories/spark-two/).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark2.0-examples).


## Window API in Spark SQL

Spark introduced window API in 1.4 version to support smarter grouping functionalities. They are very useful for people coming from SQL background. One of the missing window API was ability to create windows using time. Time plays an important role in many industries like finance, telecommunication where understanding the data depending upon the time becomes crucial.

In Spark 2.0, framework has introduced built in support for time windows. These behave very similar to time windows in spark-streaming. In this blog post, I will be discussing about how to use this time window API.


## Time Series Data

Before we start doing time window, we need to have access to a time series data. For my example, I will be using data of Apple stock from 1980 to 2016. You can access the data [here](https://raw.githubusercontent.com/phatak-dev/spark2.0-examples/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/TimeWindowExample.scala). The original source of data is [yahoo finance](https://in.finance.yahoo.com/q/hp?s=AAPL).

The data has six columns. Out of those six, we are only interested in *Date*, which signifies the date of trade and *Close* which signifies end of the day value.

## Importing time series data to DataFrame

Once we have time series data, we need to import it to dataframe. All the time window API's need a column with type timestamp. Luckily *spark-csv* package can automatically infer the date formats from data and create schema accordingly. The below code is for importing with schema inference.

{% highlight scala %}
  
  val stocksDF = sparkSession.read.option("header","true").
      option("inferSchema","true")
      .csv("src/main/resources/applestock.csv")    

{%endhighlight%}


## Find weekly average in 2016

Once we have data is represented as dataframe, we can start doing time window analysis. In our analysis, we want to find weekly average of the stock for 2016. The below are the steps to do that.

### Step 1 : Filter data for 2016

As we are interested only in 2016, we need to filter the data for 2016. The below code show how to filter data on time.

{% highlight scala %}
  
 val stocks2016 = stocksDF.filter("year(Date)==2016")

{%endhighlight%}

We can use builtin function year, as Date is already represented as a timestamp. 


### Step 2 : Tumbling window to calculate average

Once we have filtered data, we need to create window for every 1 week. This kind of discretization of data is called as a tumbling window.

{% highlight scala %}
  
 val tumblingWindowDS = stocks2016
      .groupBy(window(stocks2016.col("Date"),"1 week"))
      .agg(avg("Close").as("weekly_average"))

{%endhighlight%}

The above code show how to use time window API. Window is normally used inside a group by. The first parameter signifies which column needs to be treated as time. Second parameter signifies the window duration. Window duration can be seconds, minutes, hours, days or weeks.

Once we have created window, we can run an aggregation like average as shown in the code.


### Step 3 : Printing the window values

Once we calculated the time window, we want to see the result. 

{% highlight scala %}

printWindow(tumblingWindowDS,"weekly_average")

{%endhighlight%}

The above code uses a helper function called *printWindow* which takes aggregated window dataframe and aggregated column name. The helper function looks as follows.

{% highlight scala %}

def printWindow(windowDF:DataFrame, aggCol:String) ={
    windowDF.sort("window.start").
    select("window.start","window.end",s"$aggCol").
    show(truncate = false)
 }

{%endhighlight%}

In above function, we are sorting dataframe using *window.start*. This column signifies the start time of window. This sorting helps us to understand the output better. Once we have sorted, we print start,end, aggregated value. As the timestamp can be long, we tell the show not to truncate results for better display.

When you run the example, we see the below result.

~~~
+---------------------+---------------------+------------------+
|start                |end                  |weekly_average    |
+---------------------+---------------------+------------------+
|2015-12-31 05:30:00.0|2016-01-07 05:30:00.0|101.30249774999999|
|2016-01-07 05:30:00.0|2016-01-14 05:30:00.0|98.47199859999999 |
|2016-01-14 05:30:00.0|2016-01-21 05:30:00.0|96.72000125000001 |
|2016-01-21 05:30:00.0|2016-01-28 05:30:00.0|97.6719984        |

~~~

One thing you may observe is the date is started from 31st and first week is considered till 7. But if you go through the data, the first entry for 2016 start from 2016-01-04. The reason is there was no trading on 1st as it's new year, 2 and 3 as they are weekend. 

We can fix this by specifying the start time for window, which signifies the offset from which window should start.


## Time window with start time

In earlier code, we used a tumbling window. In order to specify start time we need to use a sliding window. As of now, there is no API which combines tumbling window with start time. We can create tumbling window effect by keeping both window duration and slide duration same.

{% highlight scala %}

val windowWithStartTime = stocks2016.groupBy(window(stocks2016.col("Date"),
                          "1 week","1 week", "4 days"))
                          .agg(avg("Close").as("weekly_average"))

{%endhighlight%}

In above code, we specify "4 days" which is a offset for start time. The first two parameters specify window duration and slide duration.When we run this code, we observe the below result

~~~
+---------------------+---------------------+------------------+
|start                |end                  |weekly_average    |
+---------------------+---------------------+------------------+
|2015-12-28 05:30:00.0|2016-01-04 05:30:00.0|105.349998        |
|2016-01-04 05:30:00.0|2016-01-11 05:30:00.0|99.0699982        |
|2016-01-11 05:30:00.0|2016-01-18 05:30:00.0|98.49999799999999 |
|2016-01-18 05:30:00.0|2016-01-25 05:30:00.0|98.1220016        |
~~~

Now we have a week starting from *2016-01-04*. Still we have initial row which is take from 2015. The reason is, as our start time is 4 days, it creates a window till that time from last seven days.We can remove this row easily using filter as below.

{% highlight scala %}

val filteredWindow = windowWithStartTime.filter("year(window.start)=2016")

{%endhighlight%}

Now we will see the expected result.

~~~
+---------------------+---------------------+------------------+
|start                |end                  |weekly_average    |
+---------------------+---------------------+------------------+
|2016-01-04 05:30:00.0|2016-01-11 05:30:00.0|99.0699982        |
|2016-01-11 05:30:00.0|2016-01-18 05:30:00.0|98.49999799999999 |
|2016-01-18 05:30:00.0|2016-01-25 05:30:00.0|98.1220016        |
|2016-01-25 05:30:00.0|2016-02-01 05:30:00.0|96.2539976        |
|2016-02-01 05:30:00.0|2016-02-08 05:30:00.0|95.29199960000001 |
~~~

You can access complete code [here](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/TimeWindowExample.scala).

So now we know how to use time windows in Spark 2.0. This is one of the powerful feature which helps in wide variety analysis in big data.

