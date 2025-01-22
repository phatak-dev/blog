---
layout: post
title: "Rediscovering Implicits in Scala 3 - Part 2 : Extension methods"
date : 2025-01-22
categories: scala scala3 rediscover-implicits-scala3
---
Implicits are one of the most advanced features of Scala. This feature makes many of the meta programming possible in language. 

The same power of implicits also brings lot of complexity and confusion to beginners. This complexity often leads to the [wrong use of the feature or scares away developers using it altogether](https://docs.scala-lang.org/scala3/reference/contextual/index.html#Critique%20of%20the%20Status%20Quo). 

In Scala 3, the major new version of Scala Language, many features are added to simplify the implicits. These simplifications help the user of the language to clearly understand the different mechanisms available to abstract over different contexts in the code. These simplifications make it easier for beginners to grasp the power of implicits and also make it cleaner code for experts.

In this series of blogs, I will be exploring these new features as if I am rediscovering the implicits all over again. In this second post, I will be discussing about extension methods. You can find all the post in this series [here](/categories/rediscover-implicits-scala3).

## Extension methods  in Scala 2.x

Extension methods is way to add new methods to already declared type. In Scala 2.x it can be used as below

{% highlight scala %}

case class Circle(x:Double, y:Double, radius:Double)

class Extensions(circle: Circle) {
      def circumference:Double = circle.radius * math.Pi *2
    }
object Extensions {
      implicit def addExtension(c: Circle):Extensions = new Extensions(c)
}

{% endhighlight %}

In the above code we do the below steps

 * We are defining a type called Circle using case class
 * Let's say we want do add a method called circumference without changing the code. This is useful to extend the types which are not controlled by us. So we can add method to this type using implicits
 * First we define a class called **Extensions** which wraps the **Circle** type and adds a method called circumference.
 * Then, we will be defining a object of same class, which defines a converting method using implicits

Now we have added the extension method to our type. To use

{% highlight scala %}
import Extensions._

val circle = Circle(0,0,50)
println(circle.circumference)

{% endhighlight %}

In above code,we have first import implicit definition using **Extension._**. Then we have can use the method as a native method of the type.

## Complexity of Scala 2.x Approach

From the above code, we can understand that the amount of work to add an extension method is quite a lot. Not only that, its very opaque in nature to understand how exactly its adding a method. So readability of the code suffers here. 

Scala 3.x has better way to achieve the same

## Scala 3 Extension method

In Scala 3, there is a explicit support for the extension method using **extension** keyword.
So the same code can be now written as 

{% highlight scala %}

extension (circle:Circle) {
      def circumferenceExtension: Double = circle.radius * math.Pi * 2
}
{%endhighlight %}

In above code, we defined an extension using **extension** keyword. The type to which extension is added to will be taken as parameter. This is much cleaner than the implicit code we had in 2.x.

To use it, we can now use below code

{% highlight scala %}

val secondCircle = Circle(0, 0, 50)
println(secondCircle.circumferenceExtension)
{% endhighlight %}

The code is same as earlier.

So in Scala 3.x, we can do add extension methods in much cleaner and readable way then Scala 2.x.

## Code

You can find complete code over [github](https://github.com/phatak-dev/scala-3-examples/blob/master/src/main/scala/ExtensionMethods.scala).
## References

[https://docs.scala-lang.org/scala3/book/ca-extension-methods.html)(https://docs.scala-lang.org/scala3/book/ca-extension-methods.html)
