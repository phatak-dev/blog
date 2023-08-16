---
layout: post
title: "Understanding Spark Connect API - Part 4: PySpark Example"
date : 2023-08-16
categories: scala python spark spark-connect
---

In the 3.4 version, Apache Spark has released a new client/server-based API called Spark Connect. This API will help in improving how we develop and deploy Spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark-connect API. This is the fourth post in the series where we will discuss PySpark integration. You can read all the posts in the series [here](/categories/spark-connect).

## Spark Connect API Server

Before you can run a program against spark connect, you need to start spark connect server which intern starts a spark driver. You can follow the steps below link for the same.

[https://spark.apache.org/docs/latest/spark-connect-overview.html#download-and-start-spark-server-with-spark-connect](https://spark.apache.org/docs/latest/spark-connect-overview.html#download-and-start-spark-server-with-spark-connect).

Once the spark connect server is started, it will be running in localhost and you should be able to see a spark UI at [http://localhost:4040/](http://localhost:4040/).

![Spark UI running Spark Connect](/images/spark_connect/spark_connect_spark_ui.png).

If we observe the rightmost part of UI, we can observe name as **Spark Connect server application UI** which confirms its a spark application running for spark connect.

## PySpark from Shell


{%highlight bash %}

bin/pyspark --remote sc://localhost

{%endhighlight%}

We can start the python shell by providing the remote as a parameter for spark session. We have seen this parameter in the earlier [scala example](/understanding-spark-connect-3#1-create-spark-session-using-spark-client-api).


## Spark Connect Spark Session

Once we started the pyspark shell, we can check if we are running against spark-connect API by inspecting the **spark** variable that points to spark session.

{% highlight python %}
spark
{% endhighlight %}

outputs

{%highlight text%}

<pyspark.sql.connect.session.SparkSession object at 0x10a067ac0>

{% endhighlight %}

From the above output, it is confirmed that we are running spark-connect based spark session.

## Data Frame Code

The below is a simple PySpark code.

{% highlight python %}

df = spark.range(100)
df.count()

{% endhighlight %}

As with [scala example](/understanding-spark-connect-3#3-executing-the-code), we can observe that in sparkUI gRpc calls will show up when we run this example.

![Spark UI after code execution](/images/spark_connect/spark_ui_after_helloworld.png)

## Conclusion

In this post, we saw how to write and run a simple PySpark code against the new spark-connect API.
