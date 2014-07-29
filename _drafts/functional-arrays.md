---           
layout: post
title: "Functional arrays in Javascript"
categories: scala
---

ES5 introduces great less known functional methods to javascript array . If you ever used 
underscore.js or any other language like in Scala.

## map

{%highlight  js %}
 var array = [1,2,4,4,5];
 var addedOne = array.map(function(value){ return value+1});
 console.log(addedOne);
{% endhighlight %}


##reduce
Max 

{%highlight  js %}
 var array = [1,2,4,4,5];
 var max = array.reduce(function(value,acc){
   if(value > acc) return value; else return acc;   
 },0);
 console.log(max);
{% endhighlight %}

Min and max together 

{%highlight  js %}
 var array = [1,2,4,4,5];
 var min_max = array.reduce(function(value,acc){
   var max,min;
   if(value > acc.max) max=value; else max=acc.max;   
   if(value < acc.min) min=value; else min=acc.min;   
   console.log(max+" "+min+" "+value.toString());
   return {"max":max,"min":min};

 },{"max":0,"min":Number.MAX_VALUE});
 console.log(min_max);
{% endhighlight %}



