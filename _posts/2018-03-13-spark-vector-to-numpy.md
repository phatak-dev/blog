---
layout: post
title: "Converting Spark ML Vector to Numpy Array"
date : 2018-03-13
categories: scala spark python
---

Pyspark is a python interface for the spark API. One of the advantage of using it over Scala API is ability to use rich data science ecosystem of the python. Spark Dataframe can be easily converted to python Panda's dataframe which allows us to use various python libraries like scikit-learn etc. 

One of challenge with this integration is impedance mismatch between spark data representation vs python data representation. For example, in python ecosystem, we typically use Numpy arrays for representing data for machine learning algorithms, where as in spark has it's own sparse and dense vector representation.

So in this post we will discuss how this data representation mismatch is an issue and how to handle it.

## Spark Dataframe with Sparse Vector

Spark ML represents the feature vector as a sparse vector. So in this section, we will load sample adult salary data take from [here](https://archive.ics.uci.edu/ml/datasets/adult) and create spark dataframe. 

* ### Load Data as Spark Dataframe

{% highlight python %}

from pyspark.sql import SparkSession
import pandas as pd
import numpy as np

sparkSession = SparkSession.builder \
       .master("local") \
       .appName("Spark ML") \
       .getOrCreate()

sparkDf = sparkSession.read.format("csv").option("header","true"). \
option("inferSchema","true").load("adult_salary_100rows.csv")

{% endhighlight %}

* ### Spark ML Pipeline 

In spark ML, we use pipeline API's to build data processing pipeline. As part of pipeline, we pre process the data.

For our analysis we will be using salary column as label. age and workclass as input features. As *salary* and *workclass* are string column we need to convert them to one hot encoded values. The below code does it using spark pipeline.

{% highlight python %}

# Create String Indexer for workclass and salary
from pyspark.ml.feature import StringIndexer,VectorAssembler,OneHotEncoder
from pyspark.ml import Pipeline

workClassIndexer = StringIndexer()
	.setInputCol("workclass")
	.setOutputCol("workclass_indexed")

workClassOneHot =  OneHotEncoder().
	setInputCol("workclass_indexed")
	.setOutputCol("workclass_onehot")

salaryIndexer = StringIndexer()
	.setInputCol("salary")
	.setOutputCol("label")

vectorAssembler = VectorAssembler()
	.setInputCols(['workclass_onehot','age'])
	.setOutputCol("features")
# create pipeline
pipeline = Pipeline().setStages([workClassIndexer,workClassOneHot,
salaryIndexer,vectorAssembler])

{% endhighlight %}

In above code, we used vector assembler to convert multiple columns into single features array.

* ### Transform

Once we have the pipeline, we can use it to transform our input dataframe to desired form.

{% highlight python %}

transformedDf = pipeline.fit(sparkDf).transform(sparkDf).select("features","label")
transformedDf.printSchema()
{% endhighlight %}

Output of the schema will looks as below 

{% highlight text %}
root
 |-- features: vector (nullable = true)
 |-- label: double (nullable = true)

{% endhighlight %}

From above result, you can observe that feature array is represented as a vector. Also if you look at the data inside , it will look as below

{% highlight text %}

+--------------------+-----+
|            features|label|
+--------------------+-----+
|(7,[5,6],[1.0,39.0])|  0.0|
|(7,[1,6],[1.0,50.0])|  0.0|
+--------------------+-----+

{% endhighlight %}

The structure of features indicate it's a sparse vector.

## Converting to Panda's dataframe

Now we have data preprocessed. In order to use scikit-learn algorithms, we need to convert the dataframe into panda's one.

{% highlight python %}

pandaDf = transformedDf.toPandas()

{% endhighlight %}

Let's print schema of panda's dataframe.

{% highlight python %}

pandaDf.info()

{% endhighlight %}

{% highlight text %}

<class 'pandas.core.frame.DataFrame'>
RangeIndex: 101 entries, 0 to 100
Data columns (total 2 columns):
features    101 non-null object
label       101 non-null float64
dtypes: float64(1), object(1)

{% endhighlight%}

From the above output, we can observe that features is represented as an object rather than as sequence.


## Impedance Mismatch 

Let's try to train logistic regression on data.

{% highlight scala %}

from sklearn.linear_model import LogisticRegression
X = pandaDf['features']
y = pandaDf['label'].values.reshape(-1,1)

lr = LogisticRegression()
lr.fit(X,y)

{% endhighlight %}

The above code fails with below error

**setting an array element with a sequence.**

The error message is little cryptic. Let's see what's going on


## Features Representation

Let's see how feature array is represented. The below code show the same 

{% highlight python %}
map(lambda x : x,pandaDf['features'].iloc[0:1])

{% endhighlight %}

{% highlight text %}
[SparseVector(7, {5: 1.0, 6: 39.0})]
{% endhighlight %}

From the output, we can observe that it's represented as a python object named *SparseVector*. This is why in the panda's dataframe info it was shown as object.

## Converting to NumPy Array

As we cannot directly use Sparse Vector with scikit-learn, we need to convert the sparse vector to a numpy data structure. In our example, we need a two dimensional numpy array which represents the features data.

The below are the steps

* ### Convert Sparse Vector to Matrix

{% highlight python %}
series = pandaDf['features'].apply(lambda x : np.array(x.toArray())).as_matrix().reshape(-1,1)
{% endhighlight %}

In above code, we convert sparse vector to a python array by calling *toArray* method. Then we use numpy *as_matrix* method to convert to the two dimensional arrays. 

If you observe the shape of series, it looks as below

{% highlight text %}

array([[array([ 0.,  0.,  0.,  0.,  0.,  1., 39.])]

{% endhighlight %}

From the result, it can be seen that there three dimensional array , where as we only need two-dimensional. This is happening because when we call apply and if it returns a sequence, python treat it as single value.

* ### Flatten using apply_along_axis

{% highlight python %}

features = np.apply_along_axis(lambda x : x[0], 1, series)

{% endhighlight %}

In above code, we are flattening the innermost array. The result looks as below

{% highlight text %}

array([[ 0.,  0.,  0.,  0.,  0.,  1., 39.]
{% endhighlight %}

Now we got two dimensional array as we needed.

## Scikit Learn Logistic Regression

Once we have our data in right shape, we can apply scikit-learn algorithm as below.

{% highlight python %}

lr.fit(features,y)
{% endhighlight %}

You can access complete code on [github](https://github.com/phatak-dev/blog/blob/master/code/python/vectortonumpy.py).

## Conclusion

In this post, we discussed how to integrate between spark ML data structures to python libraries like scikit-learn.
