---
layout: post
title: "Understanding Spark Connect API - Part 5: Dataframe Sharing Across Spark Sessions"
date : 2023-08-18
categories: scala python spark spark-connect
---

In the 3.4 version, Apache Spark has released a new client/server-based API called Spark Connect. This API will help in improving how we develop and deploy Spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark-connect API. This is the fifth post in the series where we will discuss about sharing dataframe between different spark sessions. You can read all the posts in the series [here](/categories/spark-connect).


## Sharing DataFrame across Spark Sessions

Currently in spark, there is no way to serialize the dataframe and access them in other spark session. This feature is typically useful where we want to rerun the same set of operations on data and we can encode those operations as a logical plan of a dataframe. Currently, we need to use python scripts or other code to encode the same. But with spark-connect API, this is going to change.


## Dataframe Plan and Protocol Buffer

In spark-connect, the client uses non resolved dataframe plan as the intermediate format for exchanging  information between the server. Having the dataframe plan as the intermediate format makes it very easy for spark to keep the API of dataframe as it is and just change the implementation.

Internally spark uses the protocol buffer for serializing these plans. So we can use this feature to serialize and deserialize the dataframe in the spark and share them across spark sessions.

## Serialising the Spark Plan

The below are the steps to create a dataframe and serializing it to a file.

###1. DataFrame Creation

The below spark code creates a simple dataframe and runs filter on it.

{% highlight scala %}
val data = List (
  Employee("madhav",26,60000),
  Employee("Raju",30, 80000)
)

val sourceDf = sparkSession.createDataset(data)
val filteredDf = sourceDf.filter("salary > 60000")

{% endhighlight %}



###2. Access the Plan Object

We can access the plan of the dataframe using below code.

{%highlight scala %}
val plan = filteredDf.plan
{% endhighlight %}

On a spark-connect dataframe, there is a field name **plan** which gives access to the plan object.

###3. Print Plan

Once we created the dataframe, we can print it's plan using below code

{% highlight scala %}
println(plan)
{% endhighlight %}

The output will be as below 

{% highlight scala %}
root {
  common {
    plan_id: 2
  }
  filter {
    input {
      common {
        plan_id: 1
      }
      local_relation {
        data: "\377\377\377\377 # truncated for output here" 
        schema: "{\"type\":\"struct\",\"fields\":
        [{\"name\":\"name\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},
        {\"name\":\"age\",\"type\":\"integer\",\"nullable\":false,\"metadata\":{}},
        {\"name\":\"salary\",\"type\":\"double\",\"nullable\":false,\"metadata\":{}}]}"
      }
    }
    condition {
      expression_string {
        expression: "salary > 60000"
      }
    }
  }
}

{% endhighlight %}

The above is the string representation of protocol buffer plan. As you can it encodes, schema, data and operations on dataframe.

###4. Serialize Plan

The below code serializes the plan to a file.

{% highlight scala %}

val file = new File("filterdef.ser")
val fileOutputStream = new FileOutputStream(file)
plan.writeTo(fileOutputStream)
fileOutputStream.close()

{% endhighlight %}

The above is a standard Java/Scala code to serialize the object.


## Deserialization and Dataframe Recreation 

Once we serialized the dataframe, now we can deserialize and recreate the dataframe. The below are steps for the same

## Internal API

This portion of code, uses an internal API which is not yet exposed as public API. So make sure this code resides in **org.apache.spark.sql** package. Otherwise you will be not able run the code.

### 1. Deserialize the Plan Object

The below code deserializes the file to the plan.

{% highlight scala %}

val serializedFile = "filterdef.ser"
val inputStream = new FileInputStream(serializedFile)
val parsedPlan = Plan.parseFrom(inputStream)

{% endhighlight %}.

### 2. Create Dataframe the Plan

Once we have the plan, we can create the Dataframe from the same. **This is an internal API**.

{% highlight scala %}

val dataset = new Dataset(sparkSession, parsedPlan, UnboundRowEncoder)
println(dataset.explain(true))

{% endhighlight %}

The above code uses new internal **Dataset** constructor which creates a dataset/dataframe using the parsed plan. The last parameter in the constructor is to define the type of the dataset. If we pass **UnboundRowEncoder** it will return a **Dataset[Row]**.

### 3. Print Data

Once we have recreated the dataframe, we can verify the same by outputting the data.

{%highlight scala %}

println(dataset.show())

{% endhighlight %}

The above code outputs 

{% highlight text %}

+----+---+-------+
|name|age| salary|
+----+---+-------+
|Raju| 30|80000.0|
+----+---+-------+

{% endhighlight %}

As you can see, we are able to successfully read and recreate dataframe in new spark session.

## Code

You can access code for [serialize](https://github.com/phatak-dev/spark-connect-examples/blob/master/src/main/scala/com/madhukara/sparkconnect/PlanSerializer.scala) and [deserialize](https://github.com/phatak-dev/spark-connect-examples/blob/master/src/main/scala/org/apache/spark/sql/PlanDeserializer.scala) on github.

## Conclusion

In this post, we saw how to use spark-connect plan serialization to share the dataframe between different spark sessions.
