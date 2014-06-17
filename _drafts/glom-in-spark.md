---           
layout: post
title: "Glom in spark"
categories: spark
---
Today I am going to discuss about a operation called "glom" on spark rdd which allows you to treat a partition as an array rather as single row at time. This allows you speed up some operations with some  increased memory usage.

Let's say you want to find out maximum in a given RDD. 

Now you can do it using map and reduce operations as below.

{% highlight scala %}

val dataList = List(50.0,40.0,40.0,70.0)
val dataRDD = sc.makeRDD(dataList)  
val maxValue =  dataRDD.reduce (_ max _)

{% endhighlight %}

Though it works, there will be lot of shuffles between partitions for comparisons.Thats not good, particularly for large data.

Let's rephrase our solution like as follows. 
Rather than comparing all the values, we can   

1. First find maximum in each partition    
2. Compare maximum value between partitions to get the final max value

Now we need a way to compare all values in a given partition. This can be easily done using glom as follows.

{% highlight scala %}
  
 val maxValue = dataRDD.glom().map((value:Array[Double]) => value.max)
 .reduce(_ max _)

{% endhighlight %}

As you can see here, only maximum of each partition are shuffled rather than all the values.

###Using glom for calculating weighted matrix
Glom is highly useful when you want to represent rdd operations as matrix manipulations. In many machine learning algorithms you will be needed to find weighted value of rows , i.e multiplying each row by a given weight vector. Doing this row by row, using map operation will be very costly as you will be not able to use matrix libraries optimization.

But with glom, you can multiply with whole partition at a time so that your computation will speed up significantly.Code listing for same is below


{% highlight scala %}
  // Weighted sum using glom
  import org.jblas.DoubleMatrix
  val rowsList = List[List[Double]](
      List(50.0,40.0,44.0),
      List(88,44.0,44.0),
      List(855,0,55.0,44.0),
      List(855,0,55.0,70.0)
    )
  val weights = List(1.0,0.5,3)
  val rowRDD = sc.makeRDD(rowsList)
  val result = rowRDD.glom().map( value =>{
      val doubleMatrix = new DoubleMatrix(value.map(value => value.toArray))
      val weightMatrix = new DoubleMatrix(1,weights.length,weights.toArray:_*)
      doubleMatrix.mmul(weightMatrix.transpose())

  })
  
{% endhighlight %}






