---           
layout: post
title: "Glomming RDD"
categories: spark
---
Today we are going to discuss about a function called glom on rdd which allows to treat a partion as the array to speed up some operations.

Code example :
{% highlight scala %}
val sparkContext = new SparkContext("local","glomexamples")
{% endhighlight %}

Finding max using glom
{% highlight scala %}
 val dataList = List(50.0,40.0,40.0,70.0)
 val dataRDD = sparkContext.makeRDD(dataList)  
 val maxValue = dataRDD.glom().map( value => value.max).reduce(_ max _)
 println("maximum value " + maxValue)

{% endhighlight %}

Weighted sum vectorization
{% highlight scala %}
  val rowsList = List[List[Double]](
      List(50.0,40.0,44.0),
      List(88,44.0,44.0),
      List(855,0,55.0,44.0),
      List(855,0,55.0,70.0)
    )
  val weights = List(1.0,0.5,3)
  val rowRDD = sparkContext.makeRDD(rowsList)
  val result = rowRDD.glom().map( value =>{
      val doubleMatrix = new DoubleMatrix(value.map(value => value.toArray))
      val weightMatrix = new DoubleMatrix(1,weights.length,weights.toArray:_*)
      doubleMatrix.mmul(weightMatrix.transpose())

  })
  println("weighted sum is" + result.collect().toList)
{% endhighlight %}






