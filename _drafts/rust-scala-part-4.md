---
layout: post
title: "Scala Developer Journey into Rust - Part 4: Algebric Data Types"
date : 2019-01-14
categories: rust scala rust-scala
---
Rust is one of the major programming languages that's been getting popular in recent years. It has many advanced high level language features like Scala.This made me interested to learn Rust. So in this next series of blogs I will share my experience with Rust from a Scala developer point of view. I would like to explore how these two language approach things. I would like to explore the similarities and their differences.

This is fourth post in the series. In this post, I will be talking about algebric data types. You can find all the other posts in the series [here](/categories/rust-scala).

## Algebric Data Types

Algebric data types, are the abstractions used to represent composite data types in a program. They are quite popular in functional languages likes Scala, ML etc. These data types helps representing many real world data in very precise manner. Scala as the functional language uses them quite heavily across different part of the language.

One of the good example for ADT is **Option* type in Scala. This data type is composition of two types. One is *None* and another is **Some*. Option is type is used wheereve the value is optional. You can know more about ADT in Scala in [this]() article.

Rust is one of the first system programming language to embrace the ADT's. Rust standard library comes with many ADT's like Option, Result etc. 

In this post, we are going to discuss how a given ADT is represented and used in the Scala and Rust.

## Implementing Option ADT

In this post, we are going to implment minimal **Option** ADT. Even though it's part of base language, implementing it will gives us understanding of how to implement any ADT.

The below are the different parts

### Contract of ADT

ADT may look like a normal data type. It may look like one abstract class with multiple extending child classes. But one big difference between ADT and normal data types are, all the composite part of the types should be known before hand. There should not be an option to create more subclasses by other users, because then it will break the contract of an ADT. Different language achieves this differntly.


### Option ADT in Scala

The below is the code to define an Option ADT in Scala.

{% highlight scala %}
sealed trait Optional[+T]

case object CNone extends Optional[Nothing]

case class CSome[T](value:T) extends Optional[T]

{%endhighlight%}

The names **CNone** and **CSome** is used to avoid the clash with standard library classes.

Here a sealed trait is used because Scala doesn't allow creating subclass of a sealed trait outside of this source file. This makes sure that we have fixed number of sub classes.


### Option ADT in Rust

The below is the code to define Option ADT in Rust

{% highlight scala %}
enum Optional<T> {
 CNone,
 CSome(T)
}
{% endhighlight%}

Rust used enum's for defining ADT's. Rust version is more consise and meaningful. As enum have constraint of defining all of it's components at the time of creating, it makes sure that we preserve the contract of ADT.


### Using Enum for ADT in Scala

One of the interesting development recently happened in Scala, was plan to have a improved enum support in Scala. This is going to be part of Dotty/ Scala 3.0. In this version, enum's are going to be used to represent the ADT's which is very similiar to Rust. You can find more information in this [dotty doc](https://dotty.epfl.ch/docs/reference/enums/adts.html).


## Using Option ADT

ADT's are primarily used with match expressions. One of the important part of match expression with ADT is, you need to handle all the components of ADT when you are matching. If you skip any part you will get a warning or error from compiler. This makes sure that we don't miss accidently some part of expression.

The below sections show how to create and use ADT's in Scala and Rust.


### Using Option ADT in Scala

{% highlight scala %}

     val intOptionalSome:Optional[Int] = CSome(10)

     intOptionalSome match {
     case CSome(value) => println("value is {}", value)
     case CNone => println("none value")
   }
}

{% endhighlight %}

If we skip one of the cases Scala compiler gives below warning.


{% highlight text %}

[warn] match may not be exhaustive.
[warn] It would fail on the following input: CNone
[warn]      intOptionalSome match {
[warn]      ^
[
{% endhighlight %}


### Using Option ADT in Rust

{% highlight scala %}

  let int_optional_some:Optional<i32> = Optional::CSome(10);

  match int_optional_some {
    Optional::CSome(value) => println!("value is {}", value),
    Optional::CNone => println!("none value")
   }
}
{% endhighlight %}

If we skip one of the cases Rust compiler gives below error.

{% highlight text %}
int_optional_some {
   |         ^^^^^^^^^^^^^^^^^ pattern `CNone` not covered
{% endhighlight %}


## Code
You can find complete code for Scala [on github](https://github.com/phatak-dev/Rust-scala/blob/master/scala/src/main/scala/com/madhukaraphatak/scala/simple/TypeInference.scala).

You can find complete code for Rust [on github](https://github.com/phatak-dev/Rust-scala/blob/master/Rust/simple/src/main.rs).



## Conclusion

ADT's are one of the best abstractions to model different data in programming. Rust has excellent support for them like we have it in Scala. So if you enjoy using ADT's in the Scala, those goodies will transfer when you start dabbling in Rust.
