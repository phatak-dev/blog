---
layout: post
title: "Introduction to Apache Flink for Spark Developers : Flink vs Spark"
date : 2015-12-06
categories: spark flink flink-series
---

<style type="text/css">
.post-content blockquote {
    color: #A50707;
    font: bold;
    font-size: 20px;
    border-left: none;
}
</style>


Does world need yet another big data processing system? That was the question popped up when I first heard of the Apache Flink. In big data space we don't have dearth of frameworks. But we do have shortcoming of cohesive platform which can solve all our different data processing needs. Apache spark seems to be the best framework in town which is trying to solve that problem. So I was skeptic about need of yet another framework which has similar goals.

In last few weeks I started spending some time on flink out of curiosity. Initially when I looked at the standard examples they looked very similar to one of the Spark. So I started with the impression that its just another framework which is mimicking the functionality of the spark. But as I spent more and more time, it was apparent that, there are  few novel ideas behind those same look API's which makes flink stand apart from spark. I got fascinated by those ideas and spent more and more and time understanding and exploring those.



> Many of the flink ideas like custom memory management, dataset API are already finding their home in Spark which proves that those ideas are really good. So understanding flink may help us to understand what's going to be the future of the distributed data processing.


In this post I am tried put together my first impressions of Apache flink as a spark developer. This rant/review is heavily biased as I spent my last two years in Spark and just 2-3 weeks playing with Apache flink. So take all the things I say here with grain of salt.

## What is Apache Flink?

Apache Flink is yet another new generation general big data processing engine which targets to unify different data loads. Does it sounds like Apache Spark? Exactly. Flink is trying to address same issue that Spark trying to solve. Both systems are targeted towards building the single platform where you can run batch, streaming, interactive , graph processing , ML etc. So flink does not differ much from  Spark interms of ideology. But they do differ a lot in the implementation details.

So in the following section I will be comparing different aspects of the spark and flink. Some of the approaches are same in both frameworks and some differ a lot.


## Apache Spark vs Apache Flink 


### 1. Abstraction

In Spark, for batch we have **RDD** abstraction and **DStream** for streaming which is internally RDD itself. So all the data we represent in Spark underneath represented using RDD abstraction.

In flink, we have **Dataset** abstraction for batch and **DataStreams** for the streaming application. They sound very similar to RDD and DStreams but they are not. The differences are

   *  **Dataset are represented as plans in runtime**

   In spark RDD are represented as java objects in the runtime. With introduction of Tungsten, it is changed little bit. But in Apache flink Dataset is represented as a logical plan. Does it sound familiar? Yes they are like dataframes in Spark. So in flink you get Dataframe like api as first class citizen which are optimized using an optimizer. But in Spark RDD don't do any optimization in between.

   > Dataset of flink  are like Dataframe API of spark which are optimized before executed.

   In spark 1.6, dataset API is getting added to spark, which may eventually replace RDD abstraction.

   * **Dataset and DataStream are independent API's**

