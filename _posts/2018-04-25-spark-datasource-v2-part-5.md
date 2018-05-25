---
layout: post
title: "Exploring Spark DataSource V2 - Part 5  : Filter Push"
date : 2018-05-25
categories: scala spark datasource-v2-series
---

In spark, data source is one of the foundational API for structured data analysis. Combined with dataframe and spark SQL abstractions, it makes spark one of the most complete structured data engine out there. You can read more about structured data analysis in spark [here](/categories/datasource-series).

Data source API was introduced in spark 1.3 along with dataframe abstraction. After that release, spark has undergone tremendous change. Spark has moved to custom memory management and with 2.0 we got Dataset , a better dataframe, abstraction. With these tremendous changes data source API needed to revisited.

So in 2.3 version, spark has released new version of data source API known as as data source V2. This API reflects all the learning spark developers learnt in last few releases. This API will be foundation for next few years of spark data source connectivity.

In this series of posts, I will be discussing about different parts of the API. We will be learning API by building data sources for different sources like flat file, relational databases etc.

This is fifth blog in the series where we discuss about implementing filter push. You can read all the posts in the series [here](/categories/datasource-v2-series).

## Mysql Datasource

To understand how to implement filter push, we will be using a mysql datasource rather than in-memory datasource. A mysql datasource is similar to our earlier in-memory datasource, except it reads the data from mysql database rather than in-memory array. We will be using JDBC API to read from mysql. The below is the code in Reader interface to setup an iterator and read data.

{% highlight scala %}

 def next =  {
 if(iterator == null) {
    val url = "jdbc:mysql://localhost/mysql"
    val user = "root"
    val password = "abc123"

    val properties = new java.util.Properties()
    properties.setProperty("user",user)
    properties.setProperty("password",password)


    val sparkSession = SparkSession.builder.getOrCreate()
    val df = sparkSession.read.jdbc(url,getQuery,properties)
    val rdd = df.rdd
    val partition = rdd.partitions(0)
    iterator = rdd.iterator(partition, org.apache.spark.TaskContext.get())
   }
   iterator.hasNext
  }

  def get = {
     iterator.next()
  }
 
{% endhighlight %}

As you can see from above code, we are using jdbc and *sparkSession.read.jdbc* API's to read the data. In our example, we are assuming all the data coming from single partition. We will fix this in upcoming examples.

Once we setup the iterator, get method is just calling next method on the iterators.

## Filter Pushdown

In data sources, often we don't want to read complete data from the source. In many cases, we will be analysing subset of data for our analysis. This is expressed as the filter in spark SQL code.

In normal sources, to implement filter, the complete data is brought to spark engine and then filtering is done. This is ok for sources such as file source or hdfs source. But for sources like relational databases this is very inefficient. These sources have an ability to filter data in source itself, rather than brining them to spark.

So in Datasource V2 there is new API to specify that source supports source level filtering. This helps us to reduce the amount of data transfer between the source and spark.

## Filter Push in Mysql Source

The below are the steps to add filter push support for the mysql data source.

### 1. Implement SupportsPushDownFilter Interface

We need to implement *SupportsPushDownFilter* interface to indicate to the spark engine that source supports filter pushdown. This needs to be implemented by Datasource Reader.

{% highlight scala %}

class SimpleMysqlDataSourceReader() extends DataSourceReader with SupportsPushDownFilters {
  var pushedFilters:Array[Filter] = Array[Filter]()

  def pushFilters(filters:Array[Filter]) = {
     println(filters.toList)
     pushedFilters = filters
     pushedFilters
  }
{% endhighlight %}

In above code, we have implemented the interface. Then we have overridden the *pushedFilters* method to capture the filters. In this code, we just remember the filters in a variable.

### 2. Implement Filter Pushdown in Mysql Query

Once we have captured the filters, we need to use them to create jdbc queries to push them to the source. This is implemented in *DataReader*. 

{% highlight scala %}

class SimpleMysqlDataReader(pushedFilters:Array[Filter]) extends DataReader[Row] {

  val getQuery:String = {
   if(pushedFilters == null || pushedFilters.isEmpty)
     "(select user from user)a"
   else {
    pushedFilters(1) match {
    case filter : EqualTo =>
    val condition = s"${filter.attribute} = '${filter.value}'"
    s"(select user from user where $condition)a"
    case _ =>"(select user from user)a"
    }
   }
  }

{% endhighlight %}

In above code, the pushed filters are taken an class parameters. Once we have filters available to us, we write a method which generates the queries depending upon the filters.In the query column name and table name is hard coded. This is done to simplify over all code. In real world scenario these will be passed as options.

In the code, if there is no filter we just read all the data. But if there is a filter, we generate the table query which will have a where condition. In our example, we only support *equal to* . But you can support other ones also. 

Also in code, we are looking at second filter ( 1 index in pushed filters ). There is  a reason for that. We will understand more when we see in a example. 

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/datasourcev2/SimpleMysqlDataSource.scala).

##Using Mysql Datasource with Filter Push

Once we have implemented filter push, we can test it from an example. 

{% highlight scala %}

val simpleMysqlDf = sparkSession.read.format(
"com.madhukaraphatak.examples.sparktwo.
         datasourcev2.simplemysql")
         .load()
simpleMysqlDf.filter("user=\"root\"").show()
{% endhighlight %}

In above code, we read from our source and add a filter for user.

The above code prints below result


{% highlight text %}
List(IsNotNull(user), EqualTo(user,root))

+----+
|user|
+----+
|root|
|root|
|root|
|root|
+----+

{% endhighlight %}

The first line of result signifies the filters pushed for the source. As you can see here, even though we have specified only one filter in our spark sql code, spark has pushed two of them. The reason is , spark always checks for the rows where there are no nulls. This simplifies upstream code to do the aggregations etc. The second filter is the one which we are interested.

Once filter is done, we see all the rows where filter matches. You can verify the is filter is pushed or not from mysql logs. The mysql log should show a query like below. You may need to enable logging in mysql.

{% highlight text %}

119 Query     SELECT `user` FROM (select user from user where user = 'root')a

{% endhighlight %}

Above line makes sures that actual source is getting query with filter.


## Conclusion

In this post, we have discussed how to implement filter push down in datasource V2 API. Implementing filter pushdown, greatly reduces the data transfer between source and spark engine, which intern makes the overall data source more performant.
