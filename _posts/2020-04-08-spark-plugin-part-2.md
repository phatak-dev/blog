---
layout: post
title: "Spark Plugin Framework in 3.0 - Part 2 : Anatomy of the API"
date : 2020-04-08
categories: scala spark spark-three spark-plugin
---
Spark 3.0 brings a new plugin framework to spark. This plugin framework allows users to plugin custom code at the driver and workers. This will allow for advanced monitoring and custom metrics tracking. This set of API's are going to help tune spark better than before.

In this series of posts I will be discussing about the different aspects of plugin framework. This is the second post in the series, where we will understand the different API's exposed in framework. You can read all the posts in the series [here](/categories/spark-plugin).


## Spark Plugin Interface

The top interface of the framework in SparkPlugin. It exposes below two methods

{% highlight scala %}

def driverPlugin(): DriverPlugin

def executorPlugin(): ExecutorPlugin 

{% endhighlight %}

From the name of methods, we can figure out that these are entry points to specify the driver and executor plugin. If user wants to implement only one, then they can return null in other method.


## DriverPlugin Interface

This is the interface for the driver side plugin. It has below methods. All are optional to override.


### Init Method

{% highlight scala %}

def init(sc: SparkContext, pluginContext: PluginContext): util.Map[String, String] 

{% endhighlight %}

This method is called at the beginning of driver initialisation. It has access to spark context and plugin context. The method returns a map which will be passed to executor plugin.

### RegisterMetrics Method

{% highlight scala %}

def registerMetrics(appId: String, pluginContext: PluginContext): Unit 

{% endhighlight %}

This method is used for the tracking custom metrics in driver side.

### Receive Method

{% highlight scala %}
def receive(message: scala.Any): AnyRef 
{% endhighlight %}

This method is used for receiving RPC messages sent by the executors.


### Shutdown Method

{% highlight scala %}

def shutdown(): Unit

{% endhighlight %}

This method is called when driver getting shutdown.


## ExecutorPlugin Interface

The below are methods exposed in the executor plugin interface.

### Init Method

{% highlight scala %}

 def init(ctx: PluginContext, extraConf: util.Map[String, String]):Unit

{% endhighlight %}

This method is called when an executor is started. **extraConf** are the parameters sent by driver.

### Shutdown Method

{% highlight scala %}

def shutdown(): Unit 

{% endhighlight %}

This method is called when executor shutdown.

## Adding Spark Plugin

We can add our custom spark plugins to a spark session by setting **spark.plugins** configuration on spark session.

{% highlight scala %}

sparkSession.set(""spark.plugins","full package name of plugin")

{% endhighlight %}


## References

[https://issues.apache.org/jira/browse/SPARK-29397](https://issues.apache.org/jira/browse/SPARK-29397).


## Conclusion

Spark plugin framework brings a powerful customization to spark ecosystem. In this post, we discussed about different interfaces provided by the plugin framework.
