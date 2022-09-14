---
layout: post
title: "Latest Java Features from a Scala Dev Perspective - Part 1: Type Inference"
date : 2022-09-14
categories: scala java latest-java
---

I started my career as a Java developer back in 2011. I  developed most of my code in the 1.7 version of Java. Around 2014, I switched to full-time Scala and have not programmed in Java ever since.

Java used to be a slow-moving language that had started lagging behind the other languages. Scala kind of filled that gap of modern language on a JVM. But in the last few years, it has changed. There is a new version of Java every 6 months and it has been getting new features at a very rapid pace. So I thought it will be interesting to go back to Java now and see how it has evolved compared to Java 7.

So in this series of posts, I will be talking about new features of Java from a Scala developer's point of view. I will be using **Java 17**, the latest LTS version, for these examples.

This is the first post in the series where I will be talking about type inference improvements added to Java. You can find all the posts in the series [here](/categories/latest-java).


_You can also consume this blog as video on YouTube_.

<div class="video-container"> <iframe src="https://www.youtube.com/embed/n-DMIlEY9jQ" frameborder="0" width="560" height="315"></iframe> </div>

## Java and Verbosity

One of the main complaints of any Java developer is its verbosity. This verbosity is very evident in the type declaration.

{% highlight java %}

List<String> list = new ArrayList<String>();

{% endhighlight %}

In the above example, we are telling the type of list as String multiple times which leads to a lot of verbosity.

## Type Inference in Scala

Scala greatly simplifies this verbosity using type inference 

{% highlight scala %}
val list = List[String]()
val value = 10.0

{% endhighlight %}

Here using val, we are telling Scala the type only once or infer from the value. Using RHS, Scala will automatically infer the type of variable. 


## Type Inference in Java

In **Java 10**, java has added a new feature called Local variable type inference, which simplifies the verbosity.

{% highlight java %}

var newList = new ArrayList<String>();

{% endhighlight %}

Now by using, **var** keyword, developer can skip the left hand side type. Here **var** is appropriate compared to **val** of Scala,
as all the variables in Java are mutable by default.

The same type inference can be used for built in value assignments also.

{% highlight java %}

var floatValue = 10.0;
var stringValue = "Hello World";
var thread = new Thread();

{% endhighlight %}

From the above examples, you can see that type inference can be used with various types of values and objects. 

This type of inference is not as powerful as Scala as these are limited to a local variable with initialisation. These are not supported for things like method/function return types which is supported in Scala. Still, this greatly reduces the verbosity of Java code and improves readability. 


## Code

The complete code for the above examples can be found on Github, in the below links.

[Type Inference in Java on Github](https://github.com/phatak-dev/latest-java/blob/master/src/main/java/com/madhu/TypeInferenceExample.java)

[Type Inference in Scala Github](https://github.com/phatak-dev/latest-java/blob/master/src/main/scala/com/madhu/TypeInference.scala)


## References

[Local Variable Type inference JEP](https://openjdk.org/jeps/286).

## Conclusion

In this post, we looked at how Java compiler can now do local variable type inference which reduces the verbosity of code. It is very similar to Scala type inference.
