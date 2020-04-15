---
layout: post
title: "Adaptive Query Execution in Spark 3.0 - Part 1 : Introduction"
date : 2020-04-15
categories: scala spark spark-three spark-aqe
---
Spark catalyst is one of the most important layer of spark SQL which does all the query optimisation. These optimisations are expressed as list of rules which will be executed on the query plan before executing the query itself. This makes sure Spark SQL can do lot more automatic optimisation compared to hand written RDD code.

Even though spark catalyst does lot of heavy lifting, it's all done before query execution. So that means once the physical plan is created and execution of the plan started, it will not do any optimisation there after. So it cannot do some of the optimisation which is based on metrics it sees when the execution is going on.

In 3.0, spark has introduced an additional layer of optimisation. This layer is known as adaptive query execution. This layer tries to optimise the queries depending upon the metrics that are collected as part of the execution.

In this series of posts, I will be discussing about different part of adaptive execution. This is the first post in the series where I will be discussing the goals of this layer. You can find all the posts in the series [here](/categories/spark-aqe).


## Adaptive Query Execution

Adaptive Query Execution, AQE,  is a layer on top of the spark catalyst which will modify the spark plan on the fly. This allows spark to do some of the things which are not possible to do in catalyst today.

The different optimisation available in AQE as below.

### Adaptive Number of Shuffle Partitions or Reducers

In spark sql, number of shuffle partitions are set using **spark.sql.shuffle.partitions** which defaults to **200**. In most of the cases, this number is too high for smaller data and too small for bigger data. Selecting right value becomes always tricky for the developer.

When AQE is enabled, the number of shuffle partitions are automatically adjusted depending upon the output of mapping stage. This means if map stage of the query outputted less data, spark will run less number of reducers and vice versa. This makes it performant depending upon data size.

### Handling Skew Joins

Today whenever there is skew in joins, spark creates a skewed task which lags compared to other tasks. This often result in slower execution times.

When adaptive execution is enabled, spark can recognise this query and then automatically redistribute the data to go join faster. This will make skew join go faster than normal joins.

### Converting Sort Merge Join to BroadCast Join

AQE converts sort-merge join to broadcast hash join when the runtime statistics of any join side is smaller than the broadcast hash join threshold. This allows spark to automatically adjust join type when the data may reduce when after filter etc.

The above are the some of the optimisation done by AQE.

## References

[https://github.com/apache/spark/pull/27616](https://github.com/apache/spark/pull/27616).

