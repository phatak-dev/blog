---           
layout: post
title: "Functional programming in C++"
date : 2014-11-16
categories: functional-programming cplusplus
---

Few days back I attended a [talk](https://geekup.in/2014/olvemaudal) on modern C++ [features](http://www.pvv.org/~omazModernCPP_Bangalore_Nov2014_reduced.pdf) given by [Olve Mudal](https://twitter.com/olvemaudal). I have not coded in C++ after passing out of university. So it was interesting to revisit the language after long time. In the talk, speaker mentioned that modern C++ has good support for functional programming. 

C++ and functional programming!!!. I got fascinated by this and started digging around the C++ specification to find about it. I am really impressed by what I saw. So I wrote this post to show how you can write elegant functional programs with C++ now.

## Compiler requirements
Functional features of the language are introduced in C++11 standard. So if you want to compile and run the code, you need a c++ compiler which supports the above standard. If you are using g++, you need version 4.9 and above. Follow [these](http://askubuntu.com/questions/428198/getting-installing-gcc-g-4-9-on-ubuntu) steps to install g++ 4.9 on Ubuntu. If you are using any other compiler or OS use google to find out suitable versions.

**tl;dr You can access complete example code with running instructions [here](https://gist.github.com/phatak-dev/766eccf8c72484ad623b).**

## New language constructs 
The following are the new language constructs used by example code.

### auto keyword
In C++11 standard, meaning of *auto* keyword is changed. Now auto is used for doing type inference. Whenever you declare a variable as auto, compiler tries to infer the type by looking at it's value. 

	{%highlight c++ %} 
 	auto i = 10;
	{%endhighlight%}

 In the above code, *i* will be type inferred as an integer. 

 Using type inference, we can skip specifying types which makes code more clean.

### Functional data types AKA lambdas
In functional programming, functions are treated as objects. But in C++, functions are not treated as an object. To remedy that,function objects or lambdas are just a class with operate method.

As it's tedious to create a class for each function, C++ provides a shortcut syntax to create function objects.

{%highlight c++ %} 
 auto println = [](const char  *message){ std::cout << message << std::endl;};
{%endhighlight%}

Above code is creating a function object,println, which takes a single parameter and returns nothing. The *[]* brackets are used to specify the environment aka closure for the function. We will see more about closure in later part of the post.

As you can see, we use auto here so that we don't have to care about how these function objects are encoded inside the C++ types.

## Functional combinators on C++ vector
We are going to see how we can do functional programming by looking at how we can apply different combinators like map,filter etc on C++ vector. Though we have used vector in this example, it should work for any other collection.


Modern C++ ships with a powerful library called as **algorithm**. This supports lot of nice operators like for_each,transform on collection. You can import the library by adding *#include &lt;algorithm&gt;* in your code. Most of our examples use algorithm methods.


Let's look at the example of combinator's one by one.

### for_each combinator

{%highlight c++ %} 
template <typename Collection,typename unop>
void for_each(Collection col, unop op){
  std::for_each(col.begin(),col.end(),op);
}
{%endhighlight%}

We are directly using for_each method of algorithm. First two parameters are starting and ending of the collection. Then the third parameter we pass a unary function object which operates on each element.

The following code uses it to print elements of a vector.

{%highlight c++ %} 
 auto lambda_echo = [](int i ) { std::cout << i << std::endl; };  
 std::vector<int> col{20,24,37,42,23,45,37};
 for_each(col,lambda_echo);
{%endhighlight%}

In the above code,we have a echo lambda, which prints element passed to it. 

### map combinator 
In algorithm, there is a transform function which allows us to implement map functionality.

{%highlight C++ %} 
template <typename Collection,typename unop>
  Collection map(Collection col,unop op) {
  std::transform(col.begin(),col.end(),col.begin(),op);
  return col;
}
{%endhighlight%}

The following code calls the map function to add one to each element.

{%highlight C++ %} 
 auto addOne = [](int i) { return i+1;};
 auto returnCol = map(col,addOne);
 for_each(returnCol,lambda_echo);
{%endhighlight%}

In same way you can [implement](https://gist.github.com/phatak-dev/766eccf8c72484ad623b) zip,exists combinators.


### Filter combinator 

All above combinators including zip and exists, do not change the size of the collection. But if you want to implement filter, you have to change the size.

No method in *algorithm* allows you to change the size of the collection. So we need to implement filter in following two steps

  * Determine the indexes inside the collection which do not satisfy predicates.

  * Remove those indexes from the vector.

Before implementing filter, we will implement **filterNot**. *filterNot* combinator removes all the elements which satisfies the predicate.

{%highlight C++ %} 
template <typename Collection,typename Predicate>
Collection filterNot(Collection col,Predicate predicate ) {   
    auto returnIterator = std::remove_if(col.begin(),col.end(),predicate);
    col.erase(returnIterator,std::end(col));    
    return col;
}
{%endhighlight%}

We have used *erase* function on vector to remove the elements that determined by *remove_if* function.

Now we can implement filter in terms of *filterNot*.

{%highlight C++ %} 
template <typename Collection,typename Predicate>
Collection filter(Collection col,Predicate predicate) {
 //capture the predicate in order to be used inside function
 auto fnCol = filterNot(col,[predicate](typename Collection::value_type i) { return !predicate(i);});
 return fnCol; 
}
{%endhighlight%}

If you observe code carefully, you can see that we have captured that *predicate* in our lambda so that we can use it inside. If you don't capture it, compiler will give an error.

Also as we don't know the type expected by predicate, we can use *Collection::value_type* to say whatever the type of the collection elements it will be taken by the predicate. This makes our code highly generic.

Finally we can filter as follows

{%highlight C++ %} 
 auto filteredCol = filter(col,[](int value){ return value > 30;});
 for_each(filteredCol,lambda_echo); 
{%endhighlight%}


## Memory management
As you may know, in C++ you have to manually manage the memory. In the above examples we have used pass by value style over pass by reference. This makes code easy to read and understand, but makes lot of copies of collection. With modern C++ you can use value semantics with move semantics to get same performance as pass by reference. Read more about it [here](http://stackoverflow.com/questions/3106110/what-are-move-semantics). Also you can use [smart pointers](http://msdn.microsoft.com/en-us/library/hh279674.aspx) to do automatic garbage collection.


## Compiling and Running
Clone the code from [here](https://gist.github.com/phatak-dev/766eccf8c72484ad623b). Refer to *README.md* for compiling and running instructions.

## Conclusion
C++ has changed dramatically over the last decade. It's no more *C with Classes*. If you not looked at C++ recently, it will be the right time to have another look.














