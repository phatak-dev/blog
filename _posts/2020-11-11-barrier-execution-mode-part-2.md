---
layout: post
title: "Barrier Execution Mode in Spark 3.0 - Part 2 : Barrier RDD"
date : 2020-11-11
categories: barrier-execution spark spark-three 
---
Barrier execution mode is a new execution mode added to spark in 3.0 version. This marks a significant change to platform which had only supported Map/Reduce based execution till now. This will allow spark to diversify the kind of workloads it can support on it's platform.

In this series posts we will discuss about this execution mode in detail.This is the second post in the series. In this post we will discuss the basic API for barrier execution mode. You can access all the posts in this series [here](/categories/barrier-execution).

## RDDBarrier

Spark 3.0 has introduced a new type of RDD called **RDDBarrier[T]** which signifies the RDD needs to be handled using the barrier execution mode. This RDD exposes new capabilities that are not available in normal RDD.

## Creating a RDDBarrier Rdd

The below code shows how to convert a normal RDD to barrier RDD

{%highlight scala %}

val df = sparkSession.range(0,100).repartition(4)

val barrierRdd = df.rdd.barrier()

{% endhighlight %}

In above code, we create a dataframe with 4 partitions. Then we are using **barrier()** method to convert the normal RDD to barrier RDD.

## mapPartitions method

Once barrier rdd, it exposes a **mapPartitions** function to run custom code for each of the partition. In this simple example, we will not do much.

{% highlight scala %}


val count = barrierRdd.mapPartitions(v => v).count()

println("count is " + count)

{% endhighlight %}

**mapPartitions** function return a normal RDD on which we can call methods like count.

## Executing code with Single Thread

Let's run the above code with below spark session

{% highlight scala %}

val sparkSession = SparkSession.builder.
          master("local")
          .appName("example")
          .getOrCreate()

{% endhighlight %}

In above code, we are running the program in which master which has single thread. When you run the code, you will see below warnings

{% highlight text %}

20/11/09 16:57:40 WARN DAGScheduler: Barrier stage in job 0 requires 4 slots, but only 1 are available. Will retry up to 40 more times
20/11/09 16:57:55 WARN DAGScheduler: Barrier stage in job 0 requires 4 slots, but only 1 are available. Will retry up to 39 more times

{% endhighlight %}

The above code is not executing. The reasons for this is as we discussed in last post, all the tasks in barrier execution mode start together. In our example, we have four partitions which need four threads but we have just one. If this was normal Map/Reduce execution, it would have ran one after another. But in barrier execution mode it doesn't work like that.

## Executing code with Multiple Threads

We can fix above code using below spark session

{% highlight scala %}

val sparkSession = SparkSession.builder.
          master("local[4]")
          .appName("example")
          .getOrCreate()

{% endhighlight %}

Now we have four threads. When you execute the code now it works fine.


## Code

[Execution with Single Thread](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/barrier/BarrierExceptionExample.scala).

[Execution with Multiple Threads](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/barrier/BarrierRddExample.scala).

## Conclusion

RDDBarrier is a new RDD added to the spark API to support the barrier execution mode. Using this RDD we can implement all different capabilities of this mode.
