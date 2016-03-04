---
layout: post
title: "Introduction to Spark 2.0 : A Sneak Peek At Next Generation Spark"
date : 2016-03-04
categories: scala spark
---
Spark 2.0 is the next stable of release of spark, which is expected to be released in April/May 2016. As the major version bump suggests, its is going to be bring some drastic changes to framework. In recent [spark summit](https://spark-summit.org/east-2016/), spark contributors discussed some of those in various talks. 

In this post, I am going to talk about some of the important changes made to the framework and as a spark developer how we can prepare for it.


## 1. Embrace Dataset/ Dataframe API over RDD API

Normally, whoever starts learning spark first learns about RDD abstraction. RDD was one of the novel idea of Spark which gave us a single abstraction over different big data workloads like batch, streaming, ML etc. So naturally RDD API became the way people build spark applications.

But overtime, people have realized RDD as a user facing API held back spark runtime from advanced optimizations. So from Spark 1.3, Dataframe API was introduced to solve some of the optimization issues. Dataframe brought custom memory management and runtime code generation which greatly improved performance. So in last year most of the improvements went into Dataframe API whereas RDD API stood still.

Though dataframe API solved many issues, it was not a good enough replacement for RDD API. One of the major issues with dataframe API was no compile time safety and not able to work with domain objects. So this held back people using dataframe API everywhere. But with introduction of Dataset API in 1.6, we were able to fill the gap.

So in Spark 2.0, Dataset API will be become a stable API. So Dataset API combined with Dataframe API should able to cover most of the use cases where RDD was used earlier. So as a spark developer it is advised to start embracing these two API's over RDD API from Spark 2.0.

Does that mean RDD API will be removed? Not really. Spark as a project is very serious about backward compatibility. So they don't want to remove any stable API's. So RDD API will remain as low level API mostly used by runtime. As developer you will be using Dataset or Dataframe API from Spark 2.0.

Everything above sounds great for batch processing, but what about Spark streaming? Spark streaming has lot of API's around RDD. What will happen to them?

## 2. Structured Stream processing

Spark 2.0 will introduce structured stream processing, which is a higher level API to do stream processing. It essentially going to leverage dataframe and dataset API for stream processing which will get rid of using RDD as an abstraction. Not only this bring dataframes to stream processing, it brings bunch of other benefits like datasource API support for spark streaming. You can watch [this video](https://www.youtube.com/watch?v=i7l3JQRx7Qw) to know more.

So from Spark 2.0, you will be interacting with spark streaming using same DF abstraction that you were using in batch layer.

## 3. Dataset is the new single abstraction

Spark always loved to have a single abstraction which it made it to improve in a rapid phase. Also it meant that different libraries can exchange data between each other. So doesn't having Dataframe and Dataset as two API's beats that single abstraction idea?

Currently in Spark 1.6, these are two independent abstractions. But from 2.0 Dataframe will be a special case of Dataset API. So this make dataset as a single abstraction on which all the API's are build. So Dataset will be new single abstraction which will take the place of RDD in the user API world.

## 4. Performance improvements

Spark 2.0 going to bring many performance improvements thanks to tungsten and code generation. It's been one of the constant theme in last few releases to going 1-2x performance gains. But in 2.0, data bricks is promising 6-10x performance gains. It's particularly do with more and more intelligent code generation and moving libraries on better abstraction layers like dataset.

So the above are the most important updates landing in Spark 2.0. I will be discussing more about this as and when we get to see these changes getting implemented. 

Source : You can access all the talks and slides of spark summit from [here](https://spark-summit.org/east-2016/schedule/).







