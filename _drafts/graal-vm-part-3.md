---
layout: post
title: "Experiments with Graal VM : Part 3 - Invoke JS Functions from JVM"
date : 2020-03-14
categories: scala graal-vm
---
GraalVM is new open source project by Oracle which is trying to make Java VM an universal VM to run all the different languages. Before Graal, there were already few languages like Scala, Closure which targetted JVM as their runtime. This has been hugely successful for those language. GraalVM takes this idea further and makes it easy to target JVM so that more languages can target JVM and it becomes the defacto VM to run all languages.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the third post in the series which explores calling function defined in JS frm Scala. You can read all the posts in the series [here](/categories/graal-vm)


## Invoking JS Functions

In last post, we saw how to evaluate the javascript code from Java. Just evaluating code is not enough if we want to mix and match different languages. To make both languages we should be able to send and return values. In this post we will see how to invoke function defined in JS from the Scala.


## Invoke Javascript Function

This section of the post, how to invoke a function defined in Javascript from Scala.


### Return Function from JavaScript Code

The below code returns a function from Js.

{% highlight scala %}

val context = Context.create()

val function =  context.eval("js","x => 'hello '+x")


{% endhighlight %}

The type of **function** of a type **Value**. This type stands for the return value after evaluating code. Using this return type, we can invoke the function.

### Execute the code

The the below code runs the returned function using **execute** method on Value class.

{% highlight scala %}

println(function.execute("world").asString())

{% endhighlight %}

**asString** method converts the result of execution to Java type.



## Code

You can access complete code on [github]()


## Conclusion


Polygot nature of GraalVM makes it very attractive to mix and match different languages on same VM. In this post we saw to write simple Javascript Hello World using GraalVM polygot API.
