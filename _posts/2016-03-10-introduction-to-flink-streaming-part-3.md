---
layout: post
title: "Introduction to Flink Streaming - Part 3 : Running Streaming Applications in Flink Local Mode"
date : 2016-03-10
categories: scala flink flink-streaming
---
In the last two blogs of our [series](/categories/flink-streaming/), we discussed about how to use flink streaming API's to do word count. In those blogs we ran our examples from IDE. IDE is a good way to start learning any API. But if we want to understand how code executes in distributed setting it will be good to run it outside of IDE.

In this third blog, I will be discussing about how to run flink streaming examples in local mode, which is a good starting point to understand distributed nature of flink. Access other blog in the series [here](/categories/flink-streaming/).


## Modes of execution in Flink
As with any modern big data frameworks, flink allows user to run the code on different cluster management systems. No change in code is required to run on these different systems. This makes it very easy to change the cluster management system depending on the use case.

The different modes of execution supported in flink are

* ###Local Mode 
Local mode is a pseudo distributed mode which runs all the daemons in the single jvm. It uses AKKA framework for parallel processing which underneath uses multiple threads.

* ### Standalone Cluster Mode 
 
In this setup, different daemons runs on different jvms on a single machine or multiple machines. This mode often used when we want to run only Flink in our infrastructure.

* ### YARN  
 
This mode makes flink run on YARN cluster management. This mode often used when we want to run flink on our existing hadoop clusters. 

Though standalone/yarn is suitable for production, local mode is often good start point to understand the distributed model of flink streaming. So in this blog we will be setting up flink in local mode and run our word count example.

## Setting up local mode

The following are the steps for setting up flink in local mode.

 * ### Step 1 : Download Flink 1.0 distribution
 
 You can download binary distribution of flink [here](http://flink.apache.org/downloads.html). As of now latest version is 1.0. As our examples are compiled against scala 2.10, choose one with that scala version. I chose hadoop 2.6 version for my examples. You can choose the one which matches your hadoop version if you have already any hadoop setup on your machine. If you don't have any set up choose 2.6.

 * ### Step 2 : Extract downloaded file
 
 {% highlight sh %}
   tar -zxvf flink-1.0.0-bin-hadoop26-scala_2.10.tgz 
 {%endhighlight%}

* ### Step 3 : Start Flink in local mode

The below command starts the flink in localmode. 
 {% highlight sh %}
   bin/start-local.sh
 {%endhighlight%}

Once it started successfully, you can access web UI at [http://localhost:8081](http://localhost:8081).

## Packaging code

Once we have the local flink running, we need to package our code as the jar in order to run. Use the below sbt command to create jar.

{% highlight sh %}
   sbt clean package
 {%endhighlight%}

Once the build is successful, you will get *flink-examples_2.10-1.0.jar* under directory *target/scala-2.10*.

## Running wordcount in local mode

Once we have packaged jar, now we can run it outside IDE. Run the below command from flink directory. Replace the path to jar with absolute path to the jar generated in last step.

{% highlight sh %}
 bin/flink run -c com.madhukaraphatak.flink.streaming.examples.StreamingWordCount <path-to-flink-examples_2.10-1.0.jar>
{%endhighlight%}

The above command uses *flink* command to run the example. *flink*  is a command used for interact with jobs. The *run* sub command is used for submit jobs. *-c* option indicates the jar to be added to classpath. Next two parameters are main class and the jar path.

Once you run the above command, wordcount start running in the local mode. You can see this running job [here](http://localhost:8081/#/running-jobs). As you enter the lines in socket you can observe output [here](http://localhost:8081/#/jobmanager/stdout).

Now we have successfully ran our example outside IDE in flink local mode.

## Compared to Spark

This section is only applicable to you, if you have done spark streaming before. If you are not familiar with Apache Spark feel free to skip it.

The local mode of flink, is similar to spark local mode. *flink* command is similar to *spark-submit*. One of the difference is, in spark you don't need to start web ui daemon as spark itself starts it when you create spark context.

## References

Flink Local Setup - [https://ci.apache.org/projects/flink/flink-docs-master/setup/local_setup.html](https://ci.apache.org/projects/flink/flink-docs-master/setup/local_setup.html).