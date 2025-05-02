---
layout: post
title: "Rediscovering Implicits in Scala 3 - Part 2: Extension methods"
date : 2025-05-02
categories: scala scala3 rediscover-implicits-scala3
---
Implicits are one of the most advanced features of Scala. This feature makes many of the meta programming possible in language. 

The same power of implicits also brings lot of complexity and confusion to beginners. This complexity often leads to the [wrong use of the feature or scares away developers using it altogether](https://docs.scala-lang.org/scala3/reference/contextual/index.html#Critique%20of%20the%20Status%20Quo). 

In Scala 3, the major new version of Scala Language, many features are added to simplify the implicits. These simplifications help the user of the language to clearly understand the different mechanisms available to abstract over different contexts in the code. These simplifications make it easier for beginners to grasp the power of implicits and also make it cleaner code for experts.

In this series of blogs, I will be exploring these new features as if I am rediscovering the implicits all over again. In this second post, I will be discussing about extension methods. You can find all the post in this series [here](/categories/rediscover-implicits-scala3).

## Extension Methods in Scala 2.x

In Scala 2.x, extension methods are commonly used to add new methods to existing types without modifying their original definitions. 
This helps in enriching libraries and creating more fluent APIs without needing direct access to the original source code.

The below code shows how to define an extension method for a simple `Circle` class in Scala 2.x.

{% highlight scala %}
class Extensions(circle: Circle) {
  def circumference: Double = circle.radius * math.Pi * 2
}
object Extensions {
implicit def addExtension(c: Circle): Extensions = new Extensions(c)
}
    
import Extensions._

val circle = Circle(0,0,50)
println(circle.circumference)
{% endhighlight %}

In the above code, we first create a wrapper class `Extensions` which adds a new method `circumference` to the `Circle` class. Then we define an implicit conversion method `addExtension` to automatically convert a `Circle` object to `Extensions` class. So when we call `circumference` method on `Circle`, the Scala compiler automatically inserts the conversion. The `import Extensions._` helps the Scala compiler to find right implicit.


## Extension Methods in Scala 3.x

In Scala 3, extension methods are made a first-class language feature. This provides a cleaner and more intuitive syntax to add methods to existing types without relying on implicit conversions or wrapper classes. The new syntax makes the intention of extension much more explicit and easy to understand.

The same example we discussed in Scala 2.x can now be rewritten in Scala 3.x as shown below.

{% highlight scala %}
extension (circle: Circle) {
  def circumferenceExtension: Double = circle.radius * math.Pi * 2
  }

  val secondCircle = Circle(0, 0, 50)
  println(secondCircle.circumferenceExtension)
{% endhighlight %}

In the above code, we define an extension directly on `Circle` using the `extension` keyword. Inside the extension block, we add the method `circumferenceExtension` which can be called directly on `Circle` instances. There is no need to create an explicit wrapper class or define implicit conversions, making the code much cleaner and expressive.


## Code

You can find complete code over [github](https://raw.githubusercontent.com/phatak-dev/scala-3-examples/refs/heads/master/src/main/scala/ExtensionMethods.scala).

## References

[https://docs.scala-lang.org/scala3/reference/contextual/extension-methods.html](https://docs.scala-lang.org/scala3/reference/contextual/extension-methods.html).

## Summary

In this post, we explored how extension methods were used in Scala 2.x using implicit conversions and how Scala 3 introduced a much cleaner and more expressive syntax for the same. By making extension methods a first-class citizen, Scala 3 greatly simplifies the way we can enrich existing types.
