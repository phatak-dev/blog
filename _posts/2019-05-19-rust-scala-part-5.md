---
layout: post
title: "Scala Developer Journey into Rust - Part 5: Domain Models"
date : 2019-05-19
categories: rust scala rust-scala
---
Rust is one of the major programming languages that's been getting popular in recent years. It has many advanced high level language features like Scala.This made me interested to learn Rust. So in this next series of blogs I will share my experience with Rust from a Scala developer point of view. I would like to explore how these two language approach things. I would like to explore the similarities and their differences.

This is fifth post in the series. In this post, I will be talking about domain models. You can find all the other posts in the series [here](/categories/rust-scala).

## Domain Models

Domain models are the custom data structures which are used to represent the data in a given programming language. These data structures helps to define the vocabulary of a program.

Scala has a special classes called **case class** which are meant to be used as the domain classes. These have nice properties like immutability, comparison and hashing. 

Rust uses structs to define the domain models. It can extends different interfaces to be similar to scala case class like behaviour.

In this post, we discuss how case class and struct have very similar properties to model domain classes. 

## Defining Domain Class 

In this section, we will see how to define a domain class.

### Defining Domain Class in Scala

The below code defines the person domain class in Scala.

{% highlight scala %}

case class Person(name: String = "Anonymous", age: Int = 30)

{% endhighlight %}

### Defining Domain Class in Rust

{% highlight rust %}

#[derive(Clone,Hash,Debug,PartialEq)]
struct Person<'a> {
  name : &'a str,
  age : i32
}

impl<'a> Default  for Person<'a> {
   fn default() -> Person<'a>{
     Person{name:"Anonymous",age:30}
   }
}

{% endhighlight %}

From above code, you can observe that **struct** is used. With struct, different interfaces like Clone,Hash etc. are derived to have similar feature like scala case class.

Second part of declaration is the default constructor. 


## Create Objects with Default Constructor

In both Scala and Rust, we have defined a default constructor. This allows us to create objects without passing any values. These are useful for creating default objects.

In Scala,

{% highlight scala %}

val defaultPerson: Person = Person()

{%endhighlight %}

In Rust,

{% highlight rust %}

let default_person:Person = Default::default();

{% endhighlight %}

Rust has little bit different syntax compared to Scala. By looking at the type of variable, rust invokes needed default constructor. 

## Create Objects with Values

This section show how to create instances with non-default values.

In Scala,

{% highlight scala %}

val firstPerson = Person("jack", 20)

{%endhighlight %}

In Rust,

{% highlight rust %}

let first_person = Person{name:"jack",age:20};

{% endhighlight %}


## Comparison

Domain classes give many useful features out of the box. One of them is comparing the objects by their values. This section shows how it works.

### Scala Comparison

{% highlight scala %}

val firstPerson = Person("jack", 20)

val secondPerson = Person("john", 30)

val secondJack = Person("jack", 20)

println(s"compare first and second person, result is ${firstPerson == secondPerson}")
println(s"compare same person, result is ${firstPerson == secondJack}")

{%endhighlight%}

The output will be

{% highlight text %}

compare first and second person, result is false
compare same person, result is true

{% endhighlight %}

As you can see, the comparison is value based not reference based.

### Rust Comparison

{% highlight rust %}

let first_person = Person{name:"jack",age:20};

let second_person = Person{name:"john", age:30};

let second_jack = Person{name:"jack", age:20};


println!("compare first and second person, result is {}", first_person == second_person);
println!("compare same person, result is {}", first_person == second_jack);

{% endhighlight %}

The above code prints 

{% highlight text %}

compare first and second person, result is false
compare same person, result is true

{% endhighlight %}

## Clone

One of the important need of the domain classes is cloning. It allows to only changing values of needed property, rather than doing all the assignment over again and again.

In Scala,

{% highlight scala %}

val thirdPerson = firstPerson.copy()

{% endhighlight %}

In Rust,

{% highlight rust %}

let third_person = first_person.clone();

{% endhighlight %}

These will create a shallow copy.

## Code
You can find complete code for Scala [on github](https://github.com/phatak-dev/rust-scala/blob/master/scala/src/main/scala/com/madhukaraphatak/scala/simple/CaseClass.scala).

You can find complete code for Rust [on github](https://github.com/phatak-dev/rust-scala/blob/master/rust/simple/src/bin/struct.rs).


## Conclusion

Domain classes are one of the important abstractions to model custom data in programming. Rust has excellent support for them like we have it in Scala. So if you are familiar with domain classes  in the Scala, you can easily model the same with Rust structs.
