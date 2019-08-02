---
layout: post
title: "Scala Developer Journey into Rust - Part 6 : Traits"
date : 2019-07-02
categories: rust scala rust-scala
---
Rust is one of the major programming languages that's been getting popular in recent years. It has many advanced high level language features like Scala.This made me interested to learn Rust. So in this next series of blogs I will share my experience with Rust from a Scala developer point of view. I would like to explore how these two language approach things. I would like to explore the similarities and their differences.

This is sixth post in the series. In this post, I will be talking about traits. You can find all the other posts in the series [here](/categories/rust-scala).

## Trait

Trait is a basic language construct which helps to define the interfaces in the program. Scala traits are an extension to Java interfaces. Scala uses traits extensively in it's libraries. 

Rust also has a good support for interface based programming. Rust's traits are inspired by Haskell's type classes. 

Both of the languages have very similar support for traits. In the next section, we will see those similarities. 


## Defining Trait

In this section, we will define a simple trait called **Display** which has single method **display**.

### Defining Trait in Scala

{% highlight scala %}
  trait Display {
    def display()
  }
{% endhighlight %}

### Defining Trait in Rust

{% highlight rust %}
trait Display {
  fn display(&self);
}
{% endhighlight %}


## Implementing Trait

In this section, we will discuss how to implement traits in these languages.


### Implementing Trait in Scala

In scala, we use the keyword **extends** to implement a trait. In below code, we implement trait in two different classes.

{% highlight scala %}

class StringDisplay extends Display {
  def display() = println("string display")
}

class IntDisplay extends Display {
   def display() = println("int display")
}

{%endhighlight %}


### Implementing Trait in Rust

In Rust, we implement a trait using **impl** and **for** keyword. Also from below code you will see that, implementation of trait is outside of the struct.

{% highlight rust %}
struct StringDisplay {}

struct IntDisplay {}

impl Display for StringDisplay {

 fn display(&self) {
   println!(" string display")
 }

}

impl Display for IntDisplay {
 fn display(&self) {
   println!(" int display")
 }
}

{% endhighlight %}


## Define Generic Method

Once we defined the trait and the implementations, we can define a generic method which can operate on any implementation of **Display**. In this section, we will see how to define the same.

### Generic Method in Scala

{% highlight scala %}

def display(d :Display) = d.display()
{%endhighlight %}

### Generic Method in Rust

{% highlight rust %}

fn display(d: &Display) {
  d.display();
}

{% endhighlight %}


## Using Generic Method

In this section we will how to call the above generic method with different implementation.


### Scala

{% highlight scala %}

val stringDisplay = new StringDisplay()
display(stringDisplay)

val intDisplay = new IntDisplay()
display(intDisplay)

{% endhighlight %}


### Rust

{% highlight rust %}

 let str_display = StringDisplay{};
 display(&str_display);

 let int_display = IntDisplay{};
 display(&int_display);

{% endhighlight %}


## Code
You can find complete code for Scala [on github](https://github.com/phatak-dev/rust-scala/blob/master/scala/src/main/scala/com/madhukaraphatak/scala/simple/TraitExample.scala).

You can find complete code for Rust [on github](https://github.com/phatak-dev/rust-scala/blob/master/rust/simple/src/bin/trait.rs).


## Conclusion
Trait are corner stone of interface programming. Both Rust and Scala have excellent support for the traits. 
