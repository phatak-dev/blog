---
layout: post
title: "Understanding Spark Connect API - Part 3: Scala Hello World"
date : 2023-05-05
categories: scala python spark spark-connect
---

In Spark 3.4 version, spark has release a client/server based API called Spark Connect. This API will help in improving how we develop and deploy spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark connect API.This is the third post in the series where we discuss how to run a simple scala spark program against the spark connect API. You can read all the posts in the series [here](/categories/spark-connect).


## Running Spark Connect API Server

Before you can run a program against spark connect, you need to start spark connect server which in tern starts a spark driver. You can follow the steps below link for the same.

[https://spark.apache.org/docs/latest/spark-connect-overview.html](https://spark.apache.org/docs/latest/spark-connect-overview.html).

Once the spark connect server is started, it will running in localhost and you should be able to see a spark UI at [localhost:4040](locahost:4040).

## Adding Dependencies

For using spark connect API, we need to add the below dependencies in our sbt.

{% highlight scala %}

libraryDependencies += "org.apache.spark"%  "spark-connect-client-jvm_2.12"%"3.4.0"
libraryDependencies += "org.apache.spark"%  "spark-catalyst_2.12"%"3.4.0"

{% endhighlight %}


## Writing Simple Spark Example using the Spark Connect API

The below are the steps to write a simple scala spark example using spark-connect API.


### Create Spark Session using Spark Client API

As the first step we create a spark session.

{% highlight scala %}

import org.apache.spark.sql.SparkSession
val sparkSession = SparkSession.builder().remote("sc://localhost").build()

{% endhighlight %}

The above spark session api comes from spark-connect library, which has a special method called **remote** which signifies we are running our code against the spark connect rather than standard JVM based client.

### Create DataFrame

{% highlight scala %}

val df = sparkSession.range(500)
print(df.count())

{%endhighlight %}

Once we created the session, we can create a df using standard spark API **range** method. From there all the API's of dataframe are available.

Even though this look like normal df, we are actually interacting with spark connect df rather than standard spark sql df. You can find the docs for the same in below documentation

[https://github.com/apache/spark/blob/master/connector/connect/client/jvm/src/main/scala/org/apache/spark/sql/Dataset.scala](https://github.com/apache/spark/blob/master/connector/connect/client/jvm/src/main/scala/org/apache/spark/sql/Dataset.scala).


## Reference



## Conclusion

