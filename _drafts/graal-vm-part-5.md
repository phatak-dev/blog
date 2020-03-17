---
layout: post
title: "Experiments with GraalVM - Part 5 - Passing Scala Object to JavaScript"
date : 2020-03-18
categories: scala graal-vm
---
GraalVM is a new open source project by Oracle which is trying to make Java VM an universal VM to run all the major languages. Before GraalVM, there were already few languages like Scala, Closure which targeted JVM as their runtime. This has been hugely successful for those languages. GraalVM takes this idea further and makes it easy to target JVM so that many more languages can coexist on JVM.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the fifth post in the series which explores passing complex objects from Scala to JS. You can read all the posts in the series [here](/categories/graal-vm)


## Passing Values From Scala To JavaScript

In last post, we saw how to consume JavaScript object inside the Scala program. In this post, we will be discussing about how to pass Scala objects to Javascript. 


## Enable Allow Access on Context

By default, the guest language, JavaScript in our example, doesn't have access to any objects from host langage, Scala in our case. This is done for security purposes. But we can override this by enabling access on the context level.

The below code shows how to do that

{% highlight scala %}

val context = Context.newBuilder().allowAllAccess(true).build()

{% endhighlight %}

Here we are using builder pattern of context to pass extra parameter. Using **allowAllAccess** method on context, we allow access to host environement. 

## Create a Scala Object

The below code creates a person object in Scala

{% highlight scala %}

case class Person(name:String, age:Int)

val person = Person("John",20)

{% endhighlight %}

## Make Scala Object Available to JavaScript

Not every object created in Scala is available to JavaScript by default. It needs to be explicitely made availble.

{% highlight scala %}

context.getBindings("js").putMember("person",person)

{% endhighlight %}

In above code, using **putMember** method, we make person object available for JavaScript.


## Using Scala Object from JavaScript

The below code show accessing the person object in JavaScript code.

{% highlight scala %}

 val result = context.eval("js","person.name() == 'John' && person.age() == 20").asBoolean()

{% endhighlight %}

As you can see from the code, we can access the person object as a normal JavaScript object.

## Code


You can access complete code on [github](https://github.com/phatak-dev/GraalVMExperiments/blob/master/src/main/scala/com/madhukaraphatak/graalvm/PassClassToJs.scala).


## Conclusion


Polygot nature of GraalVM makes it very attractive to mix and match different languages on same VM. Ability to pass values between different languages makes it seemless to do different computation in different languages. 
