---
layout: post
title: "Interactive Scheduling using Azkaban - Part 2 : Challenges in scheduling interactive workloads"
date : 2016-07-03
categories: scala azkaban
---
Every big data application needs some kind of scheduling to run daily jobs. So over the years having a good stable scheduling systems for hadoop, spark jobs has become more and more important. The different workloads in big data have different requirements from  the scheduler. So in this blog post I will be discussing about different scheduling requirements for batch, streaming and interactive usecases and challenges associated with interactive workload.

This is second post in series of blogs where I will be discussing about using Azkaban scheduler to do interactive scheduling. You can access all other posts from the series [here](/categories/azkaban).

## Scheduling needs of different big data workloads

The below are the different requirements of big data workloads from scheduler system.

* ### Batch
Set of jobs which needs to be executed on timely manner. In this scenario, a scheduler system needs to allow user to define the script with all the dependencies of a flow and allow it to be scheduled. To add/modify the jobs user will normally changes the script and runs the updated ones. The examples for these kind of scheduler systems are Ozzie, airflow etc.

* ### Streaming
Continuous stream of data is processed to produce results. Normally streaming only needs scheduler to initiate stream processing system and from there streaming framework will take over.

The above two scenarios are one of most supported and common place in big data world from quite sometime. So all the scheduling system, including azkaban, supports them well. But there is a new workload emerging these days which needs special attention.

## Interactive big data workload

As spark became popular, it has made interactive programming as one of the important part of big data workloads. In interactive settings, a user will be analyzing the data adhocly and once he/she is happy with the steps then they want to schedule them to run in timely manner.

The notebook systems like Zeppelin,Jupiter have made interactive programming highly popular. Initially used for the data science use cases, they are also used for data engineering use cases these days.

So as interactive workloads becoming common place, supporting ability to scheduling jobs interactively becoming more and more important. But doing this with existing systems is not easy.

## Challenges of scheduling Interactive workloads

Unlike batch workloads, interactive workloads are not static. They evolve as user adds/removes the code. Normally user may want to add / remove scheduling on the fly rather than modifying the script. So the non azkaban frameworks cannot be used in scenario because of following reasons.

* ### No/Limited REST API support

Most of the scheduling systems like oozie have very limited support for programmatic access. Often they rely upon the traditional scripting world, where you need to configure jobs using script and submit them. It works great for batch, but cannot be used for interactive applications as they need an good programmatic API to schedule jobs.

* ### Lack of good user interface for monitoring 

Most of the scheduling system have very limited user interfaces. Most of them limit themselves to show work flow graphs. Also many of them doesn't allow users to extend the user interfaces which results in building the custom ones themselves.

In batch, normally user interface is not that important. But in interactive it plays a huge role. Ability to monitor the jobs in a small time frame is important as it results in a good user feed back.

* ### Support for different executors

Many scheduling systems limit themselves for Hadoop or Spark. But in interactive application one often likes to run different kind of processing on same system. So ability to run different workloads becomes extremely important.

So from above points it's clear that the most of the existing scheduler systems are geared towards the batch processing scenarios. So using them in a interactive application is hard.

## Note on Azkaban for batch workload

Though my blog posts are focusing on using azkaban for interactive workloads, azkaban fares well in the batch also. Even most of it's documentation is dedicated to do batch scheduling using it's web UI rather than for interactive workloads. But with it's hidden gem of REST API, it's well suited for the interactive applications too.

So in this blogpost, we discussed about challenges in scheduling interactive workloads. In the next blogpost, we are going to discuss how azkaban solves these issues and is good candidate scheduler framework for interactive workloads. 







