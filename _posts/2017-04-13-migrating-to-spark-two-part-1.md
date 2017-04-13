---
layout: post
title: "Migrating to Spark 2.0 - Part 1: Scala Version and Dependencies" 
date : 2017-04-14
categories: scala spark spark-two-migration-series
---

Spark 2.0 brings a significant changes to abstractions and API's of spark platform. With performance boost, this version has made some of non backward compatible changes to the framework. To keep up to date with the latest updates, one need to migrate their spark 1.x code base to 2.x. In last few weeks, I was involved in migrating one of fairly large code base and found it quite involving process. In this series of posts, I will be documenting my experience of migration so it may help all the ones out there who are planning to do the same.

This is the first post in this series. In this post, we will discuss how to upgrade our dependencies to add right support for spark 2.0. You can access all the posts [here](/categories/spark-two-migration-series).


## Choosing Right Scala Version

When you want to upgrade from spark 1.x to spark 2.x, first task is to pick the right scala version. In spark 1.x, spark was built using scala version 2.10.6. But from spark 2.0, the default version is changed to 2.11.8. 2.10 version is still supported even though it's not default.

Scala major versions are non binary compatible, i.e you cannot mix and match the libraries built using 2.10 and 2.11. So whenever you change the scala version of the project, you need to upgrade all the libraries of the project including non-spark ones. It's a significant work as you need to comb through each and every dependency and make sure right version exist.

Initially I started the upgrade using Scala 2.10 as it was least resistance path. All the other external libraries needed no change and it was smooth. But I soon realised the distribution at spark download page [https://spark.apache.org/downloads.html](https://spark.apache.org/downloads.html) is only built using scala 2.11. So to support 2.10 I have to build my own distribution. Also I came across the [jira](https://issues.apache.org/jira/browse/SPARK-19810) which discusses about removing scala 2.10 support altogether in 2.3.0. So this meant investing in 2.10 will be not good as it will be obsolete in next few versions.

So I chose *2.11.8* as my scala version for upgrade. 


## Choosing Right Java Version

From Spark 2.1.0 version, support for Java 7 has been deprecated. So I started using Java 8 for building and deploying the code.


## Updating External Dependencies

One of the major challenges of changing scala version is to update all the project dependencies. My project had a fair bit of them and luckily all of those libraries had scala 2.11 version. So please make sure that all the libraries have 2.11 version before you make decision to change scala version.

## Updating Connectors

There are major changes happened to the connector ecosystem in spark 2.0. So when you are upgrading to spark 2.0, you need to make sure that you use the right connectors.

* ### Removal of Built in Streaming Connectors

Earlier spark had support for zeromq, twitter as part of spark streaming code base. But in spark 2.x, they have removed [it](https://issues.apache.org/jira/browse/SPARK-13843). No more these connectors are part of spark-streaming. This is done mostly to develop these connectors independent of spark versions. So if you are using these connector code will break.

To fix this issue, you need to update the dependencies to point to [Apache Bahir](https://github.com/apache/bahir). Apache Bahir is new home to all of this deleted connectors. Follow the README of bahir repository to update the dependencies to bahir ones.

* ### Spark 2.0 specific connectors 

Many popular connectors now give spark 2.0 specific connectors to build with. These connectors provide both scala 2.10 and 2.11 version. Choose the right one depending upon the scala version you have chosen. As I have chosen 2.11, the below are the some of updated connectors for some sources

* #### Elastic Search

Elastic search has a dedicated spark connector which was used to be called as elasticsearch-hadoop. You can access latest connector [here](https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch-spark-20_2.11/5.3.0).

* #### Csv Connector

From Spark 2.0, csv is built in source. Earlier we used to use [spark-csv](https://github.com/databricks/spark-csv).


If you are using any other connector, make sure they support 2.0. One thing to note that, if the connector is available in right scala version, it doesn't need any code changes to support spark 2.x. Spark 2.x data source API is backward compatible with spark 1.x


## Conclusion

So by updating scala version, java version and using right connectors you can update your project build to use spark 2.x.

## What's Next?

In next blog, we will be discuss about major changes in spark csv connector.

