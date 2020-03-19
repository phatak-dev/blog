---
layout: post
title: "Writing Apache Spark Programs in JavaScript"
date : 2020-03-19
categories: scala spark graal-vm javascript
---

Apache Spark supports programming in multiple languages like Scala, Java, Python and R. This multi-language support has made spark widely accessible for variety of users and use cases.

Not all the languages supported by Spark have equal API support. Scala and Java supports complete user facing and library development API's. Python and R are more restricted for user facing API's only. This discrepancy exist as adding support for new API in a language is lot of work. So the only essential API's are ported to all languages.

What if we want to add support to new language for spark? It will be a lot of work in traditional approach. But with GraalVM we can have access to complete set of spark library in completely new language with minimum effort. 

## GraalVM

GraalVM is a polyglot VM which allows user to run multiple languages on same VM. Not only it supports multiple languages, it allows user to bring the libraries from different languages to single platform. You can read more about graalvm [here](/graal-vm-part-1).

One of the fascinating part of GraalVM is ability use Java libraries from any other supported languages. JavaScript is first class citizen on GraalVM with Node.js support. That made me thinking, what if I am able to use that to run spark on Node.js. If I am able to do the same, then I essentially have a JavaScript API for Apache Spark.

Let's see how we go about it.

## Setup for Running Node.js on GraalVM

This section of the post we discuss how to setup the Node.js on GraalVM.


### Download GraalVM Binaries

To run Node.js programs on GraalVM, we need to download the graalvm binaries. You can download the appropriate one from below link

[https://www.graalvm.org/downloads/](https://www.graalvm.org/downloads)

### Start Node.js Interpreter

Once you downloaded the graalvm, you can start the Node.js interpreter using below command

{% highlight sh %}

bin/node --jvm

{% endhighlight %}

The **--jvm** option says that we want to run on JVM mode. If we don't specify the mode, it will run in native mode which is more optimised but doesn't have polyglot features.

Once you run above command  you should show the below output

{% highlight text %}

Welcome to Node.js v12.15.0.
Type ".help" for more information.
>

{% endhighlight %}


### Run Sample Node Code

Once you have Node interpreter, you can run hello world code to see, are you really running a Node.js environment.


{% highlight js %}

console.log("Hello World");

{% endhighlight %}

It will output 

{% highlight text %}

hello world 

{% endhighlight %}

Now we are running Node.js on JVM.


## Setting Up Spark for Node.js environment

Once we have setup the Node.js environment, we need to setup the Spark environment for the same. This section of the document talks about the various steps.


### Download Spark Binary 

We need to download Spark Binary from below link and setup it's path as SPARK_HOME

[https://spark.apache.org/downloads.html](https://spark.apache.org/downloads.html)

You can check is SPARK_HOME is set or not using below command

{% highlight sh %}

echo $SPARK_HOME

{% endhighlight %}


### Adding all the Spark JARS to the classpath

For accessing Spark from Node.js, we need to add all it's jars to JVM classpath. Currently GraalVM doesn't allow us to add a directory to it's classpath. So we will use below shell script to generate a string which will have all the jars in spark binary.

{% highlight sh %}

CLASSPATH=$(find "$SPARK_HOME/jars/" -name '*.jar' | xargs echo | tr ' ' ':')

{% endhighlight %}

The above command generates a string with all the jars and stores it in **CLASSPATH** environment variable


### Passing Classpath to Node.js

Once the CLASSPATH variable is ready, we can pass the classpath to GraalVM as below

{% highlight sh %}

bin/node  --jvm --vm.cp $CLASSPATH 

{% endhighlight %}


Now we have environment ready for the spark.

## Spark Programming In JavaScript

This section of the blog will discuss about how to write spark programs in JavaScript.

### Loading SparkSession Class

First step of any spark program is to create a spark session.

But before creating spark session, we need to import the class. In GraalVM this means we need to make the class available to JavaScript. The below code does the same.

{% highlight js %}
var sparkSessionType = Java.type("org.apache.spark.sql.SparkSession")
{% endhighlight %}

In above code, we are using **Java.type** API to import a given Java class into JavaScript.


### Creating SparkSession

Once the spark session is imported, now we can create the spark session using below code.

{% highlight js %}
var sparkSession = sparkSessionType.builder().master("local[*]").appName("example").getOrCreate() 
{% endhighlight %}

The above code looks almost exactly like Scala code even though it's written in JavaScript. 

### Loading Data

Once we have created spark session, now we can use it to load the data. Replace the path with a csv from your system.

{% highlight js %}

var data = sparkSession.read().format("csv").option("header","true").load("<path to your csv>")

{% endhighlight %}

Again code looks exactly like Scala. But one thing to note is, the **read**. In Scala, it's a val on **SparkSession** class. But in JavaScript it's treated as a function. **So when we use any Java/Scala library in GraalVM all the public properties become the zero parameter methods in JavaScript**.

### Printing Data

{% highlight js %}

data.show()

{% endhighlight %}

Once the data is loaded, the above method is used to show sample of data. 


### Running the Example

Save above code in a file named **server.js**. Then run the below command

{% highlight sh %}

bin/node  --jvm --vm.cp $CLASSPATH server.js

{% endhighlight %}

Now you can see that spark running inside the Node.js and printing sample of your csv. 

We wrote our first spark program in JavaScript successfully.

## Serving Schema Over Node.js http server

Till now, we have written only spark code. Let's mix it with Node.js code. This shows the real power of the integration. The below code prints the schema of the dataframe when user makes a get request on Node.js


{% highlight js %}

const http = require("http");

http.createServer(function (request, response) {
            response.writeHead(200, {"Content-Type": "text/html"});
                response.end(data.schema().prettyJson());
}).listen(8000, function() { console.log("Graal.js server running at http://127.0.0.1:8000/"); });

{% endhighlight %}

Adding above code to server.js and running it again, will start a web server in **8000** port. When you access the **http://127.0.0.1:8000/** you will see the schema of your dataset.

This shows how we are mixing Node code with spark on same VM.

## Code

You can access complete the code on [github](https://github.com/phatak-dev/GraalVMExperiments/blob/master/server.js).

## References

[https://medium.com/graalvm/using-testcontainers-from-a-node-js-application-3aa2273bf3bb](https://medium.com/graalvm/using-testcontainers-from-a-node-js-application-3aa2273bf3bb)
