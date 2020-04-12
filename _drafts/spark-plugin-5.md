---
layout: post
title: "Spark Plugin Framework in 3.0 - Part 5: RPC Communication"
date : 2020-04-12
categories: scala spark spark-three spark-plugin
---
Spark 3.0 brings a new plugin framework to spark. This plugin framework allows users to plugin custom code at the driver and workers. This will allow for advanced monitoring and custom metrics tracking. This set of API's are going to help tune spark better than before.

In this series of posts I will be discussing about the different aspects of plugin framework. This is the fith post in the series, where we will discuss about rpc communication. You can read all the posts in the series [here](/categories/spark-plugin).


## RPC Communication

Spark plugin framework allows driver and executor plugin to communicate to each using RPC messages. This facility is useful to send status or ask details from the driver for some initialisation of the configuration. 

In this post, we will discussed about how to use the RPC communication of the plugin framework.


## Types of Communication

RPC framework allows two types of communication. One is synchronous, where executor sends a message and waits for response. Another one is asynchronus where the executor follows fire and forget. Currently only executor plugin can initiage the communication.

## Synchronus Message Passing

This section of the post discusses about how the synchronous communication works.

### Define Messages

The below code defines the two messages. One request and one response

{% highlight scala %}

case object InitialConfigRequest extends  Serializable
case class InitialConfigResponse(value:Int) extends Serializable

{% endhighlight %}

All messages need to implement the **Serializable**.

### Sending Message from Executor Plugin

The below code shows how to send a message from executor plugin

{% highlight scala %}

override def executorPlugin(): ExecutorPlugin = new ExecutorPlugin {
    var pluginContext:PluginContext = null
    var initialConfiguration:Int = 0

    override def init(ctx: PluginContext, extraConf: util.Map[String, String]): Unit = {
      pluginContext = ctx
      initialConfiguration = pluginContext.ask(InitialConfigRequest).asInstanceOf[InitialConfigResponse].value
      println("the initial configuration is " + initialConfiguration)
    }

{% endhighlight%}

In above code, we are using **pluginContext.ask** method to send a message. The return value will be stored as the configuration. As the name suggest, **ask** is a synchronous API.

### Handling Messages in Driver Plugin

The below code shows to how to handle these messages in driver plugin

{% highlight scala %}

 override def driverPlugin(): DriverPlugin = new DriverPlugin {
   override def receive(message: scala.Any): AnyRef = {
     message match {
       case InitialConfigRequest => InitialConfigResponse(10)

{% endhighlight %}

In driver plugin, we override **receive** method to recieve the message. Then using pattern matching we respond to different messages. In this code, we are responding to the initial configuration message.


## Asynchronous Messaging

In the last section we looked at how to do synchrnous messaging. In this section, we discuss about how to do the asynchronus ones.

### Send Method

We use **send** method rather than ask in executor plugin to send the message.

{% highlight scala %}

val rpcMessage = FinalValueResponse(10 * initialConfiguration)
pluginContext.send(rpcMessage)

{% endhighlight %}

As we can see from the code, we are not waiting for results.

### Handling Messages in Driver Plugin

Handling messages in driver remains same. Rather than returning value, we return **null** to signify that there is no return value

{% highlight scala %} 

case FinalValueResponse(value)  => println("the final value is "+ value); null

{% endhighlight %}


## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/tree/master/src/main/scala/com/madhukaraphatak/spark/core/plugins/rpccommunication).



## Conclusion

Spark plugin framework brings a powerful customization to spark ecosystem. In this post, we discussed how to do communication between driver and executor plugins. 
