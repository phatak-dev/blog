---
layout: post
title: "Understanding Spark Connect API - Part 2: Introduction to Architecture"
date : 2023-05-26
categories: scala python spark spark-connect
---
In the 3.4 version, Apache Spark has released a new client/server-based API called Spark Connect. This API will help in improving how we develop and deploy Spark applications.

In this series of blogs, we are going to explore various functionalities exposed by spark connect API. This is the second post in the series where we discuss the architecture of spark connect. You can read all the posts in the series [here](/categories/spark-connect).

## Architecture

![Spark Connect Architecture](https://spark.apache.org/docs/latest/img/spark-connect-api.png)

The above diagram shows the architecture of the spark-connect. It has below main components.


### 1.Spark Connect API

Spark-Connect API is a gRPC based API that runs as a server to connect spark client applications with the spark driver and cluster. As you can 
observe from the diagram spark driver is no more part of the client application. Now it's on the server side of spark-connect API.

### 2.Thin Client Side API

As the spark driver is no more part of the client, how do we write the spark applications?. For this, spark now ships a separate client library which wraps the spark-connect API with a nice DataFrame based API. This makes sure that spark client applications do not need full installation and dependency of the spark. We will explore more about this API in our future posts.

### 3.DataFrame as the Intermediate Protocol

One of the challenges of providing a new API is it forces all the client applications to be rewritten in this new API. But spark connect API smartly solves this issue by exposing the same DataFrame/ Dataset API as in standard spark code and internally converting those API calls to the gRPC calls. This tremendously helps the application developers as they don't need to learn yet another new API and reuse all their existing code.

The below diagram how the spark code is converted to gRPC calls.

![Spark Connect DF Resolution](https://spark.apache.org/docs/latest/img/spark-connect-communication.png).

## Benefits of Spark-Connect

This section of the post talks about the different benefits of spark-connect API.


### 1. Lightweight Client

In the last post, we discussed that in spark driver based architecture the client needs to run a full-fledged spark driver for an interactive application. But with thin client API, the actual heavy lifting is done by the spark server rather than the spark client. So the resource requirements for the client application will be very low.

### 2.Standard Integration for Notebook Systems

As spark-connect is now part of the standard spark, all notebook systems can standardize on this API to provide interactive integration with spark. 

### 3. Smoother Transition with DataFrame based API

As the spark-connect use DataFrame API as it's user-facing API, transitioning to spark-connect is quite easy. As most of the spark developers are already familiar with DataFrame API there is almost no learning curve. 


## References

[https://spark.apache.org/docs/latest/spark-connect-overview.html](https://spark.apache.org/docs/latest/spark-connect-overview.html).


## Conclusion

In this post, we talked about the architecture of the spark-connect and how it avoids some of the shortcomings of the spark driver architecture.
