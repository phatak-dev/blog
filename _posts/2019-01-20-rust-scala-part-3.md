---
layout: post
title: "Scala Developer Journey into Rust - Part 3: Expression Based Language"
date : 2019-01-20
categories: rust scala rust-scala
---
Rust is one of the major programming languages that's been getting popular in recent years. It has many advanced high level language features like Scala.This made me interested to learn Rust. So in this next series of blogs I will share my experience with Rust from a Scala developer point of view. I would like to explore how these two language approach things. I would like to explore the similarities and their differences.

This is third post in the series. In this post, I will be talking about expression based language. You can find all the other posts in the series [here](/categories/rust-scala).

## Expression Based Language

Rust syntax looks very similar to C/C++ languages. But in reality,it's quite different than these languages. One of the important distinction is statement vs expression.

In a programming language, expression is a block of code which returns a result always. Whereas statement is block of code which doesn't return anything.It depends upon the side effects for actual work. For ex: In C or Java, if condition is a statement because it doesn't return any value.

One of the challenges with statement based languages is they don't compose well. Also writing immutable data based programs in these languages become tricky as they depend heavily on side effect based programming.

Scala and other family of functional languages prefer expressions over statements. In these languages, everything is an expression. All the conditions, loops, block of code returns a value. This makes them highly composable. 

Rust follows this suite for system programming. Rust is an expression based language. Every construct of rust return an expression which makes it behave very much like Scala. That's why you will see similar construct like pattern matching etc. in Rust also.

In the next few sections, we will see how Rust expression based programming compares to the Scala.

## If and Else Condition

One of the important part of expression based language is, conditions are also expression. The below show how if/else works as an expression.

In the below code we check if a given variable is even or not. Also we store the result in a variable rather than updating from the condition.

In Scala,

{% highlight scala %}

val isEven = if(a % 2 == 0) true else false

{%endhighlight%}

In Rust,

{% highlight rust %}
let is_even = if a % 2 == 0 {true} else {false};
{%endhighlight%}


## Block Expression

In an expression based language any block of code can return a value. The below shows such an example.

In Scala,

{% highlight scala %}

 val blockResultExpression =  {
      1+2;
 };

{%endhighlight%}


In Rust, 

{% highlight rust %}
let block_result_statement =  {
  1+2;
};

println!("block statement result is {:?}",block_result_statement);

let block_result_expression =  {
   1+2
};

println!("block expression result is {:?}",block_result_expression);
{% endhighlight %}


The result of the above rust is as below

{% highlight text %}
block statement result is ()
block expression result is 3
{%endhighlight%}

Both code looks almost same. But they return different result.

**In Rust, a semicolon is used to turn expression into a statement. So in the first example, since we used ; it became a statement and return unit value. This facility is there for easy interoperability with C/C++**.

## Expressions and Functional Programming

Expressions make composing easy.One of the important part of functional programming is to write programs by composing different functions. So having expression as first class citizens, makes Rust ideal candidate for many constructs of functional programming. You can read more about it in my [earlier](/categories/rust-functional) posts.
## Code

You can find complete code for Scala [on github](https://github.com/phatak-dev/rust-scala/blob/master/scala/src/main/scala/com/madhukaraphatak/scala/simple/Expression.scala).

You can find complete code for Rust [on github](https://github.com/phatak-dev/rust-scala/blob/master/rust/simple/src/bin/expression.rs).

## Conclusion

Rust is an expression based language like Scala. It makes it more composable like functional programming language. So if you are familiar with Scala expressions like conditions,patterns matching, functional combinators you can easily employ them in Rust too.
