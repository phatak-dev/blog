---
layout: post
title: "Data Source V2 API in Spark 3.0 - Part 1 : Motivation for New Abstractions"
date : 2020-03-22
categories: scala spark  spark-three datasource-v2-spark-three
---
Spark 3.0 is a major release of Apache Spark framework. It's been in preview from last December and going to have  a stable release very soon. As part of major release, Spark has a habit of shaking up API's to bring it to latest standards. There will be breaking changes also in these API's. One of such API is Data source V2 API.

Data Source V2 API, a new data source API for spark, was introduced in spark 2.3. Then it's been updated in spark 2.4. I have written detailed posts on same [here](/categories/datasource-v2-series).

This API is going to be completely changed in Spark 3.0. Spark rarely change an API this frequently in between releases. But as data source are heart of the framework, they are improved constantly. Also in spark 2.4, these API's were marked **evolving**. This means they are meant to be changed in future.

The usage of the data sources have not changed in 3.0. So if you are a user of the third party data sources you don't need to worry. These changes are geared mainly towards the developer of these sources. Also all the sources written V1 API going to work even in 3.0. So if your source is not updated, no need to panic. It's going to work without latest optimisations.

These new changes in V2 API brings more control to data source developer and better integration with spark optimiser. Moving to this API makes third party sources more performant. So in these series of posts I will be discussing the new Data source V2 API in 3.0. This is the first post in the series where I will be discussing the motivation to update the API's.You can find all the posts in the series [here](/categories/datasource-v2-spark-three).


## Learning From Experience

The biggest motivation to change the abstractions came from using Data source V2 API's for real sources. When spark team used to implement the file sources like csv, parquet and other streaming sources like Kafka they started to see the gaps. These gaps where more in the abstractions than the actual implementation itself. From this learning experience, it made them to relook at the API and change the abstractions.

The below are some of the learnings

### Scan Execution Order is Not Obvious

V2 Read API introduced the mixins interfaces for everything like operator pushdown, column pruning. One of the reason for these high level interfaces was to have flexibility of mixing the only ones needed. This was major selling point of these new interfaces.

Even though these gave a lot of flexibility they created confusion about order of their invocation. From the API it was not apparent which order these are called. Currently developer needed to depend upon the documentation. Depending upon the documentation means API is not good.


### Streaming API doesn't Play Well with the Batch API

Streaming API doesn't support few of the features like FilterPushDown etc compared to batch API. But as the current API about mixins, it was hard to write a source which supports both streaming and batch as we need to skip some features selectively. So most of the cases it was handled by throwing exceptions for non supported features and handling it other places for streaming for API. This was more of a hack than an proper API design.


### Columnar Scan should not be Mixin Trait

Columnar scan is a Data source V2 feature which allows reading data in columnar format. By default all the sources are implemented as row scan. As it's exposed as additional trait, all the default methods assume row based scanning. Currently it's not easy to define column scan only. The current hack is to throw exception when row scanning is tried and control is handed over columnar way. This is hacky and not right.


The above are some of the learnings. You can read more about the same in the design doc linked in reference material.


## References

[Data Source V2 API Improvement Design Doc](https://docs.google.com/document/d/1DDXCTCrup4bKWByTalkXWgavcPdvur8a4eEu8x1BzPM/edit#)


## Conclusion

Data source V2 brings major change to the way we write spark data sources. In Spark 3.0, this API is going through a major overhaul to integrate the learnings from wild. In this post we learned about all the motivation.
