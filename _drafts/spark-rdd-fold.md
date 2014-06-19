---           
layout: post
title: "Fold in spark"
categories: spark
---
Fold is a very powerful operation in spark which allows you to calculate many important values in O(n) time . If you are familiar with Scala collection it will be like using foldLeft operation. Even if you not used fold in Scala, this post will make you comfortable in using fold.

###Syntax
{% highlight scala %}
def fold[T](acc:T)((acc,value) => acc)
{% endhighlight %}

The above is kind of high level view of fold api. It has following three things

1. T is the data type of RDD
2. acc is accumulator which will return value of the fold operation
3. A function , which will be called for each element with rdd with previous accumulator.

Since the accumulator is left of the function arguments, it is also called as foldLeft.

Let's see some examples of fold 

###Finding max in a given RDD

Let's first build a RDD

{% highlight scala %}
 val sparkContext = new SparkContext("local", "functional")
 val list = List(1, 4, 3, 5, 3, 44, 33, 44, 44)
 val listRDD = sparkContext.makeRDD(list)
{% endhighlight %}

Naive way : of doing it will by sorting. 

{% highlight scala %}
val maxBySort = listRDD.reduce(_ max _)
 println("max is "+maxBySort)
{% endhighlight %}

But this ends up doing lot of shuffling and not neat mapping.

Let's rephrase the solution like this. Find maximum value in each partiotion and then reduce it to single value.

{% highlight scala %}
val maxByMap = listRDD.mapPartitions(iterator => {
      var maximum = Int.MinValue
      iterator.foreach(value => {
        maximum = maximum max value
      })
      List(maximum).iterator
    }).reduce((a, b) => a max b)

 println("max is " + maxByMap) 

{% endhighlight %}

Though we are able to get good performance, using var is against pure functional programming. Let's update the code using val

{% highlight scala %}
 val maxByScalaFold=listRDD.mapPartitions(iterator => {
      val max =iterator.foldLeft(Int.MinValue)((acc,element ) => acc max element)
      List(max).iterator
    }).reduce((a,b) => if(a>b) a else b)

println("max is "+maxByScalaFold)
{% endhighlight %}

It works. But still feels like lot's of code . Let's optimize it.
{% highlight scala %}
val maxByRddFold = listRDD.fold(Integer.MIN_VALUE)((acc,element) => acc max element)
println("max is "+maxByRddFold)
{% endhighlight %}

Finding both min and max
{% highlight scala %}
val (min,max) = listRDD.mapPartitions(iterator => {
      val (min,max) = iterator.foldLeft((Int.MaxValue,Int.MinValue))((acc,element) => {
        (acc._1 min element, acc._2 max element)
      })
      List((min,max)).iterator
    }).reduce((a,b)=> (a._1 min b._1 , a._2 max b._2))
 println("min and maximum " + min +","+max)
{% endhighlight %}

Fold by key
{% highlight scala %}
  val deptEmployees = List(
      ("cs",("jack",1000.0)),
      ("cs",("bron",1200.0)),
      ("phy",("sam",2200.0)),
      ("phy",("ronaldo",500.0))
    )
  val employeeRDD = sparkContext.makeRDD(deptEmployees)

  val maxByDept = employeeRDD.foldByKey(("dummy",Double.MaxValue))
  ((acc,element)=> if(acc._2 > element._2) acc else element)
  
  println("maximum salaries in each dept" + maxByDept.collect().toList)

{% endhighlight %}






