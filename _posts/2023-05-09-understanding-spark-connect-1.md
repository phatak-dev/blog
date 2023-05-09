---
layout: post
title: "Understanding Spark Connect API- Part 1: Shortcomings of Spark Driver Architecture"
date : 2023-05-09
categories: scala python spark spark-connect
---
In the 3.4 version, Apache Spark has released a new client/server-based API called Spark Connect. This API will help in improving how we develop and deploy Spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark connect API. This is the first post in the series where we discuss the shortcoming of current spark driver architecture. You can read all the posts in the series [here](/categories/spark-connect).

## Spark Driver Architecture

![JVM Integration](https://spark.apache.org/docs/latest/img/cluster-overview.png).

The above diagram shows the architecture of a spark program.

To use spark, the user needs to add spark library dependency to their application and run a JVM which will act as the client for the application. This client JVM, known as a driver,  holds the spark context, and the spark context intern communicates with other parts of the cluster to actually run the code. The same architecture is used for other language support like Pyspark.  

This approach works for batch-based programs which can run with spark-submit but come with many shortcomings for other kinds of applications. We will discuss these shortcomings in below section.

## Shortcomings of Spark Driver Architecture

### Interactive Spark Applications

If a user wants to run a spark application in an interactive system like a notebook or IDE they need a long-running spark session/context to which this system connects and executes code.

Current spark driver architecture makes building spark applications from systems like Notebook, and IDE very trick as the machine on which these systems run needs to have full-fledged spark installed and has to have enough resources to run the spark driver applications. This becomes an impediment in many applications as stopping the client system will suddenly stop the spark execution also.

### Community REST API Solutions

To address the above issue, there are many community based solutions like [Apache Livy](https://livy.apache.org/) or [Spark Job Server](https://github.com/spark-jobserver/spark-jobserver) which tries to provide the REST based interface for the Spark. But there few shortcomings in these solutions.

* Frameworks like spark-jobserver extends the spark source code, so they always lag behind the official spark release

* The API's exposed by these services are specific to the libraries so there is no inter portability

* As this is not part of official spark, these will be not available in managed spark runtime like Databricks.


### Conflict Between Spark Libraries and User Libraries

As spark driver needs to have full spark as its dependency, any libraries used in spark will have conflict with the user-added libraries. As an example, if there is a scala library used by the client app and some other version of the library used by spark it often leads to conflict with spark. This is one of the reasons to remove Akka from the spark dependency.

[https://issues.apache.org/jira/browse/SPARK-5293](https://issues.apache.org/jira/browse/SPARK-5293)

### Stability

As the client-server needs to run the driver application for interactive applications, if the driver crashes then the whole application will crash. So if you are running the spark application behind an HTTP server, the crash of the spark application can crash the HTTP server also which is not desired.

So because of the above shortcomings, we need a fully different way to develop spark applications for non-batch scenarios that will not have the above limitations.

## References

[Spark Connect SPIP Doc](https://docs.google.com/document/d/1Mnl6jmGszixLW4KcJU5j9IgpG9-UabS0dcM6PM2XGDc/edit#heading=h.wmsrrfealhrj).


## Conclusion

In this post, we talked short coming of the current spark driver based application development. In the next post in series, we will discuss how spark connect API is going to address some of these issues.
