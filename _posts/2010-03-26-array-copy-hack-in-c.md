---           
layout: post
title: "Array copy hack in C"
date: 2010-03-26 16:58:45 UTC
updated: 2010-03-26 16:58:45 UTC
comments: false
categories: c hacking
---

Programming is all about hacking . Hacking in good sense means tinkering the code for achieve great things.

So here is a small hack in C using which you can copy one array into another array without using any loops!!!! and obviously without using any in built in functions.

{% highlight c %}

#include
int main()
{
static int i=0; //array index
int a[10]={1,2,3}; //first array
int b[10]; //second array
b[i]=a[i];
printf("\n %d",b[i]);
i++;
if(i>2) exit(0); //continue till first array is empty
main();
}

{%endhighlight%}

The hack uses the two simple facts of C

1. A static variable will not be re initialized when a function is called

2. main ( ) is a user defined function

Do you have any better hacks? Let me know!
