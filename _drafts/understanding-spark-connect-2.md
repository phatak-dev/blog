---
layout: post
title: "Understanding Spark Connect API - Part 2: Architecture Introduction"
date : 2023-05-05
categories: scala python spark spark-connect
---

In Spark 3.4 version, spark has release a client/server based API called Spark Connect. This API will help in improving how we develop and deploy spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark connect API.This is the first post in the series where we discuss the shortcoming of current JVM based integration. You can read all the posts in the series [here](/categories/spark-connect).


## Current JVM Driver Based API Integration

In current approach of spark in any language, user need to add spark library dependency to their application and run a JVM which will act as the client for the application. This client JVM, known as driver,  holds the spark context and spark context intern communicates with other parts of cluster to actually run the code. This approach works for batch based programs which can run with spark-submit but comes with many shortcomings.

We will discuss these shortcomings in below section.

## Challenges with Current JVM Based Client Architecture

This section of post talks about the need for a server/client based API for spark.

## Interactive Spark Applications

If a user wants to run a spark application in an interactive system like notebook or IDE they need a long running spark session/context to which this system connects and executes code.

Current JVM based client makes building spark applications from systems like Notebook, IDE very trick as the machine on which these systems run they need to have full fledged spark installed and has to have enough resources to run the spark client/driver applications. This becomes an impedement in many applcations as stopping the client system will suddenly stop the spark execution also. 

### Community REST API Solutions

To address the above issue, there are many community based solutions like [Apache Livy](https://livy.apache.org/) or [Spark Job Server](https://github.com/spark-jobserver/spark-jobserver) which tries to provide the REST based interface for the Spark. But there few shortcomings

** Systems like spark-jobserver extends the spark source code so it always lag behind the official spark release

** The API's exposed by these services are specific to the libraries so there is no inter portability

** As this is not part of official spark, these will be not available in managed spark runtime like Databricks.


### Conflict Between Spark Libraries and User Libraries

As JVM based client needs to have full spark as it's dependency any libraries used in spark will have conflict with the user added libraries. As an example, if there is a scala library used by client app and if some other version of library used by spark it often leads to conflict with spark. This is one of the reason to remove Akka from the spark dependency

[https://issues.apache.org/jira/browse/SPARK-5293](https://issues.apache.org/jira/browse/SPARK-5293)

### Stability

As the client server needs to run the driver application for interactive applications, if the driver crashes then whole application will crash. So if you are running the spark application behind a HTTP server, crash of the spark application can crash the http server also which is not desired.

So because of the above shortcomings, we need a full different way to develop spark applications which will not have above limitations.


## References

[https://spark.apache.org/docs/latest/spark-connect-overview.html](https://spark.apache.org/docs/latest/spark-connect-overview.html).


## Conclusion

In this post, we talked short coming of the current JVM based integration in spark. 
