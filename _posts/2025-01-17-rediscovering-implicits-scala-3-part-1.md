---
layout: post
title: "Rediscovering Implicits in Scala 3 - Part 1: Implicit Parameters"
date : 2025-01-17
categories: scala scala3 rediscover-implicits-scala3
---
Implicits are one of the most advanced features of Scala. This feature makes many of the meta programming possible in language. 

The same power of implicits also brings lot of complexity and confusion to beginners. This complexity often leads to the [wrong use of the feature or scares away developers using it altogether](https://docs.scala-lang.org/scala3/reference/contextual/index.html#Critique%20of%20the%20Status%20Quo). 

In Scala 3, the major new version of Scala Language, many features are added to simplify the implicits. These simplifications help the user of the language to clearly understand the different mechanisms available to abstract over different contexts in the code. These simplifications make it easier for beginners to grasp the power of implicits and also make it cleaner code for experts.

In this series of blogs, I will be exploring these new features as if I am rediscovering the implicits all over again. In this first post, I will be discussing about implicit parameters. You can find all the post in this series [here](/categories/rediscover-implicits-scala3).

## Implicit Parameters in Scala 2.x

Implicit parameters is one of the important use case for implicits. In Scala 2.x it can be used as below

{% highlight scala %}

 trait Comparator[T] {
     def compare(x:T,y:T):Int
  }

  def max[T](x:T, y:T)(implicit comparator: Comparator[T]):T ={
     if comparator.compare(x,y) <0 then y else x
 }

{% endhighlight %}

In above code, we define a trait for comparison. In our user function **max**, we define an implicit parameter called **comparator** which is used for comparing.

The above can be invoked as below 

{% highlight scala %}
class IntComparator extends Comparator[Int] {
   override def compare(x: Int, y: Int): Int = x.compare(y)
}
implicit val intComparator = new IntComparator()
println(max(10,20))

{% endhighlight %}

In above code, we create an implementation of the comparator and define an implicit variable to hold the implementation. So when **max** function is called its resolved from the environment.

## Scala 3 Using and Given

In Scala 3, this implicit parameters are made more explicit. These are called given instances. As the name suggests, given an instance of type Scala compiler try to use it appropriately. 

So the same code can be now written as 

{% highlight scala %}

def maxWithUsing[T](x:T,y:T)(using comparator: Comparator[T]):T = {
  if comparator.compare(x,y) <0 then y else x
 }

{%endhighlight %}

In above code, we replaced *implicit* using **using** keyword, which expresses the intention of parameter that we want use from the environment.This is more explicit and clear compared to just implicit.

To use it, we can now use below code

{% highlight scala %}

given intComparatorWithGiven:Comparator[Int] = IntComparator()
println(maxWithUsing(10,20))

{% endhighlight %}

Here we are defining a parameter using **given** keyword, which expresses intention giving or providing the type for the used parameters.

So the combination of **using** and **given** pattern makes it much more clear about the usage than generic implicits.

## Code

You can find complete code over [github](https://github.com/phatak-dev/scala-3-examples/blob/master/src/main/scala/ImplicitParameters.scala).

## References

[https://docs.scala-lang.org/scala3/reference/contextual/givens.html](https://docs.scala-lang.org/scala3/reference/contextual/givens.html).
