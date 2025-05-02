---
layout: post
title: "Rediscovering Implicits in Scala 3 - Part 3: Summoning Implicits"
date : 2025-03-02
categories: scala scala3 rediscover-implicits-scala3
---
Implicits are one of the most advanced features of Scala. This feature makes many of the meta programming possible in language. 

The same power of implicits also brings lot of complexity and confusion to beginners. This complexity often leads to the [wrong use of the feature or scares away developers using it altogether](https://docs.scala-lang.org/scala3/reference/contextual/index.html#Critique%20of%20the%20Status%20Quo). 

In Scala 3, the major new version of Scala Language, many features are added to simplify the implicits. These simplifications help the user of the language to clearly understand the different mechanisms available to abstract over different contexts in the code. These simplifications make it easier for beginners to grasp the power of implicits and also make it cleaner code for experts.

In this series of blogs, I will be exploring these new features as if I am rediscovering the implicits all over again. In this third post, I will be discussing about summoning implicits. You can find all the post in this series [here](/categories/rediscover-implicits-scala3).

## Passing Implicit Parameter to a Method in Scala 2.x

In Scala 2.x, whenever we want to use an implicit value inside a method, we need to define an additional parameter list marked as `implicit`. This parameter list allows the compiler to automatically pass the correct value from the context when the method is called. However, this also means every method that needs an implicit has to explicitly declare it in its signature, which can sometimes add verbosity.

The below code shows an example where we pass an implicit ordering for comparing two integers.

{% highlight scala %}
object SummonExample {
  trait Ord[T] {
    def compare(x: T, y: T): Int
  }

  class IntOrdering extends Ord[Int] {
    override def compare(x: Int, y: Int): Int = x.compare(y)
  }

  def main(args: Array[String]): Unit = {
    given intOrdering: Ord[Int] = new IntOrdering()

    def maxWithImplicitParameter(x: Int, y: Int)(implicit intOrdering: Ord[Int]): Int = {
      if (intOrdering.compare(x, y) < 0) y else x
    }

    println(maxWithImplicitParameter(10, 20))
  }
}
{% endhighlight %}

In the above code, we first define a trait `Ord` for comparison logic and provide an implementation `IntOrdering` for integers. In the `maxWithImplicitParameter` method, we accept an implicit parameter of type `Ord[Int]`. At the call site, the compiler automatically fills in the implicit value `intOrdering`, and the method works without us needing to pass it manually.

## Summon in Scala 3.x

In Scala 3, the `summon` keyword is introduced as a standard way to fetch an implicit value from the surrounding context. Instead of passing an implicit parameter explicitly in every method signature, we can now summon the required context inside the method body itself.This makes the method signatures cleaner and separates the concern of fetching context from defining the method logic.

The same example we discussed earlier can now be improved using `summon` as shown below.

{% highlight scala %}
def maxWithSummon(x: Int, y: Int): Int = {
  // summon the implicit context using surrounding context
  val intOrdering = summon[Ord[Int]]
  if intOrdering.compare(x, y) < 0 then y else x
}

println(maxWithSummon(10, 20))
{% endhighlight %}

In the above code, we no longer have to declare an extra implicit parameter in the method definition. Instead, we directly use `summon[Ord[Int]]` inside the method body to fetch the implicit ordering from the context. This makes the method signature much simpler and keeps the implicit handling localized within the method.


## Code

You can find complete code over [github](https://raw.githubusercontent.com/phatak-dev/scala-3-examples/refs/heads/master/src/main/scala/SummonExample.scala).

## References

[https://docs.scala-lang.org/scala3/reference/contextual/using-clauses.html#summoning-instances-1](https://docs.scala-lang.org/scala3/reference/contextual/using-clauses.html#summoning-instances-1)

## Summary

## Summary

In this post, we explored how Scala 2.x required explicit implicit parameters in method signatures and how Scala 3.x introduced the `summon` keyword to simplify this process. By using `summon`, we can now fetch context parameters directly inside the method body, leading to much cleaner and more readable code.

