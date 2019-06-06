---
layout: post
title: "Multiple Column Feature Transformations in Spark ML"
date : 2019-06-06
categories: spark scala 
---
Feature transformations are one of the most important and time consuming part of any machine learning pipeline. As number of columns increases, these transformations also grow with it. So having improvement in performance in these will improve the overall performance of the machine learning system.


## Spark ML Feature Transformations Before Spark 2.3

Till spark 2.3, most of the ML transformations supported single column at a time. 

The below code show one example of such transformation.

{% highlight scala %}
val singleColumnOneHotEncoder = new OneHotEncoder()
singleColumnOneHotEncoder.setInputCol("workclass_index")
singleColumnOneHotEncoder.setOutputCol("workclass_onehot")
{% endhighlight %}

In above code, we are one hot encoding one column. If we have 20 categorical columns, we need to create 20 one hot encoders, which will run one after another. This is highly time consuming.  


## Multi-Column ML Transformations from Spark 2.3

Combining multiple columns together for feature transformations improve the overall performance of the pipeline. So from 2.3, spark has started supporting multiple column transformations for few of the built in transformations.


So from Spark 2.3 version, we can do all the one hot encoding in one shot as shown in the below code

{% highlight scala %}
val singleOneHotEncoder = new OneHotEncoderEstimator()
singleOneHotEncoder.setInputCols(stringColumns.map(_ + "_index"))
singleOneHotEncoder.setOutputCols(outputColumns)
{% endhighlight %}

In above code, we can observe that **setInputCols** allows us to set multiple columns at same time. This make sure that we run all the one hot encoding for multiple columns together.


## Missing Transformations

In Spark 2.3 and 2.4 only few of the built-in transformations support the multiple columns. Few of the the missing ones are

• StringIndexer

• Binarizer

Support for these will be added in spark 3.0.

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/2.4/src/main/scala/com/madhukaraphatak/examples/sparktwo/ml/MultiColumnTransformation.scala).

## Conclusion
From Spark 2.3, spark supports multi-column feature transformations. These greatly speedup the real world ML pipelines.
