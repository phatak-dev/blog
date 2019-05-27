---
layout: post
title: "Parallel Cross Validation in Spark"
date : 2019-05-27
categories: spark scala 
---
Cross validation in spark, is process of running a machine learning pipeline with different combinations of parameters to find the optimal model. Often this operation is costly as spark needs to go through many combinations of parameters on large size of data.


## Cross Validation before Spark 2.3

Till spark 2.3, cross validation was done serially. That means , for every combination of cross validation, spark runs one set of jobs before it moves on to next combination. 

The below code show one example of such cross validation.

{% highlight scala %}
val paramMap = new ParamGridBuilder()
  .addGrid(logisticRegression.maxIter, Array(1, 2, 3)).build()


val crossValidator = new CrossValidator()
crossValidator.setEstimator(pipeline)
crossValidator.setEvaluator(new BinaryClassificationEvaluator())
crossValidator.setEstimatorParamMaps(paramMap)

crossValidator.fit(salaryDf)


{% endhighlight %}

In above code, we are running an algorithm 3 times, logistic regression in this case ( not shown in the code), with different values of **maxIter**.


## Result of Serial Cross Validation

When we see the Spark UI, we can observe that logistic regression is running one after another. The below screenshots confirm the same.

![firstregression](/images/firstlogisticregression.png)
![secondregression](/images/secondlogisticregression.png)

As you can see from above images, two jobs have *one second* of gap.Also from the job number, we can make out that there were many jobs ran before second logistic regression started.


## Parallel Cross Validation in Spark 2.3

Every combination of cross validation are independent of each other. Which means we can run them parallely. This will increase resource usage, but it does get us results much faster.  


So from Spark 2.3 version, there is an option to specify parallelism in cross validation. The below codes shows the same

{% highlight scala %}

 val paramMap = new ParamGridBuilder()
  .addGrid(logisticRegression.maxIter, Array(1, 2, 3)).build()


val crossValidator = new CrossValidator()
crossValidator.setEstimator(pipeline)
crossValidator.setEvaluator(new BinaryClassificationEvaluator())
crossValidator.setEstimatorParamMaps(paramMap)
crossValidator.setParallelism(3)

crossValidator.fit(salaryDf)


{% endhighlight %}

In above code, we set the parallelism with *setParallelism* parameter. 


## Result of Parallel Cross Validation

When we observe the spark UI with cross validation enabled, we can see the below result.

![combinedregression](/images/combinedlogisticregression.png)
 

As you can observe from above screenshot, all the 3 logistic regression have started together.

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark2.0-examples/blob/master/src/main/scala/com/madhukaraphatak/examples/sparktwo/ml/ParallelCrossValidation.scala).
