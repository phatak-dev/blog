---
layout: post
title: "Experiments with GraalVM - Part 3 : Invoke JS Functions from JVM"
date : 2020-03-16
categories: scala graal-vm
---
GraalVM is a new open source project by Oracle which is trying to make Java VM an universal VM to run all the major languages. Before GraalVM, there were already few languages like Scala, Closure which targeted JVM as their runtime. This has been hugely successful for those languages. GraalVM takes this idea further and makes it easy to target JVM so that many more languages can coexist on JVM.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the third post in the series which explores calling function defined in JS from Scala. You can read all the posts in the series [here](/categories/graal-vm).


## Using JavaScript Function From Scala

In last post, we saw how to evaluate the JavaScript code from Scala. Just evaluating code is not enough if we want to mix and match different languages. To make use of the both languages we should be able to send and return values between them. In this post we will see how to return function from JS and use it from the Scala.

The below are the steps for achieving the same.

### Return Function from JavaScript Code

The below code returns a function from JS.

{% highlight scala %}

val context = Context.create()

val function =  context.eval("js","x => 'hello '+x")

{% endhighlight %}

The type of **function** variable in Scala is of a type **Value**. This type stands for the return value after evaluating any code snippet. Using this return type, we can invoke the function.

### Execute the code

The below code runs the returned function using **execute** method on Value class.

{% highlight scala %}

println(function.execute("world").asString())

{% endhighlight %}

**asString** method converts the result of execution to Java type.

The result looks as below 

{% highlight text %}

hello world

{% endhighlight %}

By using simple execute function, we were able to communicate between two languages without any overhead. This is the power of GraalVM.

## Code

You can access complete code on [github](https://github.com/phatak-dev/GraalVMExperiments/blob/master/src/main/scala/com/madhukaraphatak/graalvm/CallingFunctions.scala).


## Conclusion

Polyglot nature of GraalVM makes it very attractive to mix and match different languages on same VM. In this post we saw how to invoke a function defined in JS from Scala. This zero overhead interaction between languages makes GraalVM very powerful.
