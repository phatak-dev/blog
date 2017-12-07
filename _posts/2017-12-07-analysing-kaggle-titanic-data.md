---
layout: post
title: "Analysing Kaggle Titanic Survival Data using Spark ML" 
date : 2017-12-07
categories: scala spark datascience kaggle
---
Kaggle is one of the biggest data and code repository for data science. Recently I have started learning various python data science tools like scikit-learn,tensorflow, etc. As to practice these tools, I have started exploring the kaggle datasets. In kaggle, you will find many notebooks(kernels) which explore the data in various ways. Most of them are written in python and R.

Kaggle has a introductory dataset called titanic survivor dataset for learning basics of machine learning process. In this post, I have taken some of the ideas to analyse this dataset from kaggle kernels and implemented using spark ml. So as part of the analysis, I will be discussing about preprocessing the data, handling null values and running cross validation to get optimal performance.

## Titanic Survivor Dataset

Titanic survivor dataset captures the various details of people who survived or not survived in the shipwreck. Using this data, you need to build a model which predicts probability of someone's survival based on attributes like sex, cabin etc. It's a classification problem.

You can learn more about the dataset at [kaggle](https://www.kaggle.com/c/titanic).

The rest of the post will discuss various steps to build the model.

TL;DR You can access all the code on [github](https://github.com/phatak-dev/spark-ml-kaggle).


## 1. Preprocessing

First part of data analysis to load data and pre-process to fit to machine learning.

### 1.1 Loading CSV data

First we load the data using spark data source API.

{% highlight scala %}
val df = sparkSession.read.option("header", "true").option("inferSchema", "true").csv("src/main/resources/titanic/train.csv") 
df.printSchema()
{% endhighlight %}

The below is the schema of data 

{% highlight text %}

root
 |-- PassengerId: integer (nullable = true)
 |-- Survived: integer (nullable = true)
 |-- Pclass: integer (nullable = true)
 |-- Name: string (nullable = true)
 |-- Sex: string (nullable = true)
 |-- Age: double (nullable = true)
 |-- SibSp: integer (nullable = true)
 |-- Parch: integer (nullable = true)
 |-- Ticket: string (nullable = true)
 |-- Fare: double (nullable = true)
 |-- Cabin: string (nullable = true)
 |-- Embarked: string (nullable = true)


{% endhighlight %}

The *Survived* column is the target column.

### 1.2  Handling Missing Values
In many data science cases, we need to handle missing values. These are the values which are not observed or not present due to issue in data capturing process.

In our dataset, *Age* of some of the passengers is not known. So we use *na.fill* API to fill the mean age for all the missing age values.

{% highlight scala %}
val meanValue = df.agg(mean(df("Age"))).first.getDouble(0)
val fixedDf = df.na.fill(meanValue, Array("Age"))
{% endhighlight %}

### 1.3 Split Data for Train and Holdout

One we prepared our data, we split the data for training and hold out. We use spark's *randomSplit* method to do the same.

{% highlight scala %}
val dfs = fixedDf.randomSplit(Array(0.7, 0.3))
val trainDf = dfs(0).withColumnRenamed("Survived", "label")
val crossDf = dfs(1)
{% endhighlight %}

### 1.4 Handling Categorical Variables

In our dataset, many columns like *Sex*,*Embarked* are categorical variables. So we are one-hot encoding them using spark ML pipeline API's.
In this example, we are using *StringIndexer* and *OneHotEncoder* to do that.

{% highlight scala %}
def handleCategorical(column: String): Array[PipelineStage] = {
  val stringIndexer = new StringIndexer().setInputCol(column)
    .setOutputCol(s"${column}_index")
    .setHandleInvalid("skip")
  val oneHot = new OneHotEncoder().setInputCol(s"${column}_index").setOutputCol(s"${column}_onehot")
  Array(stringIndexer, oneHot)
}
{% endhighlight %}

Create stages for all categorical variables

{% highlight scala %}
 val genderStages = handleCategorical("Sex")
 val embarkedStages = handleCategorical("Embarked")
 val pClassStages = handleCategorical("Pclass")

{% endhighlight %}

## 2. Classification using RandomForest

Random Forest is one of the best algorithm for classification purposes. So we will using it for our problem.

### 2.1. Create ML Pipeline with RandomForest Classifier

The below code creates a vector assembler stage which accumulates all the features we are using for our training. Then we create random forest
stage for classification.

Then finally we create a ML pipeline with all the stages.

{% highlight scala %}
//columns for training
val cols = Array("Sex_onehot", "Embarked_onehot", "Pclass_onehot", "SibSp", "Parch", "Age", "Fare")
val vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")

//algorithm stage
val randomForestClassifier = new RandomForestClassifier()
//pipeline
val preProcessStages = genderStages ++ embarkedStages ++ pClassStages ++ Array(vectorAssembler)
val pipeline = new Pipeline().setStages(preProcessStages ++ Array(randomForestClassifier))

{% endhighlight %}


### 2.2 Fit the model

{% highlight scala %}
val model = pipeline.fit(trainDf)
{% endhighlight %}

### 2.3. Accuracy Score

Once model is trained, we need to know how it's performing. So we use *accuracy score* as our evaluation metric. The below code shows how to calculate the same.

{% highlight scala %}
def accuracyScore(df: DataFrame, label: String, predictCol: String) = {
  val rdd = df.select(label, predictCol).rdd.map(row ⇒ (row.getInt(0).toDouble, row.getDouble(1)))
  new MulticlassMetrics(rdd).accuracy
}

  println("train accuracy with pipeline" + accuracyScore(model.transform(trainDf), "label", "prediction"))
  println("test accuracy with pipeline" + accuracyScore(model.transform(crossDf), "Survived", "prediction"))

{% endhighlight %}


### 2.4. Results

The below are the results.

{%highlight text %}
train accuracy with pipeline0.8516746411483254
test accuracy with pipeline0.816793893129771
{% endhighlight %}

## 3. Cross Validation and Hyper Parameter Tuning

Random forest comes with many parameters which we can tune. Tuning them manually is lot of work. So we can use cross validation facility provided by spark ML to search through these parameter space to come up with best parameters for our data.


### 3.1. Specifying the parameter grid

The below are the parameters which we want to search for.

{% highlight scala %}

val paramMap = new ParamGridBuilder()
  .addGrid(randomForestClassifier.impurity, Array("gini", "entropy"))
  .addGrid(randomForestClassifier.maxDepth, Array(1,2,5, 10, 15))
  .addGrid(randomForestClassifier.minInstancesPerNode, Array(1, 2, 4,5,10))
  .build()

{% endhighlight %}

### 3.2 Cross Validation

Once we define the parameters, we define cross validation stage to search through these parameters. Cross validation also make sures that we don't overfit the data.

{% highlight scala %}
def crossValidation(pipeline: Pipeline, paramMap: Array[ParamMap], df: DataFrame): Model[_] = {
  val cv = new CrossValidator()
    .setEstimator(pipeline)
    .setEvaluator(new BinaryClassificationEvaluator)
    .setEstimatorParamMaps(paramMap)
    .setNumFolds(5)
  cv.fit(df)
}
val cvModel = crossValidation(pipeline, paramMap, trainDf)
{% endhighlight %}


### 3.3 Results

As you can we got much better results using cross validation.

{%highlight text %}
train accuracy with cross validation0.8787878787878788
test accuracy with cross validation 0.8577099236641222
{% endhighlight %}

## 4. Generating Submit File

As the data used in this example is part of kaggle competition, I generated results for their test data to sumbit using below code.

{% highlight scala %}

 val testDf = sparkSession.read.option("header", "true").option("inferSchema", "true").csv("src/main/resources/titanic/test.csv")
 val fareMeanValue = df.agg(mean(df("Fare"))).first.getDouble(0)
 val fixedOutputDf = testDf.na.fill(meanValue, Array("age")).na.fill(fareMeanValue, Array("Fare"))                                                 
{% endhighlight %}


{% highlight scala %}

  def generateOutputFile(testDF: DataFrame, model: Model[_]) = {                                                                                          val scoredDf = model.transform(testDF)
    val outputDf = scoredDf.select("PassengerId", "prediction")
    val castedDf = outputDf.select(outputDf("PassengerId"), outputDf("prediction").cast(IntegerType).as("Survived"))                                      castedDf.write.format("csv").option("header", "true").mode(SaveMode.Overwrite).save("src/main/resources/output/")
  }
{% endhighlight %}

You can access complete code on  [github](https://github.com/phatak-dev/spark-ml-kaggle/blob/master/src/main/scala/com/madhukaraphatak/spark/ml/titanic/RandomForest.scala).

## Kaggle Ranking

Using the above code I got accuracy of **0.81** and **250** rank in kaggle.


