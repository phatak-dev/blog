---
layout: post
title: "Apache Beam : Next Step in Big Data Unification"
date : 2016-04-17
categories: spark flink beam
---
Unification been the theme of big data processing for quite some time. More and more frameworks in big data want to be platforms which can do multiple things, rather than yet another framework to solve a specific problem. This idea of unification has taken big data to new levels. These days we have come to expect single platform to do batch, streaming and interactive processing with plethora of libraries on top of it. 

As the processing unification is settling down, there is a new trend in big data to unify the API across multiple platforms like Spark, Flink etc. Apache Beam, is one of the first steps towards that direction.

In this post, I will be talking about the different level of unification happened over the years and how Apache Beam will be next step in that direction.


## Unification of cluster/ infrastructure

When Hadoop came along, it was only capable of running Map/Reduce. To run any other system, like Storm, you were needed to have a separate cluster. This meant that you need to maintain separate cluster for each framework which is big overhead in terms of maintenance and cost.

Over time, frameworks like Mesos and YARN solved those issues. YARN became an unified platform to run the multiple processing frameworks on same cluster. In this way, we need to have only one cluster and we can easily share the resources between the frameworks.


## Unification of processing frameworks

As infrastructure unified, we wanted unification in processing. In early days of big data, we used separate frameworks for batch, streaming and structure data analysis namely map/reduce, storm and hive. Learning, developing and maintaining these multiple framework code is became trickier. Also sharing data between these systems became tricky as they don't share any common abstraction.

Apache Spark came along to solve this problem. It proposed an single abstraction to base all different kind of processing. This made sharing data trivial and developers can improve applications in much rapid way as they only have to learn single framework than multiple ones. Apache spark dawned the platform era in big data. All other systems, like Flink, came after spark are following that way.

## Unification of developers

Any big data analysis comprise data scientists, the one who focus on the data modeling and data engineers, the one who focus on data pipelines. Earlier each of these teams used to use different toolset. This made difficult to share knowledge and code across the teams.

Apache Spark with it's Dataframe API is trying to unify these developers where both can use same platform for modeling and pipeline building.


So from the above points, it's clear that more and more efforts are spent by various frameworks to unify the different aspects of big data analysis. But now there is a new level of unification is needed. That is API unification.

## Need of API unification

Whenever there is a new platform, developers are forced to rewrite their application to get the benefits. Most of the time these platforms share a very similar API for data processing, only differing in the runtime implementation. This need of the rewrite of applications, holding back the adoption of new platforms. Also this makes difficult to use best tool for a given use case.

For example, Apache Flink has good support for streaming compare to Spark. Spark has good support for batch compared Flink. I want to use both Flink and Spark, but doing same computations for streaming and batch. In the current scenario, I need to write the program twice one in flink and one in spark. Though the API's look similar, they do differ in various details. So as of now whenever we want to use best of multiple frameworks, we need to write program multiple times. 

So if you want to write program only once, then you need to stick with single platform. This restricts the developers where they cannot use the best frameworks for the work. Isn't be nice where we can write the program once and run on different platforms?

## Apache Beam : Unifying the platforms

Apache Beam, is the new unified batch and streaming framework which is trying to solve the exact problem which we discussed above. Apache Beam allows to write a program in a DSL which can run on both flink and spark. Apache Beam is bringing unification to platforms as YARN brought to infrastructure.

From Apache Bean website, Apache Beam is 

	An open source, unified model and set of language-specific SDKs for defining and executing data processing workflows, and also data ingestion and integration flows, supporting Enterprise Integration Patterns (EIPs) and Domain Specific Languages (DSLs). Dataflow pipelines simplify the mechanics of large-scale batch and streaming data processing and can run on a number of runtimes like Apache Flink, Apache Spark, and Google Cloud Dataflow (a cloud service)

So from the definition, it's clear that apache beam is trying to have a single API which can express both batch and streaming application which can run on multiple runtimes. So now I can write a single program which not only can do both streaming and batch but allows me to choose the platform on which I want to run these programs on without any change to the code.


This kind of unification is taking big data to next level where developers are not need to write code multiple times whenever there is a new framework is around. As of now Beam is still in proposal stage, it will take still time to mature. But this new way of looking at big data will going to immensely benefit the organization to adopt new platforms much faster way.




