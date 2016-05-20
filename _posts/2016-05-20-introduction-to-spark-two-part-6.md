---
layout: post
title: "Introduction to Spark 2.0 - Part 6 : Custom Optimizers in Spark SQL"
date : 2016-05-20
categories: scala spark spark-two
---
Spark 2.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 2.0.

This is the sixth blog in series, where I will be discussing about adding custom optimizers to spark sql catalyst optimizer. You can access all the posts in the series [here](/categories/spark-two/).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark2.0-examples).


## Catalyst optimizer

Spark SQL uses an optimizer called catalyst to optimize all the queries written both in spark sql and dataframe dsl. This optimizer makes queries run much faster than their RDD counterparts. Spark keeps on improving this optimizer every version in order to improve performance without changing user code.

Catalyst is a modular library which is build as a rule based system. Each rule in the the framework focuses on the specific optimization. For example, rule like *ConstantFolding* focuses on removing constant expression from the query. For more information catalyst, you can refer to my earlier talk on [anatomy of dataframe](/anatomy-of-spark-dataframe-api).

In earlier versions of spark, if we wanted add our own optimizations, we need to change the source code of spark. This is not preferable in many cases where optimizations are only applicable to the domain or user specific problems. So developer community wanted to have a pluggable way to add their optimizations to the catalyst in runtime.

In Spark 2.0, we have an experimental API for adding user defined custom optimizations. In the rest of the blog I will be discussing about how to write an optimization rule and add it to catalyst.


## Optimized plan for a dataframe

Before we write our optimization rule, let's understand how to access the optimized plan in spark. The below code shows a simple example

{% highlight scala %}

val df = sparkSession.read.option("header","true").csv("src/main/resources/sales.csv")
val multipliedDF = df.selectExpr("amountPaid * 1")
println(multipliedDF.queryExecution.optimizedPlan.numberedTreeString)
{%endhighlight %}

In above code, we have loaded a csv file and multiplied one to one of the column. We can look at the optimized plan for that dataframe using *optimizedPlan* object on queryExecution. *queryExecution* allows us to access all the information related execution of the query. Optimized plan is one of them.

Every plan in spark is represented as a tree. So *numberedTreeString* method pretty prints the optimized plan. When we run this code we get below result.

~~~
00 Project [(cast(amountPaid#3 as double) * 1.0) AS (amountPaid * 1)#5]
01 +- Relation[transactionId#0,customerId#1,itemId#2,amountPaid#3] csv
~~~

All plans are read bottom to top. The below are the two nodes of tree

* 01 Relation - Signifies the dataframe we created from csv file

* 00 Project - Signifies the projection

You can observe some of the casts added by the spark for correct results.

## Writing an optimizer rule

From the above plan, it's clear that its going to multiply 1.0 to each of the value of column. But it's not optimal plan. Whenever we see 1 in multiplication, we know it's going to return exact same value. We can use this knowledge to write a rule and add smartness to the optimizer.

The following code show how to write such a rule.

{% highlight scala %}

object MultiplyOptimizationRule extends Rule[LogicalPlan] {
    def apply(plan: LogicalPlan): LogicalPlan = plan transformAllExpressions {
      case Multiply(left,right) if right.isInstanceOf[Literal] &&
        right.asInstanceOf[Literal].value.asInstanceOf[Double] == 1.0 =>
        println("optimization of one applied")
        left
    }
  }
{% endhighlight %}


Here we are extending from Rule which operates on logical plan. Most of the rules are written as pattern matching in scala. In code, we are checking is the right operand is literal and it's value is 1.0. Here we are very specific about where value 1 should appear. If it appears on the left it will not optimize. As it's for example, for brevity I have not included checking for left also. But you can easily add.

So whenever we have right value as 1, we will skip the right expression altogether and return left.

## Integrating our optimizer rule

Once we have our rule, next step is to add to the optimizer. The below code shows that.

{% highlight scala %}
sparkSession.experimental.extraOptimizations = Seq(MultiplyOptimizationRule)
{% endhighlight %}

On spark session, we have an experimental object which exposes all the experimental API's. Using this API, you can add list of custom rules to catalyst with *extraOptimizations*.

## Using the custom optimization

Once we have our rule added, we need to check it is applied or not. We will do same manipulation again as below code.

{% highlight scala %}
val multipliedDFWithOptimization = df.selectExpr("amountPaid * 1")
println("after optimization")
println(multipliedDFWithOptimization.queryExecution.
optimizedPlan.numberedTreeString)
{% endhighlight %}

If we observe the output now,

~~~
00 Project [cast(amountPaid#3 as double) AS (amountPaid * 1)#7]
01 +- Relation[transactionId#0,customerId#1,itemId#2,amountPaid#3] csv
~~~

You can observe now that multiplication is gone. This denotes the our optimization is applied. You can access complete code [here](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/CustomOptimizationExample.scala).

In Spark 2.0 users can add their own custom rules to catalyst to optimize their code. This makes spark more developer friendly and powerful generic engine.

## References

Catalyst: Allow adding custom optimizers - [https://issues.apache.org/jira/browse/SPARK-9843](https://issues.apache.org/jira/browse/SPARK-9843)