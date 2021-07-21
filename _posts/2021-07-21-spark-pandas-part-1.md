---
layout: post
title: "Pandas API on Apache Spark - Part 1: Introduction"
date : 2021-07-21
categories: spark python spark-pandas
---
Apache Spark has revolutionised the data science field with it's support for big data. With it support for multiple languages like Scala, Python it has made big data analysis available to wide variety of developers.

Python is the leading language preferred by the data science community. Even with in Spark community, python API has seen tremendous upsurge in last few years. According to databricks, company behind the Apache Spark, 60% of the commands written on their notebook is python compared to 23% of them in Scala.

Spark has excellent support for python with Pyspark project. Pyspark allows developers to access all different parts of spark like SQL,ML etc using python language.  
Still it has not yet reached wider python community. The reason is majority of python data developers prefer Pandas API.


## PySpark vs Pandas

Pandas is the de facto library for python data science community to manipulate data in python. Also pandas integrates seamlessly with other python libraries like plotly, scikit learn. So most developers are very comfortable with it's API.

Whereas Pyspark is completely different API which is modeled after Spark dataframe DSL. Even though it can work on large data in distributed manner and has almost similar functionalities like pandas, there is steep learning to understand these API's. Also pyspark doesn't play well with other python data libraries. So adoption of pyspark in wider data science community is significantly less.

## Koalas

To address this gap databricks released a library called [Koalas](https://koalas.readthedocs.io/en/latest/). It is a spark library which brought the pandas API on top spark dataframe.

Koalas allows python developers to write pandas API code on top spark dataframe which gives best of both worlds. Now developers can write code in pandas API and get all the performance benefits of spark.

Koalas has been quite successful with python community. Now this support going to become even better with Spark 3.2

## Pandas API on Pyspark

From spark 3.2, pandas API will be added to mainline spark project. No more need of third party library. So pandas API going to be yet another API with Dataframe DSL and SQL API to manipulate data in spark.

This support suddenly opens up wide variety of use cases for python developers and is one of the very powerful API introduction in Apache spark project.

In next series of blogs, I will be discussing about this API and use cases for the same. You can read all the posts [here](/categories/spark-pandas).


## References

* [https://issues.apache.org/jira/browse/SPARK-34849](https://issues.apache.org/jira/browse/SPARK-34849)
* [Koalas Design Doc](https://docs.google.com/document/d/1tk24aq6FV5Wu2bX_Ym606doLFnrZsh4FdUd52FqojZU/edit)
* [Spark Summit Talk](https://www.youtube.com/watch?v=8P-lWuMm2UQ)
