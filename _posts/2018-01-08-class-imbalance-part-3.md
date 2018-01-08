---
layout: post
title: "Class Imbalance in Credit Card Fraud Detection - Part 3 : Undersampling in Spark"
date : 2018-01-08
categories: scala spark datascience class-imbalance python
---
Whenever we do classification in ML, we often assume that target label is evenly distributed in our dataset. This helps the training algorithm to learn the features as we have enough examples for all the different cases. For example, in learning a spam filter, we should have good amount of data which corresponds to emails which are spam and non spam.

This even distribution is not always possible. Let’s take an example of fraud detection. Fraud detection is a use case, where by looking at transaction we need to decide is the transaction is fraudulent or not. In majority of the cases, the transaction will be normal. So the data for fraudulent data is very small compared to normal ones. In these cases, there will be imbalance in target labels. This will effect the quality of models we can build.So in next series of posts we will discuss about what’s class imbalance and how to handle it in python and spark.

This is the third post in the series where we discuss about implementing undersampling technique in spark. You can read all the posts in the series [here](/categories/class-imbalance).

## Spark Dataframe vs Panda Dataframe

In last [post](/class-imbalance-part-2) we discussed about how to implement undersampling using panda dataframe. In that code, we used indexes to extract random sample of the non fraud records. 

But spark dataframe doesn't have concept of indexes. As spark dataframe is based on distributed RDD's, so maintaining order of rows is tricky. So by default, spark dataframe doesn't preserve any indexes or ordering information. If you need it, you need to explicitly sort it.

So to implement the undersampling in spark, rather than using index technique, we will use sample and union API's. The below section will explain the code to the same.

## Undersampling in Spark

The below is the code to do the undersampling in spark

### 1. Find records which are Fraud

{% highlight scala %}
val fraudDf = df.filter("Class=1.0")
{% endhighlight %}

### 2. Find records which are non Fraud

{% highlight scala %}
val nonFraudDf = df.filter("Class=0.0")
{% endhighlight %}

### 3. Random sample non fraud records

{% highlight scala %}
val sampleRatio = fraudDf.count().toDouble / df.count().toDouble
val nonFraudSampleDf = nonFraudDf.sample(false, sampleRatio)
{% endhighlight %}

### 4. Union sample non fraud with fraud ones

{% highlight scala %}
fraudDf.unionAll(nonFraudSampleDf)
{% endhighlight %}


## Running Logistic Regression on Undersampled Data

Once we have undersampled data, we need to train on that.

### Training

The below code runs logistic regression on undersampled data. The below code spark ML pipeline API's.

{% highlight scala %}

val amountVectorAssembler = new VectorAssembler().setInputCols(Array("Amount")).setOutputCol("Amount_vector")
val standarScaler = new StandardScaler().setInputCol("Amount_vector").setOutputCol("Amount_scaled")
val dropColumns = Array("Time","Amount","Class")

val cols = df.columns.filter( column => !dropColumns.contains(column)) ++ Array("Amount_scaled")
val vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")

// pipeline
val logisticRegression = new LogisticRegression().setLabelCol("Class")
val trainPipeline = new Pipeline().setStages(Array(amountVectorAssembler,
standarScaler,vectorAssembler,logisticRegression))

val underSampledDf = underSampleDf(df)
println("for balanced data")
val balancedModel = runPipeline(trainPipeline, underSampledDf)

{% endhighlight %}

You can access complete code on [github](https://github.com/phatak-dev/spark-ml-kaggle/blob/master/src/main/scala/com/madhukaraphatak/spark/ml/UnderSampling.scala).

### Results

Once we run trained the model, we can verify the model using accuracy and recall scores.

{% highlight scala %}
println("test accuracy with pipeline " + accuracyScore(model.transform(df), "Class", "prediction"))
println("test recall for 1.0 is " + recall(model.transform(df), "Class", "prediction", 1.0))
{% endhighlight %}

The result is 

{% highlight text %}
for balanced data
test accuracy with pipeline 0.9363957597173145
test recall for 1.0 is 0.9444444444444444
{% endhighlight %}

As you can observe from the result, our recall has improved a lot. It was 61% percent when data was unbalanced but now it's 93%. This means our model is pretty good identifying the fraud.

Accuracy score has gone down because we undersampled data. This is fine in our case because if we miss classify some non-fraud transactions as fraud it doesn't do any harm.

## Generalisation

Whenever we undersample data, the training data size reduces significantly. So even though model works well for balanced data, we need to make sure does it generalise well. As we did with python code,the below code calculates score for full data using above model

{% highlight scala %}

println("balanced model for full data")
printScores(balancedModel, df)

{% endhighlight %}

The result is

{% highlight text %}
balanced model for full data
test accuracy with pipeline 0.9439304511476193
test recall for 1.0 is 0.9410569105691057
{% endhighlight %}

As you can observe from the results, accuracy score is still good for when we predict for unbalanced data. This makes sure that our model generalises well even if it's trained on undersample data.


## Conclusion

In this post we understood how to implement undersampling technique in spark.

