---
layout: post
title: "Introduction to Spark 3.0 - Part 7 : Dynamic Allocation Without External Shuffle Service"
date : 2020-04-09
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.

This is the seventh post in the series where I am going to talk about improvements in dynamic allocation. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).


## Dynamic Allocation and External Shuffle Service

In spark 2.x, if the user wants to use dynamic allocation of executors, then external shuffle service was a pre requisite. This pre requisite was needed as the spark needed to make sure that the removal of the executors doesn't remove shuffle data. Having an external shuffle service make sure that all the data is stored outside of executors.

## Challenges with External Shuffle Service

External shuffle service is implemented differently for different deployments like Standalone, YARN etc. So whenever there is new deployment option like kubernetes, then the shuffle service needs to implemented to support dynamic allocation.

Also having separate storage for external shuffle service is not attractive in dynamic clusters like kubernetes. So there was need for an improvement here.


## Dynamic Allocation Improvement in 3.0

In 3.0, spark has introduced a beta feature where dynamic allocation can be run without external shuffle service. This is achieved by adding intelligence within spark dynamic scaler to track the location of shuffle data and removing executors accordingly. This feature can be enabled using **spark.dynamicAllocation.shuffleTracking.enabled**.

## Example

The below is a command where we are running spark PI examples in dynamic allocation mode without an external shuffle service. 

{% highlight sh %}

bin/spark-submit --class org.apache.spark.examples.SparkPi --master spark://localhost:7077  --executor-memory 2G --num-executors 1  --conf spark.dynamicAllocation.maxExecutors=3 --conf spark.executor.cores=1  --conf spark.dynamicAllocation.enabled=true --conf spark.dynamicAllocation.shuffleTracking.enabled=true examples/jars/spark-examples_2.12-3.0.0-preview2.jar 10000

{% endhighlight %}

From the above command, you can observe that **spark.dynamicAllocation.shuffleTracking.enable** is set and no external shuffle service is set.

## References

[https://issues.apache.org/jira/browse/SPARK-27963](https://issues.apache.org/jira/browse/SPARK-27963).
