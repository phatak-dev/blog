---
layout: post
title: "Dynamic Shuffle Partitions in Spark SQL"
date : 2019-11-26
categories: spark  scala
---
In Apache Spark, shuffle is one of costliest operation. Effective parallelising of this operation gives good performing for spark jobs.

## Shuffle Partitions in Spark SQL

Shuffle partitions are the partitions in spark dataframe, which is created using a grouped or join operation. Number of partitions in this dataframe is different than the original dataframe partitions. 

For example, the below code

{% highlight scala %}

val df = sparkSession.read.csv("src/main/resources/sales.csv")
println(df.rdd.partitions.length)

{% endhighlight %}

will print **2** for small sales file. This indicates there are two partitions in the dataframe. 

Now when we run the groupby operation on the same, the number of partitions will change

{% highlight scala %}

println(df.groupBy("_c0").count().rdd.partitions.length)

{% endhighlight %}

The above code prints **200**. The *2* partition increased to **200**. 

This is because the parameter **spark.sql.shuffle.partitions** which controls number of shuffle partitions is set to **200** by default.

## Challenges with Default Shuffle Partitions

The number of shuffle partitions in spark is static. It doesn't change with different data size. This will lead into below issues

* For smaller data, 200 is a overkill which often leads to slower processing because of scheduling overheads.

* For large data, 200 is small and doesn't effectively use the all resources in the cluster.

To over come the issues mentioned above, we need to control shuffle partitions dynamically.

## Dynamically Setting the Shuffle Partitions

Spark allows changing the configuration of spark sql using *conf* method on the *sparkSession*. Using this method, we can set wide variety of configurations dynamically. 

So if we need to reduce the number of shuffle partitions for a given dataset, we can do that by below code

{% highlight scala %}

sparkSession.conf.set("spark.sql.shuffle.partitions",100)
println(df.groupBy("_c0").count().rdd.partitions.length)

{% endhighlight %}

The above code will print **100**. This shows how to set the number partitions dynamically.

The exact logic for coming up with number of shuffle partitions depends on actual analysis. You can typically set it to be 1.5 or 2 times of the initial partitions. 
