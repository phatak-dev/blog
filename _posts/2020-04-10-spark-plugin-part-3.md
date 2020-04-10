---
layout: post
title: "Spark Plugin Framework in 3.0 - Part 3 : Dynamic Stream Configuration using Driver Plugin"
date : 2020-04-10
categories: scala spark spark-three spark-plugin
---
Spark 3.0 brings a new plugin framework to spark. This plugin framework allows users to plugin custom code at the driver and workers. This will allow for advanced monitoring and custom metrics tracking. This set of API's are going to help tune spark better than before.

In this series of posts I will be discussing about the different aspects of plugin framework. This is the third post in the series, where we will discuss about how to implement the dynamic configuration in spark streaming using driver plugin. You can read all the posts in the series [here](/categories/spark-plugin).


## Dynamic Configuration in Spark Streaming

Spark Streaming is a long running application which processes the incoming data. The streaming applications usually start with an initial configuration. But that configuration may change as the time goes. User doesn't want to stop and restart the spark streaming application for these changes. They would like to it to be updated on the fly.

Currently this is implemented by reading configurations from an external service on every micro batch. But this introduces the additional delay in the processing. It will be nice to have asynchronous update to the configuration which can be picked up at the time execution.

This post shows how to implement this using driver plugin of spark plugin framework.


## Defining a Configuration

The below code defines a simple configuration which holds a single value. In real world scenario, this can be complex object like JSON.

{% highlight scala %}

object Configuration {

  private var value = 10

  def getConfig: Int = value

  def changeConfig(newValue : Int):Int = {value = newValue; value}

}

{% endhighlight %}

In above code, there is initial value and then methods to read/write new configuration.


## Defining a Custom Spark Plugin

The below code defines a custom spark plugin 

{% highlight scala %}

class CustomConfigSparkPlugin extends SparkPlugin{
  override def driverPlugin(): DriverPlugin = new CustomConfigDriverPlugin

  override def executorPlugin(): ExecutorPlugin = null
}

{% endhighlight %}

As we need to only change configuration from the driver, we return **null** fro executor plugin.


## Implementing Driver Plugin

This section of the post we will discussing different parts of the driver plugin implementation.


### Implementing Driver Plugin Interface

First step is to implement driver plugin interface.

{% highlight scala %}

class CustomConfigDriverPlugin extends DriverPlugin

{% endhighlight %}

### Overriding Init Method

Once we extend the driver plugin interface, we override init method. 

{% highlight scala %}

override def init(sc: SparkContext, pluginContext: PluginContext): util.Map[String, String] = {
    this.sparkContext =sparkContext

    runningThread = new Thread(){
      override def run(): Unit = {
        new ServerSocketListener()
      }
    }
    runningThread.start()

    super.init(sc, pluginContext)
  }

{% endhighlight %}

In the init method, we run a socket listener in new thread. This runs in new thread so that it doesn't block the driver operations. We start the thread and leave till it shutdown.

### Socket Listener for Configuration Changes

The below code shows a simple socket listener which changes configuration whenever it's contacted. Here we are doing an incremental to configuration changes to keep it simple.

{% highlight scala %}
  class ServerSocketListener {
    var port = 9999
    val listener = new ServerSocket(port)
    while (true) {
      val socket = listener.accept()
      new Thread() {
        override def run(): Unit = {
          val currentValue = Configuration.getConfig
          Configuration.changeConfig(currentValue + 10)
          val response = "HTTP/1.1 200 OK \r\n\r\n "
             +s" the latest configuration is ${Configuration.getConfig}"
          socket.getOutputStream().write(response.getBytes("UTF-8"))
          socket.getOutputStream.flush()
          socket.close()
        }
      }.start()
    }
  }
{% endhighlight %}

### Overriding Shut Down Method

When driver shut downs, we stops the listener

{% highlight scala %}

 override def shutdown(): Unit = {
    runningThread.interrupt()
    System.exit(0)
    super.shutdown()
  }

{% endhighlight %}


## Setting Custom Spark Plugin in Spark Session

The below code is added in main program, to add the spark plugin to spark session

{% highlight scala %}

val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .set("spark.plugins","com.madhukaraphatak.spark.core
           .plugins.dynamicconfig.CustomConfigSparkPlugin")
      .setAppName("executor plugin example")

{% endhighlight %}

## Using Configuration in Spark Streaming

{% highlight scala %}

val df = sparkSession.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",8888).load().as[String]

val returnDf = df.map(value => value + Configuration.getConfig)

val query = returnDf.writeStream.
  queryName("something")
  .format("console")
  .outputMode(OutputMode.Append())

{% endhighlight %}

In above code, we are reading the data from a socket. For every data received, we are printing the latest configuration.


## Running Example

This section shows how the changes of configuration is shown.


### Initial Run

When the first message **hello world ** is sent on socket, then you can see the below result.

{% highlight scala %}
+-------------+
|        value|
+-------------+
|hello world10|
+-------------+

{% endhighlight %}


### Updating Configuration

You can update configuration by sending curl request at [http://localhost:9999](http://localhost:9999)

{% highlight sh %}

curl localhost:9999

{% endhighlight %}

It will print 

{% highlight text %}

latest configuration is 20

{% endhighlight %}


### New Configuration in Spark Streaming,

If you send the **hello world** again in the socket, you will get below result

{% highlight text %}
+-------------+
|        value|
+-------------+
|hello world20|
+-------------+

{% endhighlight %}

As you can see that streaming program has latest configuration now.


## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/tree/master/src/main/scala/com/madhukaraphatak/spark/core/plugins/dynamicconfig).


## Conclusion

Spark plugin framework brings a powerful customization to spark ecosystem. In this post, we discussed how to use driver plugin to implement dynamic configuration spark streaming.
