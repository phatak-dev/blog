---
layout: post
title: "Introduction to Spark 3.0 - Part 4 : Handling Class Imbalance Using Weights"
date : 2020-04-02
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.


This is the forth post in the series where I am going to talk about handling class imbalance using weights. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).

## Class Imbalance in Credit Card Fraud Data

I have discussed in detail about what is class imbalance and how to handle it using undersampling in [these posts](/categories/class-imbalance/). Please read the same before proceeding. 

## Challenges with Sampling

In earlier post, I had recommended doing undersampling as the resolution to handle class imbalance. Even though it works, it's very costly to do on a large amount of data. This often involves lots of shuffling and has a big impact on overall performance.

So is there any alternative way to handle the imbalance? That's what we are going to discuss in this post.


## Class Weights

We can handle class imbalance  by giving weights to the classes. By default a machine algorithm treats each class as equal. By giving different weights make it handle them differently. We can use this weights to handle the imbalance.

The class weight feature was only available for **Logistic Regression from 1.6 version**. But from 3.0, this feature has been made available to all classification models. So from 3.0 version we can use this feature with any classification algorithm to handle imbalance.


## Handling Class Imbalance using Class Weights

This section of the post will discuss how to apply the same for credit card fraud data.


### 1. Calculate the Ratio

To give the class weights, we need to calculate the ratio of fraud observations to total observations

The below code calculates the same

{% highlight scala %}
def getRatio(df:DataFrame) = {
    val fraudDf = df.filter("Class=1.0")
    val sampleRatio = fraudDf.count().toDouble / df.count().toDouble
    sampleRatio
  }
{% endhighlight %}


### 2.Calculate the Weight

Once we have calculated the  ratio, we can calculate the weight of each label as below

{% highlight scala %}

val ratioOfFraud = getRatio(df)
val fraudWeight  = 1 - ratioOfFraud
val nonFraudWeight = ratioOfFraud

{% endhighlight %}

### 3. Add Weight Column to DataFrame

Once we have calculated the weight, we need to add this as a column to dataframe. As it's a row level operation, no shuffle will be performed.

{% highlight scala %}

val weightedDF = df.withColumn("weight",
      when(df.col("Class").===("1.0"),fraudWeight)
     .otherwise(nonFraudWeight))

{% endhighlight %}


### 4. Enable Weight Column in Algorithm

Once we have added the weight column, we need to specify it in the algorithm also.

{% highlight scala %}

logisticRegression.setWeightCol("weight")

{% endhighlight %}


## Results

Once we run with above modification, we can observe the below results.

{% highlight text %}
for imbalanced data
test accuracy with pipeline 0.9992379567862879
test recall for 1.0 is 0.6206896551724138
for balanced data
test accuracy with pipeline 0.977155091285526
test recall for 1.0 is 0.8910256410256411
{% endhighlight %}

As you can observe from the above output, recall is improved for the balanced data.

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/ml/WeightedLogisticRegression.scala).

## References

[https://issues.apache.org/jira/browse/SPARK-9610](https://issues.apache.org/jira/browse/SPARK-9610).

