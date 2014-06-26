---           
layout: post
title: "Converting Java collections to Scala"
date : 2014-06-27
categories: scala java
---
Many times when you use Scala, you want to interact with a Java api like JDBC,Hibernate etc. All of those api's return Java collections. Java collections are not as rich as Scala collections. So you may want to convert them to Scala collections so that you can write idiomatic Scala code.  


On the same note, if you want to pass a Scala collection to a Java api, you have to convert  from Scala to Java collection. This post shows you different ways of achieving these conversions using Scala standard library.

###Before Scala 2.8 

In earlier versions of Scala , language endorsed the implicit conversions to convert from one data type to another data type. So till Scala 2.8 ,language included "scala.collection.JavaConversions" api which automatically converts from Java to Scala and vice versa.

The following code shows the api in action     

First import 
{% highlight scala %}
  import scala.collection.JavaConversions._
{% endhighlight %}

{% highlight scala %}
    val javaList = new java.util.ArrayList[Double]()
    javaList.add(10.0)
    javaList.add(20.0)
    javaList.add(40.0)

    //with the above import you can use all 
    //scala collection api directly on javaList
    javaList.map(value => value*10)
 
{% endhighlight %}

Though it looks simple, there is one caveat with this code. The person who reads this code may assume that methods like map are available on Java collections itself. He will be not aware of behind the scene implicit conversions.That will be bad for maintenance. Also, if there are many Java collections in the code, there will unnecessary performance penalties for these automatic conversions.

To solve the above issues, there is better way from Scala 2.8. 

###"Pimp My library" pattern from Scala 2.8.1 

From Scala 2.8.1, these conversions are made explicit using "scala.collection.JavaConverters._" api. The following code shows same conversion using this api.

First import 
{% highlight scala %}
 import scala.collection.JavaConverters._
{% endhighlight %}

Use "asScala " to convert Java list to Scala list
{% highlight scala %}
 val javaList = new java.util.ArrayList[Double]()
    javaList.add(10.0)
    javaList.add(20.0)
    javaList.add(40.0)

 // use asScala to convert
 val scalaList = javaList.asScala
 val sum = scalaList.sum
 val squareList = scalaList.map(value => value*value)
 println("sum is "+ sum)
 println("square list is" + squareList)

{% endhighlight %}

Converting Java map to Scala map
{% highlight scala %}
val javaHashMap = new java.util.HashMap[String,Double]()
    javaHashMap.put("jack",1000)
    javaHashMap.put("bob",2000)

val scalaMap = javaHashMap.asScala
println(scalaMap.getOrElse("dummy",0))
println(scalaMap.map(value =>(value._1,value._2*value._2)))

{% endhighlight %}

You can use "asJava" to convert from Scala to Java 

{% highlight scala %}
val jList = scalaList.asJava
println(jList.getClass.getName)

{% endhighlight %}


As you can see here, the conversion is explicit which makes code more readable. Its also helps in performance , as we are converting only necessary collections, not all collections in the scope. So it is the recommended way of making conversions . This way of making conversions explicit is called ["Pimp My library"](http://alvinalexander.com/scala/scala-2.10-implicit-class-example) pattern.






