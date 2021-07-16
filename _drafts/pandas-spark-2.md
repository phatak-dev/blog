---
layout: post
title: "Pandas on Apache Spark- Part 2: Hello World"
date : 2021-07-16
categories: spark python spark-pandas
---
Pandas on Spark brings the familirar python Pandas API on top of distributed Apache Platform framework. This combination allows python developers to write code in their faviroute pandas API with all the performance and distributed befinits of spark. This marriage of API and Platform is one of the biggest improvement landing in Apache spark in recent past. This feature is landing on 3.2 version of spark

In these series posts we will be discussing different aspects of this integration. This is second post in the series where we will write our first hello world example. You can access other posts in the series [here](/categories/spark-pandas).


## Setup

Running pandas on spark needs Spark 3.2. As I write the blog, its still in the development. So to run these examples you need to build the tar from code. Once 3.2 is released in stable version you can use it as any other pyspark program.

You can find more details how to build code from source in below link.


Also you need to install the below libraries in your venv

1. Pandas >= 0.23
2. PyArrow >= 1.0


## Hello World

As tradition, we always start with helloworld example. This example can be run from pyspark console or Jupyter notebook.


### Import Pandas

{% highlight python %}

import pyspark.pandas as ps

{% endhighlight %}

As first step, we import pandas from pyspark project. 


### Create Pandas Dataframe on Spark

{% highlight python %}

data = [1,2,3,4]
df = ps.DataFrame(data, columns=["a"])

{% endhighlight %}

In above code, we create pandas dataframe using some array. As you can observe the API is exactly same python Pandas. 

To make sure its using spark on pandas rather than normal pandas we can check it on spark UI

![/images/pandasonspark/pandasplan](/images/pandasonspark/pandasplan)


From above image, you can observe the above dataframe creation ran a SQL query on spark.


## Print Data

{% highlight python %}

df.head()

{% endhighlight %}

As with normal pandas, above code prints the dataframe data.


## Conclusion

In this post, we looked how to write a simple example pandas code using Pandas API on Apache Spark.
