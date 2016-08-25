---
layout: post
title: "Functional Programming in Rust - Part 2 : Functional combinators"
date : 2016-08-25
categories: rust rust-functional
---
Rust is a new system programming language developed at mozilla. It is a competitor to C and C++ with machine level access and no gc. But it's not just better C.It brings many novel features of higher level languages like Java, Scala to system level programming.This combination of low level access, speed of C combined with flexibility  and expressiveness  of functional programming language like scala makes it very interesting language.

In this series of blog posts, I will discuss how to do functional programming in rust. Most of the examples are inspired from scala, as I use scala in my every day work. If you are new to rust, I highly recommend [Rust Book](https://doc.rust-lang.org/book) as a starting point. Use the book to install rust on your machine and familiarise with basic syntax.

This is the second blog in the series which focuses on the functional combinators. You can access all the posts in series [here](/categories/rust-functional).

TL;DR You can access complete code on [github](https://github.com/phatak-dev/fpinrust).

## Functional Combinators

Functional combinators are anonymous functions which allows us to manipulate collection of objects in a elegant manner. In functional languages like Scala, we use combinators like map,flatMap extensively to manipulate collection API's.

As rust supports functions as first class objects, we can manipulate collections like arrays, vectors using functional combinator rather than using typical loops structure.

## Iterator Trait

All functional combinators in rust are defined on the *Iterator* trait. All the built in collections like arrays, vectors implement the iterator trait.

## Laziness

One of the important feature of rust iterators are they are lazy in nature. So whenever we apply any combinator, it will not execute until we call a specific action. We will see more of this in our examples.

## Defining a Vector

To use function combinators we need to have access to collection. So we are defining a vector in below code

{% highlight rust %}
let vector = vec!(1,3,4,5,3);
{%endhighlight %}

## Map Combinator
{% highlight rust %}
let mapped_vector = vector.iter().map(|&x| x +1).collect::<Vec<i32>>();
println!("{:?}",mapped_vector);
{%endhighlight %}

In above code, first we access iterator using *iter()* method. Then we call our map combinator and pass a closure to it.

As we discussed earlier, map is lazy. So we need to call collect in order to force the computation. Also when we call collect, we need to specify the type it returns.

This collect API is similar to Spark's collect RDD API.

The following code snippets follow same patterns as map combinator to achieve different manipulations on vector.

## Filter
{% highlight rust %}
let filtered_values = vector.iter().filter(|&x| x%2 ==0).collect::<Vec<&i32>>();
{%endhighlight %}

## Count
{% highlight rust %}
let vec_count = vector.iter().count();
{%endhighlight %}

## Zip with Index

{% highlight rust %}
let index_vec = 0..vec_count;
let index_zipped_vector = vector.iter().zip(index_vec).collect::<Vec<(&i32,usize)>>(); 
{%endhighlight %}

## Fold

{% highlight rust %}
let sum = vector.iter().fold(0,(|sum,value| sum+value));
{%endhighlight %}

## Max
{% highlight rust %}
let max = vector.iter().max().unwrap();
{%endhighlight %}

In max, rust returns an option value. We use *unwrap* method to get the value from option. It's similar to *get* method on scala option.

## For All

{% highlight rust %}
let greater_than_zero = vector.iter().all(|&x| x > 0 ) ;
{%endhighlight %}


## FlatMap

{% highlight rust %}
let lines_vec = vec!("hello,how","are,you");
let words_vec = lines_vec.iter().flat_map(|&x| x.split(",")).collect::<Vec<&str>>();
{%endhighlight %}

You can access complete code [here](https://github.com/phatak-dev/fpinrust/blob/master/src/bin/fncombinators.rs).

## Running code

You can run all the examples using the below command
{% highlight sh %}
cargo run --bin fncombinators
{%endhighlight%}

So in this post, we looked at how to use functional combinators to manipulate collections in rust.
