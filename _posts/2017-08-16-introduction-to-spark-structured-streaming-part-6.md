---
layout: post
title: "Introduction to Spark Structured Streaming - Part 6 : Stream Enrichment using Static Data Join"
date : 2017-08-16
categories: scala spark introduction-structured-streaming
---
Structured Streaming is a new streaming API, introduced in spark 2.0, rethinks stream processing in spark land. It models stream
as an infinite table, rather than discrete collection of data. It's a radical departure from models of other stream processing frameworks like
storm, beam, flink etc. Structured Streaming is the first API to build stream processing on top of SQL engine.

Structured Streaming was in alpha in 2.0 and 2.1. But with release 2.2 it has hit stable status. In next few releases,
it's going to be de facto way of doing stream processing in spark. So it will be right time to make ourselves familiarise
with this new API.

In this series of posts, I will be discussing about the different aspects of the structured streaming API. I will be discussing about
new API's, patterns and abstractions to solve common stream processing tasks. 

This is the sixth post in the series. In this post, we discuss about enriching stream data with static data. You 
can read all the posts in the series [here](/categories/introduction-structured-streaming).

TL;DR You can access code on [github](https://github.com/phatak-dev/spark2.0-examples/tree/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming).

## Stream Enrichment
In real world, stream data often contains minimal data for capturing the events happening in real time. For example, whenever there is sale happens in a e-commerce
website, it contains the customer's id rather than complete customer information. This is done to reduce the amount of data generated and transmitted per transaction 
in a large traffic site.

Often many of the stream processing operations needs the data more than that's available in the stream. We often want to add data from static stores like files or databases to stream data to do better decisions. In our example, if we have customer data in a static file, we want to look up the information for given id in the stream to understand better about the customer.

This step of adding additional information to the stream data is known as stream enrichment step in stream processing. It's one often one of the most important step of many stream processing operations.

## Unified Dataset Abstraction

In data enrichment, we often combine stream data with static data. So having both world, static and stream, talking same abstraction will make life much easier for the developer. In case of spark, both spark batch API and structured streaming API share a common abstraction of dataset. Since both share the same abstraction, we can easily join the datasets across the boundary of batch and streams. This is one of the unique feature of spark streaming compared to other streaming systems out there.

## Enriching Sales Data with Customer Data

To demonstrate the enrichment, we will enrich the sales data, which we used in earlier examples, with customer information. The below are the steps

### 1. Reading Static Customer Data 
{% highlight scala %}
case class Customer(customerId: String, customerName: String)

  val customerDs = sparkSession.read
      .format("csv")
      .option("header", true)
      .load("src/main/resources/customers.csv")
      .as[Customer]
   
{% endhighlight %}

In the above code, we read customer data from a csv file. We are using  *read* method which indicates that we are using batch API. We are converting
the data to a custom class named *Customer* using case classes.

### 2. Reading Sales Stream Data

{% highlight scala %}

  val socketStreamDf = sparkSession.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 50050)
      .load()

{% endhighlight %}

In the above code, we use *readStream* to read the sales data from socket.

### 3. Parsing Sales Data

{% highlight scala %}

case class Sales(
  transactionId: String,
  customerId:    String,
  itemId:        String,
  amountPaid:    Double)

val dataDf = socketStreamDf.as[String].flatMap(value âalue.split(" "))
val salesDs = dataDf
  .as[String]
  .map(value â
    val values = value.split(",")
    Sales(values(0), values(1), values(2), values(3).toDouble)
  })

{% endhighlight%}

The data from socket is in string format. We need to convert it to a user defined format before we can use it for data enrichment. So in above code, we parse the
text data as comma separated values. Then using *map* method on the stream, we create sales dataset.

### 4. Stream Enrichment using Joins

Now we have both sales and customer data in the desired format. Now we can do dataset joins to enrich the sales stream data with customer information. In our example,
it will be adding customer name to the sales stream.

{% highlight scala %}
val joinedDs = salesDs
      .join(customerDs, "customerId")
{% endhighlight%}

In above code, we use *join* API on dataset to achieve the enrichment. Here we can see how seamless it's to join stream data with batch data.

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/streaming/StreamJoin.scala).

## Running the example

### 1. Run socket 

{% highlight sh %}

nc -lk 50050

{% endhighlight %}

### 2. Enter Sales Data 

{% highlight text %}

111,1,1,100.0

{% endhighlight%}

### 3. Results

The result should look as below

{% highlight text %}

+----------+-------------+------+----------+------------+
|customerId|transactionId|itemId|amountPaid|customerName|
+----------+-------------+------+----------+------------+
|         1|          111|     1|     100.0|        John|
+----------+-------------+------+----------+------------+

{% endhighlight %}

## Conclusion

With unified dataset abstraction across batch and stream, we can seamlessly join stream data with batch data. This makes stream enrichment much simpler compared
to other stream processing systems.
