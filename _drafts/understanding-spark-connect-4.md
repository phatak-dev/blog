---
layout: post
title: "Understanding Spark Connect API- Part 4: Pyspark Example"
date : 2023-05-05
categories: scala python spark spark-connect
---

In the 3.4 version, Apache Spark has released a new client/server-based API called Spark Connect. This API will help in improving how we develop and deploy Spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark connect API. This is the fourth post in the series where we will discuss pyspark integration. You can read all the posts in the series [here](/categories/spark-connect).

## Spark Connect API Server

Before you can run a program against spark connect, you need to start spark connect server which intern starts a spark driver. You can follow the steps below link for the same.

[https://spark.apache.org/docs/latest/spark-connect-overview.html#download-and-start-spark-server-with-spark-connect](https://spark.apache.org/docs/latest/spark-connect-overview.html#download-and-start-spark-server-with-spark-connect).

Once the spark connect server is started, it will running in localhost and you should be able to see a spark UI at [http://localhost:4040/](http://localhost:4040/).

![Spark UI running Spark Connect](/images/spark_connect/spark_connect_spark_ui.png).

If we observe the rightmost part of UI, we can observe name as **Spark Connect server application UI** which confirms its a spark application running for spark connect.

## Pyspark from Shell

For using spark connect API, we need to add the below dependencies in our sbt.

{% highlight scala %}

libraryDependencies += "org.apache.spark"%  "spark-connect-client-jvm_2.12"%"3.4.0"
libraryDependencies += "org.apache.spark"%  "spark-catalyst_2.12"%"3.4.0"

{% endhighlight %}


In above dependencies, 

 1. **spark-connect-client-jvm** is the spark-connect API for Scala and Java. 

 2. **spark-catalyst** is the catalyst API of spark dataframe. As the spark-connect manipulates the logical plans, we require this dependency.


## Writing Simple Spark Example using the Spark Connect API

The below are the steps to write a simple scala spark example using spark-connect API.


### 1. Create Spark Session using Spark Client API

As the first step we create a spark session.

{% highlight scala %}

import org.apache.spark.sql.SparkSession
val sparkSession = SparkSession.builder().remote("sc://localhost").build()

{% endhighlight %}

The above spark session API comes from spark-connect library, which has a special method called **remote** which signifies we are running our code against the spark connect rather than standard JVM based client.

### 2. Create DataFrame

{% highlight scala %}

val df = sparkSession.range(500)
print(df.count())

{%endhighlight %}

Once we created the session, we can create a df using standard spark API **range** method. From there all the API's of dataframe are available.

Even though this look like normal df, we are actually interacting with spark connect df rather than standard spark sql df. You can find the docs for the same in below documentation

[https://github.com/apache/spark/blob/master/connector/connect/client/jvm/src/main/scala/org/apache/spark/sql/Dataset.scala](https://github.com/apache/spark/blob/master/connector/connect/client/jvm/src/main/scala/org/apache/spark/sql/Dataset.scala).


### 3. Executing the Code

Once you execute the code, you will see the below output on spark UI.

![Spark UI after hello world execution](/images/spark_connect/spark_ui_after_helloworld.png).

From the output its clear the code is running on gRPC API of spark-connect.

## Conclusion

In this post, we saw how to write and run a simple spark scala code against the new spark-connect API.
