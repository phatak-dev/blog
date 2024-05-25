---
layout: post
title: "Understanding Spark Connect API - Part 6: Dataframe Sharing Across Scala and Python"
date : 2023-10-11
categories: scala python spark spark-connect
---

In the 3.4 version, Apache Spark has released a new client/server-based API called Spark Connect. This API will help in improving how we develop and deploy Spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark-connect API. This is the sixth post in the series where we will discuss about sharing dataframe between different spark sessions between Scala and Python. You can read all the posts in the series [here](/categories/spark-connect).


## Reading Serialized Plan in Python

In last post, we serialised proto buffer plan to a file from Scala. Same plan can be read from python and loaded into protocol buffer.

{% highlight python %}
f = open("filepath", mode="rb")
plan = f.read()
{% endhighlight %}

In above code, we read the serialised file as binary file in python. Here we read the content of the file to binary file.

Then we will parse the binary content to proto buffer plan using below code.

{% highlight python %}

import pyspark.sql.connect.proto as proto
protoPlan = proto.Plan()
protoPlan.ParseFromString(plan)

{% endhighlight %}

In above code, we create an empty proto object and the using *ParseFromString* method of protobuffers we read the binary content to that object.

The below will be content protoPlan when we print the same.

{% highlight javascript %}

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

As you can observe, it's the same plan we had in earlier example. So we have successfully read the protobuffer plan to python proto object.


## Converting Protobuffer Plan to DataFrame

Once we have the plan, we can use spark-connect client to execute the plan and get the result as pandas dataframe.

{% highlight python %}

client = spark._client

{% endhighlight %}

In the above code, we get handle to spark-connect client from the spark session.

Then we can use below code to convert the plan to a pandas dataframe. Here the spark-connect client will send the derserliaised dataframe plan to server and fetches the result

