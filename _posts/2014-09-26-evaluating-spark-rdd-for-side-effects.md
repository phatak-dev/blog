---           
layout: post
title: "Evaluating Spark RDD's for side effects"
date : 2014-09-29
categories: spark
---
Accumulators in Spark are highly useful to do side effect based operations.
For example, the following code calculates both sum and sum of squares as a side effect.

{% highlight scala %}
//sc is spark context
val sum = sc.accumulator[Double](0.0)
val squaredSum = sc.accumulator[Double](0.0)
val values = 0 to 100000   
val inputRDD = sc.makeRDD(values)
val sumRDD = inputRDD.map(value => {
   sum+= value
   squaredSum+= value*value
 })
println("sum is "+sum.value+" and square sum is " + squaredSum.value)
{% endhighlight %}

The code looks good, but it will produce zero as the sum. This is because map is a lazy operation. Here we want to evaluate sumRDD just to update accumulators. Normally we use **collect** or **count** to trigger the calculation. But collect unnecessarily loads whole split to memory and count does the unnecessary shuffling. 

So we need an operation which just evaluates the RDD for it's side effect without actually returning any value. 
 
##Evaluating a RDD
The following function takes an RDD and evaluates it
{% highlight scala %}
 def evaluate[T](rdd:RDD[T]) = {
    rdd.sparkContext.runJob(rdd,(iter: Iterator[T]) => {
      while(iter.hasNext) iter.next()
    })
  }
{% endhighlight %}

We are using **runJob** api on context which triggers the evaluation. Api takes a RDD which has to be evaluated and a function which of form
  {% highlight scala %}
    (iterator:Iterator[T]) => U
  {% endhighlight %}

We pass a function which just goes over the iterator without producing any value. This allows us to update just the needed accumulators.

##Using evaluate
Now we use evaluate function to evaluate our **sumRDD** and get our accumulator values.
{% highlight scala %}
 evaluate(sumRDD)
 println("sum is "+sum.value+" and squared sum is " + squaredSum.value)
{% endhighlight %}

Now it prints correct values of sum and squared sum.




