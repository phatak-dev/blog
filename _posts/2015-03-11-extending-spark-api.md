---           
layout: post
title: "Extending Spark API"
date : 2015-03-11
categories: spark scala
---
Apache Spark comes with lot of built in generic operators to do data processing. But many a times, when we are building real world applications, we need domain specific operators to solve problem in hand. So in these cases, we like to extend the Spark API to add our own custom operators.


We can extend spark API in two ways. One of the way is to add custom operator for existing RDD's and second is to one create our own RDD. 

In this post, we are going to discuss both the methods.

tl;dr Access complete code on [github]({{ "/ExtendingSpark" | prepend: site.github_code_root }}). 

## Motivation

Let' say we have sales data from an online store. The data is in csv format. It contains *transactionId*, *customerId*, *itemId* and *itemValue*. This model is represented as SalesRecord.

{% highlight scala %}
 class SalesRecord(val transactionId: String,
                  val customerId: String,
                  val itemId: String,
                  val itemValue: Double) extends Comparable[SalesRecord]
with Serializable
{% endhighlight %}


So whenever we get sales data, we convert the raw data to RDD[SalesRecord].

{% highlight scala %}
val sc = new SparkContext(args(0), "extendingspark")
val dataRDD = sc.textFile(args(1))
val salesRecordRDD = dataRDD.map(row => {
    val colValues = row.split(",")
    new SalesRecord(colValues(0),colValues(1),
    colValues(2),colValues(3).toDouble)
})  
{% endhighlight %}


Let's say we want to find out total amount of sales, then in Spark we can write

{% highlight scala %}
  salesRecordRDD.map(_.itemValue).sum  
{% endhighlight %}

though it's concise, it's not super readable. It will be nice to have

{% highlight scala %}
salesRecordRDD.totalSales
{% endhighlight %}

In the above code, the *totalSales* feels like built in spark operator.Of course spark don't know anything about our data or our data model.  Then how we can add our own custom operator on RDD?


## Adding custom operators to RDD

The following are the steps to add custom operator's to RDD.

### Step 1 : Define Utility class to hold custom operators

The following code defines an utility class, *CustomFunctions* , which holds all the custom operators. We take specific RDD,i.e RDD[SalesRecord] so that these operators only available on sales record RDD.

{% highlight scala %} 
class CustomFunctions(rdd:RDD[SalesRecord]) {
  def totalSales = rdd.map(_.itemValue).sum  
}  
{% endhighlight %}

### Step 2 : Implicit conversion to add operators on RDD 

The following code defines an implicit function, *addCustomFunctions* which will add all the custom functions defined in *CustomFunctions* to the RDD[SalesRecord]

{% highlight scala %}
object CustomFunctions {
  implicit def addCustomFunctions(rdd: RDD[SalesRecord]) = new
  CustomFunctions(rdd) 
}  
{% endhighlight %}

### Step 3: Use custom functions, using implicit import
The following code has access to custom operator, *totalSales* using *CustomFunctions._* import. 

{% highlight scala %}
import CustomFunctions._
println(salesRecordRDD.totalSales)
{% endhighlight %}


With the above steps, you defined a domain specific operator on RDD.

## Creating custom RDD

In the earlier example, we implemented an action which result in single value. But what about the situation where we want to represent lazily evaluated actions?. For example, let's say we want to give discount to each sales in the RDD. These discounts are lazy in nature. So we need a RDD which can represent the laziness. In following steps we are going to create a RDD called *DiscountRDD* which holds the discount calculation.


### Step 1: Create DiscountRDD by extending RDD

{% highlight scala %}

class DiscountRDD(prev:RDD[SalesRecord],discountPercentage:Double) 
extends RDD[SalesRecord](prev){

// override compute method to calculate the discount
override def compute(split: Partition, context: TaskContext): Iterator[SalesRecord] =  {
  firstParent[SalesRecord].iterator(split, context).map(salesRecord => {
      val discount = salesRecord.itemValue*discountPercentage
      new SalesRecord(salesRecord.transactionId,
      salesRecord.customerId,salesRecord.itemId,discount)
})}

override protected def getPartitions: Array[Partition] = 
firstParent[SalesRecord].partitions
}  
{% endhighlight %}

In the above code, we created a RDD called DiscountRDD. It is a RDD derived by applying discount on sales RDD. When we extend RDD, we have to override two methods

* ####compute 

This method is the one which computes value for each partition of RDD. In our code, we take input sales record and output it by applying discount as specified by *discountPercentage*.

* ####getPartitions

*getPartitions* method allows developer to specify the new partitions for the RDD. As we don't change the partitions in our example, we can just reuse the partitions of parent RDD.

### Step 2: Add a custom operator named *discount*

Using similar trick discussed earlier, we can add custom operator called *discount* which creates DiscountRDD.

{% highlight scala %}
 def discount(discountPercentage:Double) = new DiscountRDD(rdd,discountPercentage)
{% endhighlight %}


### Step 3 : Use discount, using implicit import

{% highlight scala %}
 import CustomFunctions._
 val discountRDD = salesRecordRDD.discount(0.1)
 println(discountRDD.collect().toList) 
{% endhighlight %}


So now you know how you can extends spark API for your own domain specific use cases.