In Spark all the different abstractions like DStream, Dataframe  are built on top of RDD abstraction. But in flink, Dataset and DataStream are two independent abstractions built on top common engine. Though they mimic the similar API, you cannot combine those together as you can do in case of DStream and RDD. Though there are [some efforts](https://issues.apache.org/jira/browse/FLINK-2320) in this direction, there is not enough clarity what will be the end result.

> We cannot combine DataSet and DataStreams like RDD and DStreams.

So though both flink and spark have similar abstractions, their implementation differs.

## Memory management

 Till spark 1.5, Spark used Java heap for caching data. Though it was easier for project to start with, it resulted in OOM issues and gc pauses. So from 1.5, spark moved into custom memory management which is called as project tungsten.

 Flink did custom memory management from day one. Actually it was one of the inspiration for Spark to move in that direction. Not only flink stores data in it's custom binary layout, it does operate on binary data directly. In spark all dataframe operations are operated directly on tungsten binary data from 1.5.

Doing custom memory management on JVM result in better performance and better resource utilization.

## Language of implementation.

Spark is implemented in Scala. It provides API's in other languages like Java,Python and R.

Flink is implemented in Java. It does provide Scala API too. 

So language of choice is better in Spark compared to flink. Also in some of the scala API's of flink, the java abstractions does API's. I think this will improve as they get more users for scala API. I am not much aware of Java API's both in Spark and Flink as I moved to Scala long back.

## API

Both Spark and Flink mimic scala collection API's. So from surface both API's look very similar. Following is the scala word count using RDD and Dataset API.


{% highlight scala %}

// Spark wordcount
object WordCount {

  def main(args: Array[String]) {

    val env = new SparkContext("local","wordCount")

    val data = List("hi","how are you","hi")

    val dataSet = env.parallelize(data)

    val words = dataSet.flatMap(value => value.split("\\s+"))

    val mappedWords = words.map(value => (value,1))

    val sum = mappedWords.reduceByKey(_+_)

    println(sum.collect())

  }

}
{%endhighlight%}

{% highlight scala %}


// Flink wordcount
object WordCount {

  def main(args: Array[String]) {

    val env = ExecutionEnvironment.getExecutionEnvironment

    val data = List("hi","how are you","hi")

    val dataSet = env.fromCollection(data)

    val words = dataSet.flatMap(value => value.split("\\s+"))

    val mappedWords = words.map(value => (value,1))

    val grouped = mappedWords.groupBy(0)

    val sum = grouped.sum(1)

    println(sum.collect())
  }

}

{%endhighlight%}


Though I am not sure, is this coincidence or deliberate, having very similar API
s does help to switch between these frameworks very easily. It seems that the collection API going to be the standard API to do data pipeline in near future. Even Martin Odersky, creator of Scala, [acknowledges](https://www.youtube.com/watch?v=NW5h8d_ZyOs) this fact.



## Streaming

Apache Spark looks at streaming as fast batch processing. Where as Apache flink looks at batch processing as the special case of stream processing. Both of these approaches have fascinating implications. The some of the differences or implications of the two different approaches are

* **Realtime vs Near Realtime**

Apache flink provides event level processing which is also known as real time streaming. It's very similar to the Storm model. 

In case of Spark, you get mini batches which doesn't provide event level granularity. This approach is known as near real-time.


> Spark streaming is faster batch processing and Flink batch processing is bounded streaming processing.


Though most of the applications are ok with near realtime, there are few applications who need event level realtime processing. These applications normally storm rather than Spark streaming. For them flink going to be very interesting alternative. 

* **Ability to combine the historical data with stream**

One of the advantage of running streaming processing as faster batch is, then we can use same abstractions in the both cases. Spark has excellent support for combining batch and stream data because both underneath are using rdd abstraction.

In case of flink, batch and streaming don't share same api abstractions. So though there are ways to combine historical file based data with stream it is not that clean as Spark.

In many application this ability is very important. In these applications Spark shines in place of Flink streaming.

* **Flexible windowing**

Due to nature of mini batches, support for windowing is very limited in Spark as of now. Only you can window the batches based on the process time.

Flink provides very flexible windowing system compared to any other system out there. Window is one of the major focus of the flink streaming API's. It allows window based on process time, data time, no of records etc etc. This flexibility makes flink streaming API very powerful compared to spark ones. 



I am not sure how easy to bring those API's to Spark, so till that time flink has superior window API compared to the Spark streaming.


## SQL interface

One of the most active Spark library as of now is spark-sql. Spark provided both Hive like query language and Dataframe like DSL for querying structured data. It is matured API and getting used extensively both in batch and soon to be in streaming world.

As of now, Flink Table API only supports dataframe like DSL and it's still in beta. There are plans to add the sql interface but not sure when it will land in framework.

So as of now Spark has good sql story compared to flink. I think flink will catch up as it was late into the game compared to Spark.

## Data source Integration

Spark data source API is one the best API's in the framework. The data source API made all the smart sources like NoSQL databases, parquet , ORC as the first class citizens on spark. Also this API provides the ability to do advanced operations like predicate push down in the source level.

Flink still relies heavily upon the map/reduce InputFormat to do the data source integration. Though it
s good enough API to pull the data it's can't make use of source abilities smartly. So flink lags behind the data source integration as of now.

## Iterative processing

One of the most talked feature of Spark is ability to do machine learning effectively. With in memory caching and other implementation details its really powerful platform to implement ML algorithms.

Though ML algorithm is a cyclic data flow it's represented as direct acyclic graph inside the spark. Normally no distributed processing systems encourage having cyclic data flow as they become tricky to reason about.

But flink takes little bit different approach to others. They support controlled cyclic dependency graph in runtime. This makes them to represent the ML algorithms in a very efficient way compared to DAG representation. So the Flink supports the iterations in native platform which results in superior scalability and performance compared to DAG approach.

I hope spark also start supporting this in framework which will benefit the ML community immensely.

## Stream as platform vs Batch as Platform

Apache Spark comes from the era of Map/Reduce which represents whole computation as the movement of the data as collections of the files. These files may be sitting in memory as arrays or physical files on the disk. This has very nice properties like fault tolerance etc.

But Flink is new kind of systems which represents the whole computation as the stream processing where data is moved contentiously without any barriers. This idea is very similar to new reactive streams systems like akka-streams. 

Though with my limited research it's not very apparent that which one is the future of big data systems, doing everything as stream seems to picking up these days. So in that sense flink breathes a fresh air into way we think about big data systems.

## Maturity

After knowing all the differences, one question you may ask is Flink production ready like Spark? I argue it's not fully ready. There are parts like batch which already in production, but other pieces like streaming , table API are still getting evolved. It's not saying that people are not using flink streaming in production. There are some brave hearts out there who are doing that. But as mass market tool its need to be matured and stabilized over course of time.


## Conclusion

At this point of time Spark is much mature and complete framework compared to Flink. But flink does bring very interesting ideas like custom memory management, data set API etc to the table. Spark community is recognizing it and adopting these ideas into spark. So in that sense flink is taking big data processing to next level altogether. So knowing flink API and internals will help you to understand this new stream paradigm shift much before it lands in Spark.













