---
layout: post
title: "Latest Java Features from a Scala Dev Perspective - Part 4: Higher Order Functions"
date : 2022-10-17
categories: scala java latest-java
---
I started my career as a Java developer back in 2011. I  developed most of my code in the 1.7 version of Java. Around 2014, I switched to full-time Scala and have not programmed in Java ever since.

Java used to be a slow-moving language that had started lagging behind the other languages. Scala kind of filled that gap of modern language on a JVM. But in the last few years, it has changed. There is a new version of Java every 6 months and it has been getting new features at a very rapid pace. So I thought it will be interesting to go back to Java now and see how it has evolved compared to Java 7.

So in this series of posts, I will be talking about new features of Java from a Scala developer's point of view. I will be using **Java 17**, the latest LTS version, for these examples.

This is the fourth post in the series where I will be talking about the higher order functions. You can find all the posts in the series [here](/categories/latest-java).


## Higher Ordered Functions

A higher order function is a function, that can take another function as input or return a function. Higher order functions are corner stone of functional programming in any language. 

In this post, we will discuss how Java lambda expressions can be used in higher order functions context.

## Function as Input in Scala

{% highlight scala %}
def inputFormatter(value:String, fmt:(String) => String):String= {
    fmt(value)
}
{% endhighlight %}

In above code, we define a function **inputFormatter** which takes another function *fmt* as input. Here **inputFormatter** becomes higher order function.

The above function can be invoked as below 

{% highlight scala %}

val formattedValue = inputFormatter("hello world" ,(value) => "the formatted value is :" + value)

{% endhighlight %}


## Function as Input in Java

Same example now can be written in Java as below 

{% highlight java %}

static String inputFormatter(String data, Function<String,String> fmt) {

    return fmt.apply(data);
}

{% endhighlight %}

In above code, we are using **Function** type that is discussed in earlier posts. 

Now above higher ordered function can be invoked as below using lambda expression.

{% highlight java %}

var formattedString = inputFormatter("hello world", (value) -> "the formatted value is :"+value);

{% endhighlight %}


## Function as Return Type in Scala

Next aspect of higher ordered functions we are going to see is, how to return a function from another function.

{% highlight scala %}

def delayedSupplier(data:String) = {
    () => "length of the string is " + data.length
}

{% endhighlight %}

In above code, higher ordered function **delayedSupplier** returns another function which is used for deferred execution.

Above code can be invoked using below code

{% highlight scala %}

val delayedSupplierRef = delayedSupplier("hello world")
println(delayedSupplierRef())

{% endhighlight %}


## Function as Return Type in Java

Now same example can be written in Java as below.

{% highlight java %}

static Supplier<String> delayedSupplier(String data) {
       return () -> {
            //do some costly work which needs to be run only when needed
            return "length of the string is "+data.length();
        };
}

{% endhighlight %}

Here we are using helper function type **Supplier** to return a function.

The above function can be invoked as below

{% highlight java %}

var delayedSupplier = delayedSupplier("hello world");
System.out.println(delayedSupplier.get());

{% endhighlight %}

## Code

The complete code for the above examples can be found on Github, in the below links.

[Java Higher Ordered Functions](https://github.com/phatak-dev/latest-java/blob/master/src/main/java/com/madhu/lambdas/HighOrderFunctions.java).

[Scala Higher Ordered Functions](https://github.com/phatak-dev/latest-java/blob/master/src/main/scala/com/madhu/functional/HigherOrderFunctions.scala).

## Conclusion

In this post, we looked how we can write higher ordered functions in Java using it's functional programming capabilities. 
