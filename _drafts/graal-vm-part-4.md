---
layout: post
title: "Experiments with Graal VM : Part 4 - JS Object to Case Class"
date : 2020-03-14
categories: scala graal-vm
---
GraalVM is new open source project by Oracle which is trying to make Java VM an universal VM to run all the different languages. Before Graal, there were already few languages like Scala, Closure which targetted JVM as their runtime. This has been hugely successful for those language. GraalVM takes this idea further and makes it easy to target JVM so that more languages can target JVM and it becomes the defacto VM to run all languages.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the fourth post in the series which explores passing complex objects from JS to Scala. You can read all the posts in the series [here](/categories/graal-vm)


## Returning Complex Values

In last post, we saw how to use a function from JS in Scala. In many applications, sharing data between languages is important for interportablity. Javascript encodes it data as JS object. In Scala, we encode the data as the case classes. In this example, we will see how to convert a JS object to Case class.


## Return JS Object from Code

The below code returns a Javascript object after it's evaluation

{% highlight scala %}

val context = Context.create()
val result = context.eval("js","({ 'name':'John', 'age':20})")

{% endhighlight %}

In this code, result will have the javascript object.


## Define Scala Case Class

The below code will define a Scala case class.

{% highlight scala %}

case class Person(name:String, age:Int)

{% endhighlight %}


## Converting JS Object to Case Class

The below code converts the result to case class object.

{% highlight scala %}

 val person = Person(result.getMember("name").asString(), result.getMember("age").asInt())

{% endhighlight %}

The above code uses, **getMember** method available on **Value** object to read from the result returned by Javascript. It acts as a getter method. Using this we can read the result and fill the same to our case class.

## Code


You can access complete code on [github]()


## Conclusion


Polygot nature of GraalVM makes it very attractive to mix and match different languages on same VM. In this post we saw to write simple Javascript Hello World using GraalVM polygot API.
