---
layout: post
title: "Latest Java Features from a Scala Dev Perspective - Part 5: Java Streams"
date : 2023-01-22
categories: scala java latest-java
---
I started my career as a Java developer back in 2011. I  developed most of my code in the 1.7 version of Java. Around 2014, I switched to full-time Scala and have not programmed in Java ever since.

Java used to be a slow-moving language that had started lagging behind the other languages. Scala kind of filled that gap of modern language on a JVM. But in the last few years, it has changed. There is a new version of Java every 6 months and it has been getting new features at a very rapid pace. So I thought it will be interesting to go back to Java now and see how it has evolved compared to Java 7.

So in this series of posts, I will be talking about new features of Java from a Scala developer's point of view. I will be using **Java 17**, the latest LTS version, for these examples.

This is the fifth post in the series where I will be discussing about the Java streams. You can find all the posts in the series [here](/categories/latest-java).


## Java Collections and Mutability

Java collections is one of earliest API's that was added to Java language. Java built-in collections made the language feature rich and productive compared to other languages of the time like C/C++. They have served language well in last 2 decades.

One of the corner stone idea of the collections is that, they are mutable by default. It's great for memory management and object oriented programming but it's not great if you want to express the collections operations in functional programming way.

## Scala Collections and Functional Operations

Scala by default creates collections that are immutable. For example

{% highlight scala %}

val list = List(10,20,30,40)

{% endhighlight %}

The above list is an immutable list. Once we have the list, we can express the operations like below 

{% highlight scala %}

val result = list.map(value => value+1)

{% endhighlight %}

Here we are looping on the collection and adding 1 to it each value. 

## Java Collection and Looping

Let's say we have a normal java collection like below.

{% highlight java %}
var integerList = new ArrayList<Integer>();
        integerList.add(10);
        integerList.add(5);
        integerList.add(4);
{% endhighlight %}

In normal java, we achieve adding 1 to each value as below 

{% highlight java %}

for(var i = 0; i<integerList.size();i++) {
            integerList.set(i,integerList.get(i)+1);
 }

{% endhighlight %}

The above code is verbose and doesn't use any functional programming features.


## Java Streams

Java Stream is a new API that is added to Java, which creates Stream aka immutable collection from the underlying collection on which functional programming API's are supported. Java Streams makes it possible to use functional higher order functions in the Java now. 

The below are the steps for creating and using a stream.


### Creating a Stream out of existing Collection

{%highlight java %}

var integerStream = integerList.stream();

{% endhighlight %}

**stream()** method on the collection is used to create the stream.

### Higher Ordered Functions on Stream

Once we have stream, we can use the Scala like functional operations on it

{% highlight java %}

var mappedStream = integerStream.map(value -> value+1);

{% endhighlight %}

Here we use *map* higher order function to map on the list. There are other operations which are similar to Scala collection API.


## Scala Collection vs Java Streams

Even though Java Streams look and behave like Scala collection, there are few difference which developer need to be aware of. The below are the important ones.

### Java Stream Operations are Lazy

In Scala, all operations are immediate and creates new collections. But in Java streams, all the operations are lazy and they only compute when an operations like **reduce** or **toList** are called. This laziness is similar to Spark RDD's. Laziness helps in performance.

The below is an example same 

{% highlight java %}
var mappedStream = integerList.stream().map(value -> value+1);
var result = mappedStream.toList();
{% endhighlight %}

Here we are calling **toList** explicitly and that triggers the **map** operation.

### Java Streams can be Consumed Only Once

Once a stream is created and consumed , i.e converted to output using reduce or list it cannot be reused. 

For example 

{% highlight scala %}

var integerStream2 = integerList.stream();
var sumResult = integerStream2.reduce((a,b) -> a+b).get();

{% endhighlight %}

In above example, we created a stream and consumed it using reduce. Now if we try to calculate sum again

{% highlight java %}
var anotherSum = integerStream2.reduce((a,b)-> a+b).get();
{% endhighlight %}

We will get below error

{% highlight text %}
"stream has already been operated upon or closed"
{% endhighlight %}

## Code

The complete code for the above examples can be found on Github, in the below links.

[Java Higher Ordered Functions](https://github.com/phatak-dev/latest-java/blob/master/src/main/java/com/madhu/lambdas/StreamExample.java).

[Scala Higher Ordered Functions](https://github.com/phatak-dev/latest-java/blob/master/src/main/scala/com/madhu/functional/CollectionsExample.scala).

## Conclusion

In this post, we looked at how we can write functional collection code using Java Streams. This makes Java collection more powerful like Scala ones.
