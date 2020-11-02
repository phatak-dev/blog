---
layout: post
title: "Introduction to Spark 3.0 - Part 10 : Ignoring Data Locality in Spark"
date : 2020-11-02
categories: scala spark spark-three 
---
Spark 3.0 is the next major release of Apache Spark. This release brings major changes to abstractions, API's and libraries of the platform. This release sets the tone for next year's direction of the framework. So understanding these few features is critical to understand for the ones who want to make use all the advances in this new release. So in this series of blog posts, I will be discussing about different improvements landing in Spark 3.0.

This is the tenth post in the series where I am going to talk about ignoring data locality in spark. You can access all posts in this series [here](/categories/spark-three).

TL;DR All code examples are available on [github](https://github.com/phatak-dev/spark-3.0-examples).


## Data Locality in Apache Spark

Data locality in spark helps spark scheduler to run the tasks of compute or caching on the machines where the data is available. This concept came from Hadoop Map/Reduce where data in HDFS will be used to place map operation. This avoided the data movement over network in HDFS. So whenever spark connects to sources like HDFS, s3 it captures the locations of files.

## Remote HDFS and S3

The above approach makes sense when spark cluster is co-located with distributed file system like HDFS. But there are many use cases where user may be processing data from a remote HDFS with separate spark cluster. This also applicable for other HDFS like file systems like s3. In these cases, reading locations of files is not useful as spark schedule can't use this information for co-locating the processing.

Till 3.0, there was no option to disable it and it was wasting a lot of time initially to figure all block location of remote files just to discard them in future. This has changed in 3.0

## Configuration to Disable the Data Locality

In Spark 3.0, there is a new configuration added to disable this data locality.

{% highlight text %}

spark.sql.sources.ignoreDataLocality.enabled

{% endhighlight %}

By default this configuration will be set to false. Using this configuration, we can instruct spark not to read data location.

## References

[https://issues.apache.org/jira/browse/SPARK-29189](https://issues.apache.org/jira/browse/SPARK-29189).

## Conclusion
Data Locality Ignore configuration helps developer improve the spark data reading when the source is remote to the spark cluster.
