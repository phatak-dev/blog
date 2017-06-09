---
layout: post
title: "Migrating to Spark 2.0 - Part 6 : Spark ML Transformer API"
date : 2017-06-09
categories: scala spark spark-two-migration-series
---

Spark 2.0 brings a significant changes to abstractions and API's of spark platform. With performance boost, this version has made some of non backward compatible changes to the framework. To keep up to date with the latest updates, one need to migrate their spark 1.x code base to 2.x. In last few weeks, I was involved in migrating one of fairly large code base and found it quite involving process. In this series of posts, I will be documenting my experience of migration so it may help all the ones out there who are planning to do the same.

This is the fifth post in this series.In this post we will discuss about spark ml transformer API. You can access all the posts [here](/categories/spark-two-migration-series).

TL;DR You can access all the code on [github](https://github.com/phatak-dev/spark-two-migration).

## Machine Learning in Spark 2.0

In spark 1.x, MLLib was the library offered by spark for doing machine learning on large data. MLLib was based on rdd abstraction. So from 1.4, spark started to
move to new library called Spark ML which implements machine learning algorithms using dataframe abstraction. As spark ML is new library compared to MLLib it was not feature complete and was in beta through the 1.x series. So MLLib was de facto library for machine learning.

But from spark 2.0, spark ML becomes de facto machine library. mllib will be around for backward compatibility. So from 2.0, all the machine learning algorithms
will be using newer abstractions like dataframe/dataset rather than rdd.

## Transformer and Evaluator API

In spark ML, data preparation is represented using transformation API. The learning algorithm are represented using Evaluator API. These
two form the basis of the spark ML API's and abstractions. You can learn more about them in below link

[https://spark.apache.org/docs/latest/ml-pipeline.html#example-estimator-transformer-and-param](https://spark.apache.org/docs/latest/ml-pipeline.html#example-estimator-transformer-and-param)

## Changes to API's

In spark 1.x, transformer and evaluator API, used to operate on dataframe abstraction. But from spark 2.0, it's changed to use Dataset API. As Dataset abstraction
is more versatile than the dataframe this change makes library more powerful.

If you are using built in transformers or evaluators , then you don't have to worry. All code works without any change.

But if you have implemented you own custom transformer/ evaluator, then you need to change the code to accommodate this API change.


## Custom Transformer in 1.x

The below code shows a simple transformer in spark 1.x. It's an identity transformer. 

{% highlight scala %}
class IdentityTransformer(override val uid:String=Identifiable.randomUID("identity")) extends Transformer {
 override def transform(inputData:DataFrame):DataFrame = {
  inputData
 }

 override def copy(paramMap:ParamMap) = this
 override def transformSchema(schema:StructType)= schema
}
{% endhighlight %}

In the above code, you can observe *transform* method takes a dataframe and returns a dataframe.

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-one/src/main/scala/com/madhukaraphatak/spark/migration/sparkone/CustomMLTransformer.scala).

## Custom Transformer in 2.x

The below code shows modified version of the above transformer in 2.0

{% highlight scala %}

class IdentityTransformer(override val uid: String = Identifiable.randomUID("identity")) extends Transformer {
  override def transform(inputData: Dataset[_]): DataFrame = {
    inputData.toDF()
  }

  override def copy(paramMap: ParamMap) = this
  override def transformSchema(schema: StructType) = schema
}
{% endhighlight %}

As you can observe from the code, transform now takes *Dataset* rather than dataframe. One thing to note that return type is still 
remains to be dataframe.

Also first line transform, we are calling *toDF* on dataset so we can port code easily.

You can access complete code on [github](https://github.com/phatak-dev/spark-two-migration/blob/master/spark-two/src/main/scala/com/madhukaraphatak/spark/migration/sparktwo/CustomMLTransformer.scala).

## Advantage of Dataset over Dataframe.

The API change may seem small and unnecessary. But there is very valid reason to move from dataframe to Dataset abstraction for spark ml. 

In mllib API's, we were able to represent the data using case classes like *LabelPoint*. This made a compact way of representing data for given algorithm.
But with moving to dataframe abstraction we lost that option. As we can't represent custom structured data in dataframe, it's not possible to do
any validation at compile time. So we may be sending wrong dataframe to an algorithm which is expecting some other structure altogether. This made
code more complicated and difficult to validate.

Dataset alleviates all these problems. Dataset abstraction can represent custom structures very efficiently. As we represented in mllib  like RDD[LabelPoint]
we can now represent it as Dataset[LablePoint]. This makes code compile time safe.

You can read more in this [jira](https://issues.apache.org/jira/browse/SPARK-14500).

## Conclusion

In this post, we have discussed about the change to spark ML API's and how to migrate the code accommodate the same.
