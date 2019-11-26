---
layout: post
title: "Data Modeling in Apache Spark - Part 1 : Date Dimension"
date : 2019-11-26
categories: spark data-modeling scala
---
Data modeling is one of the important aspect of data analysis. Having right kind of model, allows user to ask business questions easily. The data modeling techniques have been bedrock of the SQL data warehouses in past few decades. 

As Apache Spark has become new warehousing technology, we should be able to use the earlier data modeling techniques in spark also. This makes Spark data pipelines much more effective.

In this series of posts, I will be discussing different data modeling in the context of spark. This is the first post in the series which discusses about using date dimension. You can access all the posts in the series [here](/categories/data-modeling).


## Importance of Data and Time in Data Analysis

Most of the data that we analyse typically captured contains Date or Timestamp. For example, it may be

• Trading date of the Stock

• Time of the transactions in POS systems

Many of the analysis what we do typically is around the time. We typically want to slice and dice the data time only.

## Date Analysis using Built In Spark

This section of the document talks about the how to analyse a time series data using builtin spark date functions.

### Apple Stocks Data

For this example, we will be using Apple Stocks data. The below is the sample data

{% highlight text %}

+-------------------+----------+----------+----------+----------+---------+---------+
|               Date|      Open|      High|       Low|     Close|   Volume| AdjClose|
+-------------------+----------+----------+----------+----------+---------+---------+
|2013-12-31 00:00:00|554.170013|561.279976|554.000023|561.019997| 55771100|76.297771|
|2013-12-30 00:00:00|557.460022|560.089989|552.319984|554.519981| 63407400| 75.41378|
|-----------------------------------------------------------------------------------|

{% endhighlight %}

### Loading into Spark Dataframe

The below code loads data into spark dataframe.

{% highlight scala %}
val appleStockDf = sparkSession.read.format("csv").
      option("header","true")
      .option("inferSchema","true")
      .load("src/main/resources/applestock_2013.csv")

{% endhighlight %}

### Date Analysis

In this section, let's see how we can answer questions. 

#### Is there any records which belongs to weekend?

This analysis is typically done to make sure the quality of the data. There should not be any data for weekend as there will be no trading done on weekend.

{% highlight scala %}

  assert(sparkSession.sql
       ("select * from stocks where dayofweek(Date)==1 or 
       dayofweek(Date)==7").count() == 0)

{% endhighlight %}

In above code, **1** signifies **Sunday** and **7** signifies **Saturday**. As we can see here code is not readable unless we know how to encode those numbers.

#### Show Quarterly Max Price

This analysis finds the maximum for a given quarter. 

{% highlight scala %}
appleStockDf.groupBy(year($"Date"),quarter($"Date")).
      avg("Close").
      sort("year(Date)","quarter(Date)")
      .show()
{% endhighlight %}

## Challenges with Date Analysis using Spark Date Functions

Even though we can do the above analysis using spark builtin date functions, writing them is tricky. Also these one cannot be easily expressed from an external BI solutions, where typically business analyst users are the end users. So we need an easier and better way to do the above.

## Date Dimension

Date dimension is a static dataset which lists all the different properties of a given date in it's columns. This sample dataset looks as below

{% highlight text %}

t
 |-- date_key: integer (nullable = true)
 |-- full_date: string (nullable = true)
 |-- day_of_week: integer (nullable = true)
 |-- day_num_in_month: integer (nullable = true)
 |-- day_num_overall: integer (nullable = true)
 |-- day_name: string (nullable = true)
 |-- day_abbrev: string (nullable = true)
 |-- weekday_flag: string (nullable = true)
 |-- week_num_in_year: integer (nullable = true)
 |-- week_num_overall: integer (nullable = true)
 |-- week_begin_date: string (nullable = true)
 |-- week_begin_date_key: integer (nullable = true)
 |-- month: integer (nullable = true)
 |-- month_num_overall: integer (nullable = true)
 |-- month_name: string (nullable = true)
 |-- month_abbrev: string (nullable = true)
 |-- quarter: integer (nullable = true)
 |-- year: integer (nullable = true)
 |-- yearmo: integer (nullable = true)
 |-- fiscal_month: integer (nullable = true)
 |-- fiscal_quarter: integer (nullable = true)
 |-- fiscal_year: integer (nullable = true)
 |-- last_day_in_month_flag: string (nullable = true)
 |-- same_day_year_ago: string (nullable = true)

{% endhighlight %}

In above schema, important columns are

• full_date - Timestamp for given day
• year - year in the date
• quarter - quarter the given date belongs 

etc. 

This static dataset can be generated for multi years and kept available. A sample we are using in the example can be downloaded from below link.

[https://www.kimballgroup.com/data-warehouse-business-intelligence-resources/books/microsoft-data-warehouse-dw-toolkit/](https://www.kimballgroup.com/data-warehouse-business-intelligence-resources/books/microsoft-data-warehouse-dw-toolkit/).
## Date Analysis using Date Dimension

This section of the document talks about how to do the above analysis using date dimension.

### Loading the Data to Spark Dataframe

We can create a dataframe for our date dataset as below

{% highlight scala %}

val originalDf = sparkSession.read.format("csv").
      option("header","true")
      .option("inferSchema","true")
      .load(dataPath)

    //replace space in the column names
    val new_columns = originalDf.schema.fields
      .map(value => value.copy(name = value.name.replaceAll("\\s+","_")))

    val newSchema = StructType(new_columns)
    val newNameDf = sparkSession.createDataFrame(originalDf.rdd, newSchema)

    import org.apache.spark.sql.functions._
    val dateDf = newNameDf.withColumn("full_date_formatted",
      to_date(newNameDf.col("full_date"),"dd/MM/yy"))

{% endhighlight %}
In the above code, preprocessing is done to convert the *String* to spark *date* datatype.


### Joining with Stocks Data

We can combine stocks data with Date using spark joins

{% highlight scala %}

val joinedDF = appleStockDf.join(dateDf, appleStockDf.col("Date") ===
      dateDf.col("full_date_formatted"))

{% endhighlight %}

This join doesn't increase size of the data as it's an inner join.

### Analysis

This section shows how the analysis can be done without using complex spark functions

#### Is there any records which belongs to weekend?

{% highlight scala %}

assert(joinedDF.filter("weekday_flag != 'y'").count()==0)

{% endhighlight %}

#### Show Quarterly Max Price
 
{% highlight scala %}

joinedDF.groupBy("year","quarter").
      avg("Close").
      sort("year","quarter")
      .show()
{% endhighlight %}


## Advantages of Date Dimension

This section discusses about the advantages of date dimension.

### Reuse Across Different Analysis

Same dataset can be used for different data analysis. Rather writing special functions in the query or adding these columns on dataset itself, having a standard date dimension helps to standardise all date analysis.

### Scalable
Users can add more properties on date dimension like regional holidays etc. This will enrich the analysis for every one. No additional querying is needed there.

### User Friendly

The queries generated by using date dimension are more easier to understand.

## Reference

[https://www.kimballgroup.com/data-warehouse-business-intelligence-resources/kimball-techniques/dimensional-modeling-techniques/calendar-date-dimension/](https://www.kimballgroup.com/data-warehouse-business-intelligence-resources/books/microsoft-data-warehouse-dw-toolkit/).
