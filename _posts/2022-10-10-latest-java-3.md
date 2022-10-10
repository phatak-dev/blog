---
layout: post
title: "Latest Java Features from a Scala Dev Perspective - Part 3: Functional Interfaces"
date : 2022-10-10
categories: scala java latest-java
---
I started my career as a Java developer back in 2011. I  developed most of my code in the 1.7 version of Java. Around 2014, I switched to full-time Scala and have not programmed in Java ever since.

Java used to be a slow-moving language that had started lagging behind the other languages. Scala kind of filled that gap of modern language on a JVM. But in the last few years, it has changed. There is a new version of Java every 6 months and it has been getting new features at a very rapid pace. So I thought it will be interesting to go back to Java now and see how it has evolved compared to Java 7.

So in this series of posts, I will be talking about new features of Java from a Scala developer's point of view. I will be using **Java 17**, the latest LTS version, for these examples.

This is the third post in the series where I will be talking about the functional interfaces. You can find all the posts in the series [here](/categories/latest-java).


## Type of a Function

In a static typed language, each language expression should have a type. In Java, it can be simple types like int, long or classes like String.

So what about type of a lambda expression? A lambda expression is an anonymous function which holds definition of the function. This is an important aspect to understand, to know how Java integrated the functional programming this late into language and still has backward compatibility. 

## Type of a Function in Scala

Before we look into Java, let's see how Scala allows to define the type of the function. The below code is example of the same

{% highlight scala %}

val simpleFunction: (Int) => Int = (x:Int) => x+10
println(simpleFunction(10))

{%endhighlight%}

In above code, we have declared **simpleFunction** variable which of the type of function. The type **(Int)=>Int** is type of the variable of function which accepts an Int and return Int. This kind of types is called structured types.In this way, Scala allows to specify the type of the function in a straightforward way.

## Functional Interfaces in Java

Java uses a functional interface methodology to specify the type of the function. 

_A Functional Interface is a Java interface with single abstract method_. 

The same example of Scala, now can be written in Java as below 

{% highlight java %}

Function<Integer,Integer> simpleFunction = (Integer x) -> x+10;
System.out.println(simpleFunction.apply(10));

{% endhighlight %}

In this example, **Function** is the interface with single method **apply**. Lambda expression now can be stored into that variable.

## Functional Interface and Backward Compatibility

One of the most important part of Java evolution is, it's always backward compatible. This backward compatibility has made Java very popular and sure bet in enterprises. So when they added functional programming, one of the challenge was how to make maximum use of the existing libraries with new code. Functional interfaces is one of the key part of it.

Because of the functional interface is just an normal interface with one abstract method, we can use lambda expressions with API's which don't know anything about the lambda expressions.

{% highlight java %}

static Integer callFunction(Callable<Integer> callable) throws Exception {
        return callable.call();
}

{%endhighlight %}

The above code is simple method, which accepts a callable. The **Callable** interface is available in Java from 1.4 version.

We can invoke this method as below 

{% highlight java %}

callFunction(()->10);

{%endhighlight%}

As you can see in example, we used a lambda expression to create callable object. Java will do the work of wrapping the lambda expression into an object of callable as Callable qualifies to be a functional interface.

Ability to use lambda expressions with existing interfaces and code, greatly increases it's utility. Functional interfaces is the magic behind it.

## Helper Function Types

The **java.util.function** package has some helper function types that can be used for different use cases.

{% highlight java %}
import java.util.function.Consumer;
Consumer<Integer> noReturnFunction = (Integer x) -> System.out.println(10);
{% endhighlight %}

In above code uses a type **Consumer** which allows to define a function which consumes input but doesn't return anything.

{%highlight java %}

 Supplier<Integer> noInputFunction =  () -> 10;

{% endhighlight %}

**Supplier** is yang of the **Consumer** which produces a value without taking any parameters.

These helper functional interfaces/types makes writing functional code in Java more productive.

## Code


The complete code for the above examples can be found on Github, in the below links.

[Java Functional Interfaces](https://github.com/phatak-dev/latest-java/blob/master/src/main/java/com/madhu/lambdas/FunctionInterfaces.java)

[Scala Function Operators](https://github.com/phatak-dev/latest-java/blob/master/src/main/scala/com/madhu/functional/FunctionalOperators.scala).


## References

[State of Lambda](http://cr.openjdk.java.net/~briangoetz/lambda/lambda-state-4.html).

## Conclusion

In this post, we looked at Java Functional Interfaces which allows defining various functional constructs in Java with backward compatibility.
