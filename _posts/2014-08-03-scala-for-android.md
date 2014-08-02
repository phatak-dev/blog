---           
layout: post
title: "Google, it's time - We want Scala for Android"
date : 2014-08-03
categories: scala google android
---
Apple made a bold move in this year's WWDC 2014 by announcing a new language,Swift,for it's iOS platforms. Finally, Apple recognized that Objective-C has lived its course and its time for new language. 

Now its time for Android to make a move and embrace a modern language for modern post PC devices. Java showing it's age and we need a new language.

##Google and languages
Google has many languages under it's kit for various kinds of projects. The prominent 
one are

  * Dart   
  * Javascript  
  * Go  
 
Other than Javascript, other two are niche languages created by Google. Though Go is getting buzz these days, its not yet ready to take over language like Java. As for Javascript and buzz with HTML5 its still doesn't feel home when you want to build native apps with it. So it's better Google embrace a open language which is well tested and mature rather than going with their own.

Before we go and look at why Scala language may be good fit , let's see what Swift is bringing to the table which is missing in both Java and Objective -C.


##Swift features 

###Managed runtime

 We don't want to go back C time where we need to manage memory ourselves. 
 Swift has managed runtime with garbage collection. As phones are getting faster 
 memory is not an issue anymore. Android had managed runtime from day one so whatever
 language we choose to replace Java, it should be able to run on Dalvik/ART,
 the managed runtime of Android.

###Type inference

 Having a strongly typed language is always plus. But specifying all the types makes
 code more muddier and hard to code. So we want the best of both worlds, compile
 time check as in static languages and ability to skip the type specifications when not needed like in Dynamic languages. Type inference is solution to this. Type inference
 is where compiler says **"Don't worry buddy, I got your back. You code and I will
 figure out types for you"**. It works like Magic. Swift is replacing the old C like type declarations with elegant ruby like ones.
 
###Lambdas 
Functional programming is everywhere. With event based user interfaces like on Web
and mobile having higher order functions is absolutely needed. Whenever I see a inner
class holding the implementation of on click event handler, it feels so wrong. Swift is bringing lambdas to iOS world and Java 8 to servers. Let's have them in Android too.

###Inter portability with existing applications
No one is going to rewrite all the pieces of iOS applications with
swift on day one. There should be an easy way to move from existing code to new code
where developer should be able to reuse the libraries and tools. Swift allows Objective-C code to co-exist with it in same application which allows for gentle migration.

###Ability to run on existing run time (To support older versions of OS)
Having a new shiny language is cool, but not able to run on older OS version is not cool.
Particularly with Android, ability to run on old version of OS is super critical.


*Knowing the things swift bringing to table, the question is can Scala bring these
things to Android without much effort. The answer is resounding yes!!!*

##What is Scala?
[Scala](http://www.scala-lang.org/) is a JVM language created by Martin Odersky. 
He is the person behind Javac and Generics in Java. It's there from 2004 and getting
popular for it's interesting blend of function programming with object oriented programming. Watch [this](https://www.youtube.com/watch?v=ecekSCX3B4Q) video to understand
the philosophy behind the language.


##Why Scala?
The following features of Scala makes language of choice for Android.

###Scala works seamlessly with Java
This is one of the greatest strength of Scala. No need to rewrite every Java/Android library in Scala , as it seamlessly integrates. Calling Java code from Scala
is super easy which means we get all great libraries of Java for free. So integrating
with existing apps is not an issue at all. That's a big win.

###Runs on JVM , so will run on Dalvik/ART
 No need of new runtime as Scala compiles to byte code which means we can convert to
 Dalvik and ART format directly.

###Excellent type inference
Scala compiler is great. It does some magical things to get the type's right without
you every specifying anything about type. Scala has rich type system which allow you to enjoy all great things of static typing without Java's verbosity.

###Great community support
 Scala is used by thousands of developers in wild. It has excellent support in terms of
 IDE both in Eclipse and IDEA. Lot's of libraries have both Scala and Java API which
 allows them to inter-operate nicely.

###Production ready
 Scala is not a new shiny kid on block like Node.js . It's there from 10 years and battle
 tested in lot of applications. Big data applications like Spark, web application
 framework like Play, Distributed messaging platforms like Akka, are written in Scala
 and in production for years. So the language is well battle tested and ready for production.

###It's already used in Android
 As we speak now, Scala is already used as alternative to Java in Android. Few of the project like [Scaloid](https://github.com/pocorall/scaloid), [Macroid](http://macroid.github.io/) allow you to code in Scala . But what we need is more
 deep support for language in Android framework so that coding in Android becomes fun again.

  Thought it's pretty obvious that Scala should be the modern language of Android,given Android team's [understanding](https://www.youtube.com/watch?v=K3meJyiYWFw#t=1566) of Scala, it's going long time before they adopt it.



