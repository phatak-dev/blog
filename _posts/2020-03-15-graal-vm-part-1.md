---
layout: post
title: "Experiments with GraalVM - Part 1 : Introduction"
date : 2020-03-15
categories: scala graal-vm
---
GraalVM is a new open source project by Oracle which is trying to make Java VM an universal VM to run all the major languages. Before GraalVM, there were already few languages like Scala, Closure which targeted JVM as their runtime. This has been hugely successful for those languages. GraalVM takes this idea further and makes it easy to target JVM so that many more languages can coexist on JVM.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the first post in the series which introduces the GraalVM. You can read all the posts in the series [here](/categories/graal-vm).

## What is GraalVM

From GraalVM [website](https://www.graalvm.org/docs/why-graal/) 

{% highlight text %}

GraalVM offers a comprehensive ecosystem supporting a large set of languages (Java and other JVM-based languages, JavaScript, Ruby, Python, R, WebAssembly, C/C++ and other LLVM-based languages) and running them in different deployment scenarios (OpenJDK, Node.js, Oracle Database, or standalone). 

{% endhighlight %}

They are essentially saying that, it's a ecosystem which has Java VM at it's heart and want to support run wide variety of languages on top of it.


## Why Multiple Languages

Why does some one care about running their languages on JVM? The below are some reasons

### Server Tuned VM

JVM has been battle tested over years on server. This can benefit systems like Node.js, javascript server framework, which currently runs on V8 which is optimised for client environment like browser. Also languages like Ruby already do this. That's why there are lot of companies which run JRuby implementation. GraalVM makes this much easier and supports more languages with less effort.

### Polyglot Support

As we can now run multiple languages on same VM, we can mix and match these languages. This is very powerful. For example, we can now extend capability of a compiled language like Java, with interpreted language like  Javascript. This combination makes it much more powerful than running single language. We will discuss more about this in future posts.

### Libraries of Java Ecosystem

One of the reason Scala adopted JVM is for it's rich ecosystem. Able to run on the JVM makes all the Java libraries available to Scala. Now they are going to be available to all the languages which run on graalVM.


There are many more advantages to GraalVM than listed above. You can read more about it [here](https://www.graalvm.org/docs/why-graal/).

## GraalVM Architecture


![GraalVM Architecture](https://image.slidesharecdn.com/dopoledne-ignite-1-jaroslav-tulach-graalvm-180620154636/95/jaroslav-tulach-graalvm-z-vvoje-nejrychlejho-virtulnho-stroje-na-svt-15-638.jpg)

The above picture shows the architecture of graalvm.

From Above Architecture , the below are the main components

   * Java Hotspot VM : It's heart of the architecture. Everything is powered by Java VM. Supported from Java 8 onwards.

   * Graal Compiler : The Graal Compiler which is responsible for generating the byte code from AST generated from above layers

   * Truffle Framework : A framework which allows defining interpreters for different languages in a AST model. This standard AST model between languages makes it polyglot

## References

[https://stackoverflow.com/questions/54631823/implementing-a-programming-language-on-the-graalvm-architecture](https://stackoverflow.com/questions/54631823/implementing-a-programming-language-on-the-graalvm-architecture)

[https://www.graalvm.org/docs/why-graal/](https://www.graalvm.org/docs/why-graal/)

## Conclusion

GraalVM brings a new era in VM's. It's positioning the JVM as center of all the popular language runtime. This makes JVM ecosystem exciting again.
