---           
layout: post
title: "sizeof operator for Java&#47;Scala"
date : 2014-12-03
categories: scala spark 
---

As I was going through Apache spark source code, I stumbled upon one interesting tool. Spark has a utility, called [SizeEstimator](https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/util/SizeEstimator.scala), which estimates the size of objects in Java heap. This is very interesting idea and I started to explore use case of tool like this.

## sizeof operator in C/C++
In C/C++ size of operator is used to determine size of given data structure. This is important, as size of data structures in these languages are platform dependent. For example 

{% highlight c++%}
 std::cout << sizeof(int) ;
{% endhighlight %}

the above c++ code will print 2 if it's 16 bit machine and 4 if it's 32 bit machine.

Also these languages does manual memory management. Developer uses sizeof operator to specify how much memory needs to be allocated. 

{% highlight c++%}
int * intArray = (int *)calloc(10*sizeof(int));
{% endhighlight %}

The above code create an array of integers which hold 10 elements. Again as you can see here we used sizeof operator specify exact amount memory we wanted to allocate.

So from above examples, it's apparent that sizeof operator is a tool which helps you to know the size of the variable at runtime.


## sizeof operator for Java
Java do not have any sizeof operator in the language. The following are two reasons for that

* The size of data structure is same on all platforms
* Java virtual machine with garbage collection will do the memory management for you. 

So as a developer, you do not need to worry about the memory management in java. So creators of Java felt there is no use of sizeof operator.

But there are few use cases where we may need a way to measure size of objects at runtime. 

## Use case for sizeof operator
As I told in the beginning, this idea of sizeof operator came from Spark source code. So I started digging why they need this. As it turns out SizeEstimator in Spark is used for building memory bounded caches. The idea is that you want to specify amount of heap memory the cache can use so when it runs out of memory it can use LRU method to accommodate newer keys. 

You can find more use cases in [this](http://www.javaworld.com/article/2077408/core-java/sizeof-for-java.html) article.

## Memory bounded caches in Spark
We use caches in almost every application. Normally most of the in-memory caches are driven by time. As keys get less and less accessed they get evicted to accommodated newer keys. But in few use cases, like in Spark, we need different approach for eviction.

Spark uses caching to cache big data on RAM in cluster of computers. Caching the data on RAM allows for speedier processing which is important when we do processing on TB's of data. Though this looks like a good idea, it comes with issue that cache may take over the complete RAM of the node which makes node useless for any kind of processing.

So spark divides the main memory of a node to two parts. One for caching the data and one for running the computations on the data. Using the SizeEstimator tool they can precisely control usage of RAM.

You can look at one of the implementation of memory bounded caches [here](https://github.com/phatak-dev/java-sizeof/blob/master/examples/src/main/scala/com/madhukaraphatak/sizeof/examples/BoundedMemoryCache.scala).

## java-sizeof library

I extracted the code from the spark, simplified little and published as a independent [library](https://github.com/phatak-dev/java-sizeof). So if you want to analyze size of your objects in your Java/Scala projects, you can use this library. This library is well tested inside the spark. 

## Adding dependency

You can add the library through sbt or maven.

 * Sbt
   {% highlight scala %}
   libraryDependencies += "com.madhu" %% "java-sizeof" % "0.1"
   {% endhighlight %}
 * Maven
 {% highlight xml %}
 <dependency>
 <groupId>com.madhu</groupId>
 <artifactId>java-sizeof_2.11</artifactId>
 <version>0.1</version>
 </dependency>
{% endhighlight %}

## Using

The following code how you can estimate size of the object.

{% highlight java %}
SizeEstimator.estimate('a');
   
List<Integer> values = new ArrayList<Integer>();
values.add(10);
values.add(20);
values.add(30);
SizeEstimator.estimate(values);
{% endhighlight %}

You can find more examples [here](https://github.com/phatak-dev/java-sizeof/tree/master/examples).