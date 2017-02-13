---
layout: post
title: "Scalable Spark Deployment using Kubernetes : Part 1 - Introduction to Kubernetes" 
date : 2017-02-13
categories: scala spark kubernetes-series
---
As our workloads become more and more micro service oriented, building an infrastructure to deploy them easily 
becomes important. Most of the big data applications need multiple services likes HDFS, YARN, Spark  and their clusters.
Creating, deploying and monitoring them manually is tedious and error prone. 

So most of the users move to cloud to simplify it. Solutions like EMR, Databricks etc help in this regard. But then users will be locked into
those specific services. Also sometimes we want same deployment strategy to work on premise also. Most of the cloud providers don't have that option today.

So we need a framework which helps us to create and monitor complex big data clusters. Also it should helps us move between on premise and
other cloud providers seamlessly. Kubernetes is one those frameworks that can help us in that regard.

In this set of posts, we are going to discuss how kubernetes, an open source container orchestration framework from Google, helps us
to achieve a deployment strategy for spark and other big data tools which works across the on premise and cloud. As part of the series, we will 
discuss how to install, configure and scale kubernetes on local and cloud. Also we are going to discuss how to build our own customised images for the services and applications.

This is the first blog in the series where we discuss about what is kubernetes and it's advantages. You can access
all other blogs in the series [here](/categories/kubernetes-series/).

## What is Kubernetes?

Kubernetes is an open source container orchestration framework. In simple words, it's a framework which allows us
to create and manage multiple containers. These containers will be docker containers which will be running some services. 
These can be your typical webapp, database or even big data tools like spark, hbase etc.

## Why Kubernetes?

Most of the readers may have tried docker before. It's a framework which allows developers containerise their application. It has become a
popular way to develop, test and deploy applications on scale. When we already have docker, what is kubernetes bring into picture? Can't we 
just build our clusters using normal docker itself?

The below are the some of the advantages of using kubernetes over plain docker tools.

* #### Orchestration

One of the import feature that sets kubernetes apart from docker is it's not a container framework. But it's more of a orchestration layer for multiple containers
that normally make an application. Docker itself has compose feature but it's very limited. So as our application become complex, we will have
multiple containers which needs to be orchestrated. Doing them manually becomes tricky. So kubernetes helps in that regard.

Also kubernetes has support for multiple container frameworks. Currently it supports docker and rkt. This makes users
to choose their own container frameworks rather than sticking with only docker.

* #### Cloud Independent

One of the import design goal of kubernetes, is ability to run everywhere. We can run kubernetes in local machine, on-premise clusters or on cloud.
Kubernetes has support for AWS,GCE and Azure out of the box. Not only it normalises the deployment across the cloud, it will use best tool for given
problem given by specific cloud. So it tries to optimise for each cloud.

* #### Support for Easy Clustering 

One of the hard part of installing big data tools like spark on cloud is to build the cluster and maintain it. Creating clusters often need tinkering with networking to make sure all services are started in right places. Also once cluster is up and running, making sure each node has sufficient resources also is tricky.

Often scaling cluster, adding node or removing it, is tricky. Kubernetes makes all this much easier compared to current solutions. It has excellent support to
virtual networking and ability to easily scale clusters on will.

* #### Support for Service Upgradation and Rollback

One of the hard part of clustered applications, is to update the software. Sometime it may be you want to update the application code or want to update version of
spark itself. Having a well defined strategy to upgrade the clusters with check and balances is super critical. Also when things go south, ability to rollback 
in reasonably time frame is also important.

Kubernetes provides well defined image ( container image) based upgradation policies which can unify the upgrading different services across cluster. This makes
life easier for all the ops people out there.

* #### Effective Resource Isolation and Management

One of the question, we often ponder should we run services like kafka next to spark or not? Most of the time people advise to have separate machines
so that each service gets sufficient resources. But defining machine size and segregating services based on machines becomes tricky as we want to scale our
services.

Kubernetes frees you from the machine. Kubernetes asks you to define how much resources you want to dedicate for service. Once you do that, it will take care
of figuring out which machine to run those. It will make sure that it will effectively using all resources across machines and also give guarantees about resource
allocation. You no more need to worry about is one service is taking over all resources and depriving others or your machines are under utilized.

Not only kubernetes allows you to define resources In terms of GB of RAM or number of cpu's, it allows it to be defined in terms of percentage of machine resource or
in terms of no of requests. These options are there to dedicate the resources more granularly.

* #### Well Defined Storage Management

One of the challenges of micro service oriented architectures is to store the state across the restart/ upgradation of containers. It's critical for applications like Databases not loose data when something goes wrong with container or machine.

Kubernetes gives a clear abstraction of storage who's life cycle is independent of the container itself. This makes users ability to use different storages like host based, network attached drives to make sure that there will be no data loss. These abstractions ties well with persistence options provided by cloud like EBS from aws. Kubernetes makes long running persistent services like databases a breeze.

Now we know what kubernetes brings to the table. In our next post, we will be discussing how to install kubernetes on local machine. 


