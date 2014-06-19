---           
layout: post
title: "Converting Matlab file to spark RDD"
categories: spark
---
Many of the times, research data available to data science is in matlab format. So if you want to process that data through spark you have to have a way to convert matlab files to spark rdd's. This post I am going to discuss about using open source [JMatIO](http://sourceforge.net/projects/jmatio/) library to convert matlab files to spark rdd's.


##JMatIO - Matlab's MAT-file I/O in JAVA

JMatIO is an open source library provided to read matlab files in java. We can use this to read matlab files in spark also. You can download jar from [here](http://sourceforge.net/projects/jmatio/) or if you are using maven , add following dependency 

{% highlight xml %}
  <dependency>
    <groupId>net.sourceforge.jmatio</groupId>
    <artifactId>jmatio</artifactId>
    <version>1.0</version>
  </dependency>

{% endhighlight %}

### Reading a mat file
We are going to use mnsit mat file for this example. It has following for matrix in it


1. train_x - train data x features
2. train_y - train data labels
3. test_x -  test data x features
4. test_y -  test data labels

* Reading mat file using JMatIO

{% highlight scala %}
val file = new MatFileReader("src/main/resources/mnist_uint8.mat")
val content = file.getContent  
{% endhighlight %}

* Getting specific Matlab variable from content
{% highlight scala %}
 val train_x = content.get("train_x").asInstanceOf[MLUInt8].getArray
 val train_y = content.get("train_y").asInstanceOf[MLUInt8].getArray
{% endhighlight %}

Casting to MLUInt8 says that array content is integers. 

* Converting Matlab arrays to spark label point rdd
{% highlight scala %}
val trainList = toList(train_x,train_y)
val trainRDD = sparkContext.makeRDD(trainList)
{% endhighlight %}

* toList method

{% highlight scala %}
 def toList(xValue:Array[Array[Byte]],yValue:Array[Array[Byte]]):Array[(Double,Vector)] ={
     xValue.zipWithIndex.map{
       case (row,rowIndex) => {
         val features = row.map(value => value.toDouble)
         val label = yValue(rowIndex)(0).toDouble
         (label,Vectors.dense(features))
       }
     }
   }

{% endhighlight %}

"toDouble" is used as spark label point expects all values to be in double. 


* Saving rdd for further processing
{% highlight scala %}
trainRDD.saveAsObjectFile("mnsit")
{% endhighlight %}









