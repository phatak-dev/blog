---
layout: post
title: "Spark Plugin Framework in 3.0 - Part 1: Introduction"
date : 2020-04-07
categories: scala spark spark-three spark-plugin
---
Spark 3.0 brings a new plugin framework to spark. This plugin framework allows users to plugin custom code at the driver and workers. This will allow for advanced monitoring and custom metrics tracking. This set of API's are going to help tune spark better than before.

In this series of posts I will be discussing about the different aspects of plugin framework. This is the first post in the series, where we will understand the motivation behind the framework. You can read all the posts in the series [here](/categories/spark-plugin).


## Spark Plugin Framework in 3.0

Spark plugin framework is a new set of API's which allows users to run the custom code in the driver and executor side. This allows user to control the initialisation of driver and executor JVM's for different use cases.

## Uses of Plugin Framework

This section of the post talks about what are the different use cases for plugin framework.

### Support for Custom Metrics

Spark exposes wide variety of metrics like memory consumption, GC overhead etc which are very useful for the application tuning. But let's say user want to add their own metrics and track it using spark metrics framework?. Currently it's not possible because the code needs to run inside the executor/driver JVM to calculate the metrics.Also even if user calculates it using application code, those will not integrate with standard spark metrics. But with spark plugin it's possible to write code which collects the application specific metrics and integrate with spark metrics system.

### Ability to Push Dynamic Events to Driver and Executor

Spark plugin framework allows user to run arbitrary listeners on driver or executor side. This allows for a communication to spark JVM's from the external application. As these plugins have access to spark context, this will allow for dynamic control of the execution from outside which is very powerful. 


### Ability to Communicate Between Driver and Executor

Spark plugin framework exposes a RPC communication option between driver and executor plugins. This communication can be used to send any user defined messages between executors and driver.

The above are the some of the use cases for the spark plugin framework. These will become clear when we discuss the same with examples.


## References

[https://issues.apache.org/jira/browse/SPARK-29397](https://issues.apache.org/jira/browse/SPARK-29397).


## Conclusion

Spark plugin framework brings a powerful customization to spark ecosystem. Users can use this framework to add custom metric listeners and build a dynamic dispacth event system for their spark jobs. 
