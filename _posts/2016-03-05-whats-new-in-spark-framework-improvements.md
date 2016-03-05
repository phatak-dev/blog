---
layout: post
title: "What's New in Spark : Tales from Spark Summit East - Framework Improvements"
date : 2016-03-05
categories: spark spark-summit-east-2016
---
Recently Databricks, company behind the Apache Spark, held this year's first [spark summit](https://spark-summit.org/east-2016/), spark developer conference, in new york city. Lots of new exciting improvements in spark and it's ecosystem got discussed in various talks. I was watching the videos of the conference on youtube and wanted to share the ones I found interesting.

These are the series of blog posts focusing on various talks categorized into different aspects of Spark. You can access all the posts in the series [here](/categories/spark-summit-east-2016).

This is the first post in the series where I will be sharing talks which focused on improvements to the core of the spark itself. 

### 1. Matei Zaharia keynote on Spark 2.0

Matei Zaharia, Spark's creator, laid out plans for next version of Spark in his keynote. His talks mainly revolved around performance and new abstraction like Dataset. Spark 2.0 is one of the major steps in spark's evolution.You can read more about my thoughts on Spark 2.0 [here](/introduction-to-spark-2.0).

The below is the video of the talk. You can find slides on [slideshare](http://www.slideshare.net/databricks/2016-spark-summit-east-keynote-matei-zaharia).

<div class="video-container"> <iframe src="https://www.youtube.com/embed/ZFBgY0PwUeY" frameborder="0" width="560" height="315"></iframe> </div>  

### 2. Structuring Spark: Dataframes, Datasets and Spark Streaming

Structured data analysis has become very important part of spark in last few releases. More and more work is done on Dataframe API compared to RDD API. In this talk speaker talks about how these API's share common core and how they are planning to bring the same API's for stream analysis also.

The below is the video of the talk. You can find slides on [slideshare](http://www.slideshare.net/SparkSummit/structuring-spark-dataframes-datasets-and-streaming-by-michael-armbrust).

<div class="video-container"> <iframe src="https://www.youtube.com/embed/i7l3JQRx7Qw" frameborder="0" width="560" height="315"></iframe> </div>  

### 3. The Future of Real Time Spark - A Revamped Spark Streaming 

Building streaming application is hard. Combining batch processing and stream processing in a single application needs a lot of design and detailed implementation. Compared to other components of Spark, there was not much going on in spark streaming for a while. But its more the case. Spark 2.0 going to bring a completely new revamped API for spark streaming.

The below is the video of the talk. You can find slides on [slideshare](http://www.slideshare.net/rxin/the-future-of-realtime-in-spark).

<div class="video-container"> <iframe src="https://www.youtube.com/embed/oXkxXDG0gNk" frameborder="0" width="560" height="315"></iframe> </div> 

### 4. Spark performance: What's Next - 10x performance improvement in Spark 2.0

With introduction of tungsten and codegen in 1.4, spark performance is significantly improved in last few releases. Spark 2.0 bring whole new set of techniques which going to take the spark performance to next level. In this talk, speaker talks about different techniques getting developed to improve spark performance. Most of these already in master branch which you can start using to test it yourself.

The below is the video of the talk. You can find slides on [slideshare](http://www.slideshare.net/databricks/spark-performance-whats-next).

<div class="video-container"> <iframe src="https://www.youtube.com/embed/JX0CdOTWYX4" frameborder="0" width="560" height="315"></iframe> </div> 


In next blog in the series, I will be sharing my thoughts on the talks which focused on the ecosystem around spark.