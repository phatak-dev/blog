from pyspark.sql import SparkSession
import pandas as pd
import numpy as np


sparkSession = SparkSession.builder.master("local").appName("Spark ML").getOrCreate()

sparkDf = sparkSession.read.format("csv").option("header","true").option("inferSchema","true").load("adult_salary_100rows.csv")

sparkDf.printSchema()



# Create String Indexer for workclass and salary
from pyspark.ml.feature import StringIndexer,VectorAssembler,OneHotEncoder
from pyspark.ml import Pipeline

workClassIndexer = StringIndexer().setInputCol("workclass").setOutputCol("workclass_indexed")
workClassOneHot =  OneHotEncoder().setInputCol("workclass_indexed").setOutputCol("workclass_onehot")

salaryIndexer = StringIndexer().setInputCol("salary").setOutputCol("label")
vectorAssembler = VectorAssembler().setInputCols(['workclass_onehot','age']).setOutputCol("features")
# create pipeline
pipeline = Pipeline().setStages([workClassIndexer,workClassOneHot,salaryIndexer,vectorAssembler])


transformedDf = pipeline.fit(sparkDf).transform(sparkDf).select("features","label")
transformedDf.printSchema()


pandaDf = transformedDf.toPandas()

pandaDf.info()


from sklearn.linear_model import LogisticRegression
X = pandaDf['features']
y = pandaDf['label'].values.reshape(-1,1)

lr = LogisticRegression()
#lr.fit(X,y)


map(lambda x : x,pandaDf['features'].iloc[0:1])

series = pandaDf['features'].apply(lambda x : np.array(x.toArray())).as_matrix().reshape(-1,1)
features = np.apply_along_axis(lambda x : x[0], 1, series)


model = lr.fit(features,y)
print(model)

