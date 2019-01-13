---
layout: post
title: "Scala Developer Journey into Rust - Part 2 : Type Inference"
date : 2019-09-12
categories: rust scala rust-scala
---

## Type System

As Scala, Rust has very strong type system. It has many goodies provided by Scala to make sure that programs are correct at the time of compilation. Having a strong type system comes up with it's on challenges. One of the challenge is to providing the right types. Scala elevates this challenge by inferring most of the types by itself.This makes programmer's life much easier.

As Scala, Rust also has type inference. It's one of the first system level programming language which has support for the type level inference. This features makes rust easy to start as we don't need worry about the types and their memory model to start with. 

In this post, we will be discussing about how rust's type inference compares to Scala one.


## Type Inference


### Type inference of normal variables

In Scala 

{% highlight scala %}
    val a = 10
{% endhighlight %}

In Rust,

{% highlight rust %}
    let _a = 10;

{%endhighlight %}


### Type Inference for Collections

In Scala,
{% highlight scala %}
    val values = List(10,20,30,40)
{% endhighlight %}

In Rust,

{% highlight rust %}
    let mut vector = Vec::new();
    vector.push(10);

{%endhighlight %}

### Checking is Type Infered

In Scala,

{% highlight scala %}
    assert(a.isInstanceOf[Int], "not a int")
{% endhighlight %}

In Rust,

{% highlight rust %}
   assert!(a != 10.0, "not a int");
  //prints can't compare `{integer}` with `{float}`
{%endhighlight %}


