---
layout: post
title: "Scala Magnet Pattern"
date : 2020-02-19
categories: scala 
---
Scala has many advanced type based patterns which helps developers to handle scenarios which are hard to handle in other languages. Magnet pattern is one of those patterns. In this post, I will be discussing about how it can be used for handling type erasure challenges.


## Problem Statement

Let's say we would like to write an overloaded method which completes the futures and return their result. The invocation of function will look as below

{% highlight scala %}

completeFuture( Future {1}) // returns value 1
completeFuture( Future{"hello"}) // return value "hello"

{% endhighlight %}


## Using Method Overloading

One of the way to define above function is to use method overloading. The below code does the same

{% highlight scala %}

def completeFuture(value:Future[String]):String = Await.result(value,Duration.Zero)

def completeFuture(value: Future[Int]):Int =  Await.result(value,Duration.Zero)

{% endhighlight %}

But when you try to compile this you will get below compilation error

{% highlight text %}

completeFuture(_root.scala.concurrent.Future) is already defined in scope 

{% endhighlight %}


## Type Erasure

[Type erasure](http://en.wikipedia.org/wiki/Type_erasure) is feature inherited from Java to Scala. This feature turn above two functions as below

{% highlight scala %}

def completeFuture(value:Future):String = Await.result(value,Duration.Zero)
def completeFuture(value:Future):Int = Await.result(value,Duration.Zero)

{% endhighlight %}

As you can see from above code, both method signature looks exactly same. This make Scala think that method is defined multiple times in same scope.


## Magnet Pattern

As we cannot use the method overload in this scenario, we need to use Scala type machinery to handle the same. This is where magnet pattern comes into picture.

Magnet pattern is a design pattern which use Scala's implicits and dependent types. 

The below sections will guide you about different parts of the pattern.


### Defining a Magnet Trait

A Magnet trait defines the application and result of the type. For our example, the below will be the trait

{% highlight scala %}

sealed trait FutureMagnet {
  type Result

  def apply() : Result
}

{% endhighlight %}

Here
  * Result - Signifies the return value of the magnet. It's a dependent type
  * apply - Signifies the computation.


### Define completeFuture using Magnet

Now the **completeFuture** method will be defined as below

{% highlight scala %}

  def completeFuture(magnet: FutureMagnet):magnet.Result = magnet()

{% endhighlight %}

As you can, depending upon the computation the return value of method will change.


### Implementing Magnet for Int and String

Once the above is defined, then we need to implement the magnet for needed types.

{% highlight scala %}

object FutureMagnet {
    implicit def intFutureCompleter(future:Future[Int]) = new FutureMagnet {
      override type Result = Int

      override def apply(): Result = Await.result(future,Duration.Zero)
    }

    implicit def stringFutureCompleter(future:Future[String]) = new FutureMagnet {
      override type Result = String

      override def apply(): Result = Await.result(future,Duration.Zero)
    }

  }

{% endhighlight %}

As you can see from above, these are defined using implicits.

### Usage

Now we can use the above method as we intended.

{% highlight scala %}

completeFuture( Future {1}) 
completeFuture( Future{"hello"})

{% endhighlight %}


## How Magnet Works?

Magnet pattern works mostly using Scala implicit magic. Whenever we pass a value to Scala method, if Scala compiler doesn't find method with the same signature, then it tries to find an implicit which can convert it to needed type. In our example, when we pass **Future[Int]**, compiler searcher for a implicit which converted it into FutureMagnet.

Using Scala dependent types, we were able to define the different return type depending upon the magnet implementation.

## References

Magnet pattern can be used for other use cases also. You can read about them in this [post](http://spray.io/blog/2012-12-13-the-magnet-pattern/).


## Conclusion

Scala Magnet Pattern helps developers to overcome the limitation of language and provide a seamless interface for the users. This pattern makes use of advanced features like implicits and dependent types
