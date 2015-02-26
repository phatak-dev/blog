---           
layout: post
title: "Apache Spark is not a one-trick pony : Going beyond in-memory processing"
date : 2015-02-26
categories: spark
---

At [DataMantra](http://datamantra.io), whenever we suggest Apache Spark to our customers, they respond *"it just in-memory thing right?"*. There is a misconception about Spark,  that it's just about in-memory computation. Even creators of spark acknowledge it. In one of their recent [blog post](https://databricks.com/blog/2014/10/10/spark-petabyte-sort.html) they say

> This has always been one of the most common misconceptions about Spark, especially for people new to the community. Spark is well known for its in-memory performance, but from its inception Spark was designed to be a general execution engine that works both in-memory and on-disk. Almost all Spark operators perform external operations when data does not fit in memory. More generally, Spark’s operators are a strict superset of MapReduce.

Though in-memory is an important feature, it's not only thing that makes Spark special. So in this blog post, I am going to discuss about few of the other important features that make Spark stand out.

(Note : This post originally published in DataMantra [blog](http://datamantra.io/blog/2015/02/16/apache-spark-not-a-one-trick-pony/))

## 1. One platform for all Big data loads

Big data analysis has different data loads. Different use cases need different kind of analysis. Most of the big data analysis need both batch processing and real time capabilities. Not only that, they have structured, unstructured, graph analysis and other advanced processing needs too. So a big data processing framework has to cater to all of this needs.

Hadoop M/R, current leading big data processing framework,  is only optimized for batch processing. So in hadoop, you need to go to other frameworks like Storm for real time and Apache Giraph for graph processing capabilities. Having multiple different frameworks is a pain for development and maintenance. Though introduction of YARN has solved few issues, YARN is too low level to be used in applications.

In case of Spark, it was designed to cater to multiple data loads from day one. Batch processing and real time capabilities are built in to the core of Spark. Not only that many advanced graph and machine learning libraries are built in so that it can cater to wide variety of data analysis needs.

## 2. One abstraction to rule them all

In Spark, all APIs and libraries talk same abstraction called RDD. RDD stands for Resilient Distributed Dataset. It's just a big collection of immutable data which is sitting in some storage. But what's the advantage if all libraries talk in RDD?

The advantage is that you can mix and match different kind of processing in same application. You can take a stream of data from spark real time system and can run sql queries using SparkSQL. Then take the output of SparkSQL and feed it to machine learning algorithm using MLLib. All of this is done without ever have to convert or store intermediate result. This is very powerful to build complex data flow systems.


## 3. Runs everywhere

Spark runs everywhere. It is designed to run on different platforms and different distributed systems. You can run Spark on Hadoop 1.0, Hadoop 2.0, Apache Mesos or stand alone spark cluster. This flexibility of deployment is very attractive to customers, as it allows them to harness power of spark using their existing infrastructure investments.


## 4. Small and Simple
The original release of Spark contained only 1600 lines of Scala code. It was designed to be extremely modular. So today you can add or remove capabilities to your spark application simply by changing your build file. Also having a small code base makes extend the framework easy.

Spark API thrives for simplicity. If you ever seen a spark word count vs Map/Reduce word count you will understand how simple API is. There is a lot of thought gone in to the API to make it more approachable and consistent. Contrast to that Java API of Map/Reduce is a mess. There is too much verbosity.


## 5. Prospering Ecosystem
In Hadoop, ecosystem was an after thought. The ecosystem projects like Hive and Pig dint have access to Map/Reduce abstractions. All they can do was generate Map/Reduce programs on the fly and run it on cluster. This severely effected their performance.

Where as in Spark, there was a plan for ecosystem from day one. The ecosystem libraries Pregel and MLLib were developed side by side with core spark. So in Spark, all libraries have access to same level of abstraction as main API. This makes them first class citizen on the platform.


## 6. Multi language API

Spark officially supports Scala, Java and Python API's. This makes it more attractable to many developers compared to java only M/R. Though M/R supported C++ and other API's they are not up to date like Java API. So Spark is great option for developers with different backgrounds.

Now you know spark is not just about in-memory processing. Let's break this myth by sharing this article with everyone.