---           
layout: post
title: "Fold in spark"
date : 2014-07-30
categories: spark
---
Fold is a very powerful operation in spark which allows you to calculate many important values in O(n) time. If you are familiar with Scala collection it will be like using fold operation on collection. Even if you not used fold in Scala, this post will make you comfortable in using fold.

###Syntax
{% highlight scala %}
def fold[T](acc:T)((acc,value) => acc)
{% endhighlight %}

The above is kind of high level view of fold api. It has following three things

1. T is the data type of RDD
2. acc is accumulator of type T which will be return value of the fold operation
3. A function , which will be called for each element in rdd with previous accumulator.


Let's see some examples of fold 

###Finding max in a given RDD

Let's first build a RDD

{% highlight scala %}
 val sparkContext = new SparkContext("local", "functional")
 val employeeData = List(("Jack",1000.0),("Bob",2000.0),("Carl",7000.0))
 val employeeRDD = sparkContext.makeRDD(employeeData)
{% endhighlight %}

Now we want to find an employee, with maximum salary. Spark provides *max* function on double RDD. So we can do

{% highlight scala %}
 val maxSalary = employeeRDD.map(_._2).max()
{% endhighlight %}

But with this approach we loose all other information about employee. We have to join *maxSalary* with *employeeRDD* in order to get back the name of the employee which is not good in performance point of view.



So we want an operator which can keep the original structure of RDD but still allowing to do this max operation. Fold is the right operator.

To use fold we need a start value. The following code defines a dummy employee as starting accumulator.

{% highlight scala %}
 val dummyEmployee = ("dummy",0.0);

{% endhighlight %}

Now using fold, we can find the employee with maximum salary.

{% highlight scala %}
val maxSalaryEmployee = employeeRDD.fold(dummyEmployee)((acc,employee) => { 
if(acc._2 < employee._2) employee else acc})
println("employee with maximum salary is"+maxSalaryEmployee)
{% endhighlight %}


###Fold by key
In Map/Reduce key plays a role of grouping values. We can use foldByKey operation to aggregate values based on keys.

In this example, employees are grouped by department name. If you want to find the maximum salaries in a given department we can use following code.

{% highlight scala %}
  val deptEmployees = List(
      ("cs",("jack",1000.0)),
      ("cs",("bron",1200.0)),
      ("phy",("sam",2200.0)),
      ("phy",("ronaldo",500.0))
    )
  val employeeRDD = sparkContext.makeRDD(deptEmployees)

  val maxByDept = employeeRDD.foldByKey(("dummy",Double.MinValue))
  ((acc,element)=> if(acc._2 > element._2) acc else element)
  
  println("maximum salaries in each dept" + maxByDept.collect().toList)

{% endhighlight %}






