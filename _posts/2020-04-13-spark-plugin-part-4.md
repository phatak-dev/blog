---
layout: post
title: "Spark Plugin Framework in 3.0 - Part 4 : Custom Metrics"
date : 2020-04-13
categories: scala spark spark-three spark-plugin
---
Spark 3.0 brings a new plugin framework to spark. This plugin framework allows users to plugin custom code at the driver and workers. This will allow for advanced monitoring and custom metrics tracking. This set of API's are going to help tune spark better than before.

In this series of posts I will be discussing about the different aspects of plugin framework. This is the fourth post in the series, where we will discuss about how to implement the customer metrics. You can read all the posts in the series [here](/categories/spark-plugin).


## Need For Custom Metrics

Spark exposes wide variety of metrics for external consumption. These metrics include things like resource usage, scheduling delay, executor time etc. These metrics can be consumed using wide variety of sinks for further analysis. You can read about built-in metrics [here](https://spark.apache.org/docs/latest/monitoring.html#executor-task-metrics).

Even though these metrics are very useful, there are cases where you want to track your own metrics. These metrics may be amount of time a given condition is met or may be amount of data written to specific source. This kind of custom application specific metrics allow developers to optimise things specific to their applications.

Till spark 2.x, developers needed to build their own infrastructure to track these custom metrics. They were not able to reuse the spark metrics infrastructure for the custom metrics. But in 3.0 it's going to change.


## Custom Metrics Support in 3.0

Spark added supported for tracking custom metrics using plugin framework from 3.0. Using custom plugin, we can track our own metrics and plug it into the spark metrics system.

The rest of post talks about how to define and consume custom metrics 

## Even Number Custom Metrics

Let's say we have a dataframe which contains number from 0 to 5000. We can create Dataframe as below.

{% highlight scala %}
 val df = sparkSession.range(5000).repartition(5)

{% endhighlight %}

As operation we want to increment the each of the value by 1. In doing so, we also like to keep track how many even numbers are processed by the each executor. This tracking will be our custom metrics called **even number metrics**.


## Custom Executor Plugin for Custom Metrics

To track the above metrics, we need to run code in each executor spark spawns. Executor plugin will be helpful here.

The below are the steps for the same.

### Defining a Custom Spark Plugin

In below code we define the custom spark plugin.

{% highlight scala %}

class CustomMetricSparkPlugin extends SparkPlugin

{% endhighlight %}

### Return Empty Driver Plugin

For this use case, we don't need a driver plugin so we return null for the same.

{% highlight scala %}

  override def driverPlugin(): DriverPlugin = null

{% endhighlight %}

### Define Singleton Atomic Value to Track Metric

We use a simple singleton in plugin code to track the latest value of the metric

{% highlight scala %}

object CustomMetricSparkPlugin {
  val value = new Counter
}

{% endhighlight %}

We define a counter which keeps our metric latest value. Counter is one of kind of metric type supported by spark. There are others also. You can read more about there [here](https://metrics.dropwizard.io/3.1.0/getting-started/).

There will one copy of this plugin for each executor spark runs. 

### Define Executor Plugin

The below code defines a executor plugin and sets up custom metrics

{% highlight scala %}

override def executorPlugin(): ExecutorPlugin = new ExecutorPlugin {
   override def init(ctx: PluginContext, extraConf: util.Map[String, String]): Unit = {
      val metricRegistry = ctx.metricRegistry()
      metricRegistry.register("evenMetrics",CustomMetricSparkPlugin.value)
   }
  }

{% endhighlight %} 

In above code, we register a metrics using **register** method on MetricRegistery. We give a name called **evenMetrics**. The value of metric will take from the counter defined above. This counter will be polled for every 1s by default.


## Using Custom Metrics From Code

Once we setup the executor plugin and the metrics, we need to update the same from our code.

{% highlight scala %}

val incrementedDf = df.mapPartitions(iterator => {
      var evenCount = 0
      val incrementedIterator = iterator.toList.map(value => {
        if(value % 2 == 0) evenCount = evenCount +1
        value +1
      }).toIterator
      CustomMetricSparkPlugin.value.inc(evenCount)
      incrementedIterator
    })

{% endhighlight %}

In above code, we are running our increment operation and also updating the metric value using **inc** operator. Also note that, we are going over data only once for both operation and metric calculation.


## Setting Up Sink

To consume the metrics in spark, we need to specify it's settings in **metrics.properties** file. The below code shows sample of the same.

{% highlight txt %}

*.sink.console.class=org.apache.spark.metrics.sink.ConsoleSink

{% endhighlight %}

In above line, we specify our sink as console which means we want to print the metrics to console.


## Passing Metrics File in Spark Session

We need to specify the path of metrics files using **spark.metrics.conf** property. We can set the property as below

{% highlight scala %}

.set("spark.metrics.conf","src/main/resources/metrics.properties")

{% endhighlight %}


## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/tree/master/src/main/scala/com/madhukaraphatak/spark/core/plugins/custommetrics).

## Output

When you run the above example, you will observe the custom metrics with other spark metrics like as below

{% highlight text %}

plugin.com.madhukaraphatak.spark.core.plugins.custommetrics
                       .CustomMetricSparkPlugin.evenMetrics
             count = 2500

{% endhighlight %}

As we are running on local, there is only one executor. That's why all the even numbers are coming to single executor. But if you run the same code on cluster, you will see different numbers in different executors.

Now our custom metric is flowing as part of the spark metrics system.

## References

[https://issues.apache.org/jira/browse/SPARK-24918](https://issues.apache.org/jira/browse/SPARK-24918)

[https://github.com/cerndb/SparkPlugins](https://github.com/cerndb/SparkPlugins)

## Conclusion

Spark plugin framework brings a powerful customization to spark ecosystem. In this post, we discussed how to use executor plugin to implement custom metrics for our programs.
