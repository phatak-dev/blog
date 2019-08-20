---
layout: post
title: "Scala Developer Journey into Rust - Part 7 : Type Classes"
date : 2019-08-20
categories: rust scala rust-scala
---
Rust is one of the major programming languages that's been getting popular in recent years. It has many advanced high level language features like Scala.This made me interested to learn Rust. So in this next series of blogs I will share my experience with Rust from a Scala developer point of view. I would like to explore how these two language approach things. I would like to explore the similarities and their differences.

This is seventh post in the series. In this post, I will be talking about type classes. You can find all the other posts in the series [here](/categories/rust-scala).

## Type Class

Type class is a programming pattern which associates behaviour with types. Type class design pattern allows programmer to implement the various behaviours without using inheritance. This pattern is very popular in Scala to implement various libraries such as serialization ones. You can learn more about the type classes from this excellent [talk](https://www.youtube.com/watch?v=yYo0gANYViE).

Rust also has first class support for type classes. Rust traits follow type class pattern. 

So both Scala and Rust have similar support for type class. This post explores more about the type class implementation in Scala and Rust.

## Implementing Type Class

In this section, we will see different steps in implementing type classes.

### Defining Behaviour using Trait in Scala

First step of type class is to define the behaviour. We can do the same using trait in Scala.

{% highlight scala %}
   trait Serializable[T] {
    def serialize(v: T): String
  } 
{% endhighlight %}

Here we have defined a behaviour called **Serializable** which defines how to convert a given type **T** to **String**.

### Defining Behaviour using Trait in Rust
As we used trait in Scala, we can use trait in Rust to define the behavior.

{% highlight rust %}
pub trait Serializable<'a> {
 fn serialize(self : &Self) -> Cow<'a,str>; 
}
{% endhighlight %}
We are using **Copy on Write** pointer to return a string from the method. You can read more about it in this [blog](https://hermanradtke.com/2015/05/29/creating-a-rust-function-that-returns-string-or-str.html).

### Defining the Types in Scala

Once we have defined the behaviour, we need to define types on which this behaviour we can be implemented. We can use case classes to the same.

{% highlight scala %}

  case class Person(name: String, age: Int)
  case class Restaurant(name: String, brunch: Boolean)
 
{% endhighlight %}

### Defining the Types in Rust

In Rust, we will use struct to define the types.

{% highlight rust %}

istruct Person<'a> {
  name : &'a str,
  age : i32 
}
struct Restaurant<'a> {
  name : &'a str,
  brunch : bool
}
{%endhighlight %}


### Implementing Serializable Behaviour for Types in Scala

Once we defined the behaviour and types, next step is to implement the serialization for **Person** and **Restaurant**.

{% highlight Scala %}
  implicit object Person extends Serializable[Person] {

    def serialize(v: Person): String = "Person(" + v.name + "," + v.age + ")"
  } 
{% endhighlight %}
We use implicit object to attach the behaviour of serialization to type Person.

### Implementing Serializable Behaviour for Types in Rust
In Rust, there is no concept of implicits. But in rust, if a given trait is implemented, by importing the type all the behaviour is automatically imported. This makes the implicit redundant. This feature is inspired by type classes of Haskell.

{% highlight rust %}
mpl<'a> Serializable<'a> for Person<'a> {
  fn serialize( self: &Self) -> Cow<'a,str> {
     Cow::Owned(self.name.to_owned()+" "+ &self.age.to_string())
  }
}
{% endhighlight %}


### Using Type Classes in Scala

Once we have implemented the behaviour, we can define a generic method which can use this.

{% highlight scala %}
  def serializeMethod[T](value: T)(implicit serializer: Serializable[T]) = {
    serializer.serialize(value)
  }

{% endhighlight %}

### Using Type Classes in Rust

As in Scala, we can define generic method for serialization in rust also.

{% highlight rust %}

pub fn serialize_method<'a,T>(v:&T) -> Cow<'a,str> where T:Serializable<'a> {
  T::serialize(v) 
}
{% endhighlight %} 

## Extending Built in Types

In earlier example, we have added serialization behaviour for user defined types. But both in Scala and Rust, has ability to add this behaviour to built in types also. In this section, we can see how we can serialize the List type.

### Implementing Serialization for List in Scala

A list of serializable objects can be serialized. The below code shows the same.

{% highlight scala %}

  implicit def ListSerializable[T: Serializable] = new Serializable[List[T]] {
    def serialize(v: List[T]) = v.map(serializeMethod(_)).mkString("List(", ",", ")")
  }
{% endhighlight %}


### Implementing Serialization for List in Rust

{% highlight rust %}
impl<'a,T:Serializable<'a>> Serializable<'a> for Vec<T> {
  fn serialize( self: &Self) -> Cow<'a,str> {
      let result = self.iter().map(|x| serialize_method(x)).collect::<Vec<Cow<'a,str>>>();
      let join_result = result.join(",");
       Cow::Owned(join_result)
  }
}
{% endhighlight %}

## Code
You can find complete code for Scala [on github](https://github.com/phatak-dev/rust-scala/blob/master/scala/src/main/scala/com/madhukaraphatak/scala/simple/Typeclass.scala).

You can find complete code for Rust [on github](https://github.com/phatak-dev/rust-scala/blob/master/rust/simple/src/bin/typeclass.rs).

## Conclusion
Type classes are one of the powerful patterns in Scala and Rust which allows programmer to add different generic behaviour to types.
