---
layout: post
title: "Experiments with Graal VM : Part 1 - Introduction"
date : 2020-03-14
categories: scala graal-vm
---
GraalVM is new open source project by Oracle which is trying to make Java VM an universal VM to run all the different languages. Before Graal, there were already few languages like Scala, Closure which targetted JVM as their runtime. This has been hugely successful for those language. GraalVM takes this idea further and makes it easy to target JVM so that more languages can target JVM and it becomes the defacto VM to run all languages.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the first post in the series which introduces the GraalVM. You can read all the posts in the series [here](/categories/graal-vm)


## What is GraalVM

From GraalVM [website](https://www.graalvm.org/docs/why-graal/) 

{% highlight text %}

GraalVM offers a comprehensive ecosystem supporting a large set of languages (Java and other JVM-based languages, JavaScript, Ruby, Python, R, WebAssembly, C/C++ and other LLVM-based languages) and running them in different deployment scenarios (OpenJDK, Node.js, Oracle Database, or standalone). 

{% endhighlight %}

There are essentially saying it's a ecosystem which has Java VM at it's heart and want to support run wide variety of languages on top of it.


## Why Multiple Lanaguages

Why does some one care about running their languages on JVM? The below are some reasons

### Server Tuned VM

JVM has been battle tested years and years on server. This can benifit systems like Node.js, JS framework, which currently runs on V8 which is optimised for client enviornments. Also languages like Ruby already understand this. That's there are lot of companies run JRuby implementation. GraalVM makes this much easier and supports more languages with less effort


### Polygot Support

As we can now run multiple languages on same VM, we can mix and match these languages. This is very powerful. For example, we can now extend capabality of a compiled language like Java, with intepreted language like  Javascript. This combination makes it much more powerful than running single language .


### Reuse Libraries of Java Ecosystem

One of the reason Scala adopted JVM for it's rich ecosystem . Being run on the JVM makes all the Java libraries available to Scala. Now they are going to be all the languages which run graalVM.


There are many more adavantages you can read more in below article

https://www.graalvm.org/docs/why-graal/


## GraalVM Architecture


![GraalVM Architecture](https://image.slidesharecdn.com/dopoledne-ignite-1-jaroslav-tulach-graalvm-180620154636/95/jaroslav-tulach-graalvm-z-vvoje-nejrychlejho-virtulnho-stroje-na-svt-15-638.jpg)


From Above Architecture , the below are the main components

   * Java Hotspot VM : It's heart of the architecture. Everything is powered by Java VM. Supported from Java 8

   * Graal Compiler : The Graal Compiler which is responsible for generating the byte code from AST generated from above layers

  * Truffle Framework : A framework which defines intepreters for different languages in a AST model. This standard AST model between languages makes it polygot



## References

https://stackoverflow.com/questions/54631823/implementing-a-programming-language-on-the-graalvm-architecture


## Conclusion

GraalVM brings a new era in VM's. It's positioning the JVM as center of all the popular language runtimes.
