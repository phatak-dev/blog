---
layout: post
title: "Functional Programming in Rust - Part 1 : Function Abstraction"
date : 2016-08-24
categories: rust rust-functional
---
Rust is a new system programming language developed at mozilla. It is a competitor to C and C++ with machine level access and no gc. But it's not just better C.It brings many novel features of higher level languages like Java, Scala to system level programming.This combination of low level access, speed of C combined with flexibility  and expressiveness  of functional programming language like scala makes it very interesting language.

In this series of blog posts, I will discuss how to do functional programming in rust. Most of the examples are inspired from scala, as I use scala in my every day work. If you are new to rust, I highly recommend [Rust Book](https://doc.rust-lang.org/book) as a starting point. Use the book to install rust on your machine and familiarise with basic syntax.

This is the first blog in the series which focuses on defining functions and using them in different scenarios. You can access all the posts in series [here](/categories/rust-functional).

TL;DR You can access complete code on [github](https://github.com/phatak-dev/fpinrust).

## Defining a function in rust
{% highlight rust %}
fn simple_function() {
	println!("function called");
}
{%endhighlight%}

The above code shows how to define a function. *fn* keyword marks beginning of a function followed with function name and argument. As with scala, if we don't specify any return type it will be inferred as unit.

The body of function is written inside { } brackets.

## Function variable

In functional programming languages, a function is a first class citizen. So we can store a function inside a variable and return the function from other function. So in this example, we will see how to store a function in a variable.

{% highlight rust %}
fn add(a:i32, b:i32) -> i32  {
    a + b
    }
let fn_variable = add;
println!("calling using function variable {}",fn_variable(10,20));
{%endhighlight %}

The above code show how to define a function variable in rust. First we define a function named add. In this function, we specify the return type explicitly. Unlike scala, in rust return types are not type inferred. The last line of the function is considered as return statement.

Once we have defined the function, we can store in a variable as shown in *fn_variable*. Once we have the variable then we can invoke as regular function.

## Higher Order function - Take function as parameter

Once we are able to store a function in a variable, we should be able to pass the function to another function. These kind of functions are known as higher order functions. The below code shows how to define function which accepts another function as parameter.

{% highlight rust %}
fn higer_order_fn<F>(value:i32, step: F)  -> i32
                    where F: Fn(i32) -> i32 {
    step(value)
}
{%endhighlight%}

The above code shows a higher order function. The interesting piece in code is the type of second parameter step. step is of generic type F which is defined in  the where clause.

*where* clause in rust is used for type bound on generics. The type bound in our example says, F is of type Fn, one of the trait for function type, which takes i32 value and returns i32. i32 is one of the data types for representing number with 32 bits.

Once we have defined the higher order function, let's see how to invoke it using functions. There are two ways to invoke the function. One by passing a named function  and another using anonymous functions.

### Using Named Function 

{%highlight rust %}
fn add_one(x:i32)->i32 { x+1}
let result = higer_order_fn(20, add_one);	 
{%endhighlight%}

The above code defines a named function add_one and passes it to higher_order function.

### Using anonymous functions
Anonymous functions are in rust are part of closure feature of rust. Closures are special functions which has access to their surrounding scope. So anonymous functions are closures with empty scope.

{%highlight rust %}
let result = higer_order_fn(20, |x:i32| x +1 );
{%endhighlight%}

The above code show how to pass an anonymous function. You can read more about rust closures [here](https://doc.rust-lang.org/book/closures.html).

## Higher Order function - Return a function from function

As we can pass a function as parameter to a function, we can return a function from another function. 
This is quite simple in most of the functional programming languages, but it's its little bit tricky in rust.

In this code I will show how to achieve the return of a function. 
{% highlight rust %}
fn higer_order_fn_return<'a>(step_value:& 'a i32) -> 
                            Box<Fn(i32) -> i32 + 'a > {
       Box::new(move |x:i32| x+step_value)
}

{%endhighlight%}

The above code looks complicated. The reason to have the complications is the way rust works with lifetimes. Let's try to understand what's going on here.

In our code, we are defining a function which take i32 value as parameter. Then the function should return a function which wraps this value with logic to increment given value with the step. The question we ask ourselves is how long this function lives?

In garbage collected language like Scala, it's easy as garbage collector take care of this issue. But rust doesn't have a gc. So rust has to determine the lifetime of the function in the compile time only.

In our example, we are defining a scope **'a** which is associate a scope with input value. So we are saying here to compiler, keep lifetime of function as long as value step_value exist. Lifetimes in rust can only exist with references. So in our example we will take &i32 rather than i32. Also we create to reference to Fn using Box.

Now we understand the lifetime of formal parameters and return type. But what about move in our implementation?. The value we take as parameter is created in stack. So when function returns the step_value is destroyed. So move says copy the step_value as part of closure.

You can read more about returning closures [here](https://doc.rust-lang.org/book/closures.html#returning-closures).

The below code shows how to invoke the above function.

{% highlight rust %}
let step_value = &10;
let step_function = higer_order_fn_return(step_value);
println!("the stepped value is{}", step_function(50)); 
{%endhighlight%}
You can access complete code [here](https://github.com/phatak-dev/fpinrust/blob/master/src/bin/definefunctions.rs).

## Running code

You can run all the examples using the below command
{% highlight sh %}
cargo run --bin definefunctions
{%endhighlight%}


So in this we got familiar with rust function abstraction. In next post, we will discuss how to use this abstractions to build function combinators on collections.
