---
layout: post
title: "Experiments with GraalVM - Part 4 : JavaScript Object to Case Class"
date : 2020-03-17
categories: scala graal-vm
---
GraalVM is a new open source project by Oracle which is trying to make Java VM an universal VM to run all the major languages. Before GraalVM, there were already few languages like Scala, Closure which targeted JVM as their runtime. This has been hugely successful for those languages. GraalVM takes this idea further and makes it easy to target JVM so that many more languages can coexist on JVM.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the fourth post in the series which explores passing complex objects from JavaScript to Scala. You can read all the posts in the series [here](/categories/graal-vm).


## Returning Complex Values

In last post, we saw how to use a function from JavaScript in Scala. In many applications, sharing data between languages is important for interportablity. JavaScript encodes it data as JavaScript object. In Scala, we encode the data as the case classes. In this example, we will see how to convert a JavaScript object to case class.


## Return JavaScript Object from Code

The below code returns a JavaScript object after it's evaluation

{% highlight scala %}

val context = Context.create()
val result = context.eval("js","({ 'name':'John', 'age':20})")

{% endhighlight %}

In this code, result will have the JavaScript object.


## Define Scala Case Class

The below code will define a Scala case class.

{% highlight scala %}

case class Person(name:String, age:Int)

{% endhighlight %}


## Converting JavaScript Object to Case Class

The below code converts the result to case class object.

{% highlight scala %}

 val person = Person(result.getMember("name").asString(), result.getMember("age").asInt())

{% endhighlight %}

The above code uses, **getMember** method available on **Value** object to read from the result returned by JavaScript. It acts as a getter method. Using this we can read the result and fill the same to our case class.

## Code


You can access complete code on [github](https://github.com/phatak-dev/GraalVMExperiments/blob/master/src/main/scala/com/madhukaraphatak/graalvm/CaseClassFromJson.scala).


## Conclusion

Polyglot nature of GraalVM makes it very attractive to mix and match different languages on same VM. Sharing data between the languages without any overhead allows user to use capabilities of both the languages natively. 
