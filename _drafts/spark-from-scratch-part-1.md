---           
layout: post
title: "Spark from Scratch - Part 1"
categories: spark spark-from-scratch 
---
This are series of blog posts, where I am putting my experience of learning Apache 
spark from its source code. 

##Why?

I have used spark over a year now and really like its simplicity. But I want to understand 
it from the low level. Understanding big data software is not easy as they have lakhs of line of
source code. But spark is different. It has elegant small core on which most of the functionality
is built. In one of the talks, Matei Zaharia, core developer of spark, mentioned that initial
release, 0.1-alpha had only 1600 lines scala code which is highly encouraging.

So the idea will be to start exploring spark from 0.1-alpha release and built my knowledge
with subsequent releases. 

##Setup

This is first post in the series. In this post I am going to discuss about how to setup the
environment for exploring spark code. 

Follow the following steps to setup

* ####Clone the source code
		git clone https://github.com/apache/spark.git
* ####Build the master
  export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"  		
  mvn -DskipTests clean package
* ####Checkout 0.1-alpha
  	git checkout -b firstrelease alpha-0.1
 
   
   Now you will be in first release branch.   
   The source code will be in src folder and the classes will be in build folder

* ####Install Scala 2.8

  The spark alpha-0.1 version uses Scala 2.8. So download it from [here](http://www.scala-lang.org/download/2.8.0.final.html).
  Set SCALA_HOME environment variable to point it to Scala 2.8

 * ####Build

   Run
    	make 
   to build. Also run
    	make jar
   to generate two jar files in build folder. One is Spark.jar which contains all the compiled
   spark code and another spark-dep.jar which contains dependencies.  

If you are able to run all above steps then you are ready to explore the source code.




