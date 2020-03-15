---
layout: post
title: "Experiments with Graal VM : Part 2 - Polygot JavaScript Hello World"
date : 2020-03-14
categories: scala graal-vm
---
GraalVM is new open source project by Oracle which is trying to make Java VM an universal VM to run all the different languages. Before Graal, there were already few languages like Scala, Closure which targetted JVM as their runtime. This has been hugely successful for those language. GraalVM takes this idea further and makes it easy to target JVM so that more languages can target JVM and it becomes the defacto VM to run all languages.

GraalVM is around from 2014 as a research project. It's been used in production by Twitter from 2017. But for general public, it became production ready in latter half of 2019.

In this series posts, I will be exploring what GraalVM can bring to JVM ecosystem. This is the second post in the series which starts exploring polygot aspect of graalvm. You can read all the posts in the series [here](/categories/graal-vm)


## Polygot 

One of the main advantages of GraalVM is ability to mix and match multiple languages in same VM. From last post, we seen the all languages running on the Graal go through same compiler. This makes using multiple languages in same VM much smoother. 

In this post, I will be showing how to setup an environment where we can mix Scala with Javascript.


## Dependencies

To run, graalvm and truffle, we need to add below dependecies in our build.sbt. We need to run it on JDK 8


{% highlight scala %}

"org.graalvm.sdk" % "graal-sdk" % "20.0.0",
"org.graalvm.truffle" % "truffle-api" % "20.0.0"

{% endhighlight %}

Here we are adding graal and truffle dependecies.


Since we want to use Javascript, we need to add the dependecy from it's truffle implementation.

{% highlight scala %}

"org.graalvm.js" % "js" % "20.0.0",

{% endhighlight %}.


## JavaScript Hello World

Once all the dependecies are done, we are ready to write our first polygot example. As programming tradition, we will be starting with hello world.

{% highlight scala %}

val polyglot = Context.create()
polyglot.eval("js","print('hello world from javascript')")
 
{% endhighlight %}

In just two lines, we wrote a JS application within Java!!. Let's see it's parts

### Polygot Context

For any language, we need to create a context. This context allows us to configure all the needed properites of that language. Here we are creating a simple context.

### Eval Function

Eval function on context takes a language source code and evals it. It's as simple as that.


Now we have written our first polygot program on Graal.


## Code

You can access complete code on [github]()


## Conclusion

Polygot nature of GraalVM makes it very attractive to mix and match different languages on same VM. In this post we saw to write simple Javascript Hello World using GraalVM polygot API.
