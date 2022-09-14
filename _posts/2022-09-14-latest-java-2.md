---
layout: post
title: "Latest Java Features from a Scala Dev Perspective - Part 2: Lambda Expressions"
date : 2022-09-14
categories: scala java latest-java
---
I started my career as a Java developer back in 2011. I  developed most of my code in the 1.7 version of Java. Around 2014, I switched to full-time Scala and have not programmed in Java ever since.

Java used to be a slow-moving language that had started lagging behind the other languages. Scala kind of filled that gap of modern language on a JVM. But in the last few years, it has changed. There is a new version of Java every 6 months and it has been getting new features at a very rapid pace. So I thought it will be interesting to go back to Java now and see how it has evolved compared to Java 7.

So in this series of posts, I will be talking about new features of Java from a Scala developer's point of view. I will be using **Java 17**, the latest LTS version, for these examples.


This is the second post in the series where I will be talking about the lambda expressions. You can find all the posts in the series [here](/categories/latest-java).

## Function Passing in Java 7 using Anonymous Classes

Many developers think that before Java 8 they cannot implement function passing in Java. But it's not true. Using anonymous classes, we can mimic the function passing in Java 7.

The below are the steps involved in the same.

### Define a Function Interface

First, we define an interface that represents a simple function

{% highlight java %}

interface Function<T,U> {
   U apply(T input);
}

{% endhighlight %}

The above is an interface that takes a single input of type T and returns a type U. It has a single method apply.

### A method taking Function as input

{% highlight java %}

static Integer increment(Integer value, Function<Integer,Integer> incrementFunction) {
        return incrementFunction.apply(value);
}

{% endhighlight %}

The above code, shows a simple method that increments a given value using **incrementFunction**. In this method is taking
function as the input.

### Implement the Interface for Creating Increment Function

Now we can use this in our example like below 

{% highlight java %}
var value = 10;
var result = increment(value, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input+1;
            }
});

{% endhighlight %}

In the above code, we called  **increment** method with value **10**. For incrementing, we created an anonymous class that implements this function. This acts as a passing function.

Even though this whole code gives us the function passing, the whole syntax of anonymous class is too verbose.

## Function Passing in Scala

The same example can be written in Scala as below.

{% highlight scala %}

  def increment(value:Int,incrementFunction:(Int)=>Int) = {
    incrementFunction(value)
  }
  val value = 10
  val result = increment(value, v=>v+10)

{% endhighlight %}

In this example, rather than using an interface, we use first class function as method argument. The we use **()=>{}** expression to create function to pass to the increment function.  

The same now can be achieved in Java using lambda expressions.

## Lambda Expressions in Java 8

Lambda expression is a major feature that is released as part of **Java 8**. This allows us to create function expressions in Java with simplified syntax.

The same above code can be return as below 

{% highlight java %}

var result2 = increment(value, (x) -> x+1);

{% endhighlight %}

This is much more cleaner than earlier one. The syntax of **() -> {}** is a shortcut for function definition. This is called lambda expression.

You may be wondering how Java example is working without changing the method argument to a function from interface. This is done using functional interfaces. We will discuss the same in the next post.

## Code

The complete code for the above examples can be found on Github, in the below links.

[Java Lambda Expression](https://github.com/phatak-dev/latest-java/blob/master/src/main/java/com/madhu/lambdas/LambdaExpression.java)

[Scala Function Passing](https://github.com/phatak-dev/latest-java/blob/master/src/main/scala/com/madhu/functional/FunctionPassing.scala).


## References

[Lambda Expressions JEP](https://openjdk.org/jeps/126).

## Conclusion

In this post, we looked at Java lambda expressions which simplifies the function creation in Java.
