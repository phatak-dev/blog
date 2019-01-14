---
layout: post
title: "Scala Developer Journey into Rust - Part 2 : Type Inference"
date : 2019-01-14
categories: rust scala rust-scala
---
Rust is one of the major programming languages that's been getting popular in recent years. It has many advanced high level language features like Scala.This made me interested to learn Rust. So in this next series of blogs I will share my experience with Rust from a Scala developer point of view. I would like to explore how these two language approach things. I would like to explore the similarities and their differences.

This is second post in the series. In this post, I will be talking about type inference. You can find all the other posts in the series [here](/categories/rust-scala).


## Type System

As Scala, Rust has very strong type system. It has many goodies provided by Scala to make sure that programs are correct at the time of compilation. Having a strong type system comes up with it's on challenges. One of the challenge is to provide the right types everywhere. Scala elevates this challenge by inferring most of the types by itself.This makes programmer's life much easier.

As Scala, Rust also has type inference. It's one of the first system level programming language which has support for the type inference. This features makes Rust easy to start as we don't need worry about the types and their memory model.

In this post, we will be discussing about how Rust's type inference compares to Scala.

## Type Inference
There are different places where we need type inference. The below are some of those examples.

### Type Inference of Normal Variables

In Scala 

{% highlight scala %}
val a = 10
{% endhighlight %}

In Rust,

{% highlight rust %}
  let _a = 10;
{% endhighlight %}

Variable inference looks almost same in both languages.


### Checking Types

Scala has *asInstanceOf* to check the type. For earlier example, we can check the type as below

{% highlight scala %}
assert(a.isInstanceOf[Int], "not a int")
{% endhighlight %}

Rust doesn't have any facility like this. We can use the different types to produce a compile error which gives us idea what's the inferred type is 

{% highlight rust %}

assert!(a != 10.0, "not a int");
/* prints can't compare `{integer}` with `{float}`*/
{% endhighlight %}

### Type Inference for Collections

In Scala,

{% highlight scala %}
    val values = List(10,20,30,40)
{% endhighlight %}

In Rust,

{% highlight rust %}
let vector = vec!(1,2,3,4);

{%endhighlight %}
### Type Inference in Function Arguments

Function arguments are one the place in Scala where the user has to specify the arguments explicitly. This is same in Rust also.

In Scala

{% highlight scala %}
def add(a : Int, b: Int) = {
    a + b
}
{% endhighlight %}

In Rust,

{% highlight rust %}
fn add( a: i32, b : i32) -> i32 {
    a + b
}
{% endhighlight %}

**One change from Scala is, in Rust return type should be always specified. In scala it's inferred from the last line**.

I feel this is an improvement , as specifying return type explicitly makes interface of the function more explicit.


## Code

You can find complete code for Scala [on github](https://github.com/phatak-dev/Rust-scala/blob/master/scala/src/main/scala/com/madhukaraphatak/scala/simple/TypeInference.scala).

You can find complete code for Rust [on github](https://github.com/phatak-dev/Rust-scala/blob/master/Rust/simple/src/main.rs).

## Conclusion

From the above examples, we can observe that Rust type inference is very similar to Scala. So if you are comfortable with these aspects in Scala, you will feel right at home in Rust.
