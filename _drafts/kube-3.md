---
layout: post
title: "Scalable Spark Deployment using Kubernetes : Part 3 - Kubernetes Abstractions" 
date : 2017-02-08
categories: scala spark kubernetes-series
---
In last blog, we discussed about how to install kubernetes in our local machine.In this blog, we will discuss what are the different abstractions
provided by the kubernetes. You can access all the blogs in the series [here](/categories/kubernetes-series).


## Kubernetes Abstractions

Kubenetes is a prodution grade system. So it will have a vast concept sufrace. We are not going to cover each of those abstractions here. We are
going to focus on few of the one which are used most of the times. For all the abstractions, refer to [user guide](https://kubernetes.io/docs/user-guide/).

The Kubernetes abstractions can be divided into following four major categories

* Compute Abstractions - All the abstractions related running a computing unit

* Network Abstractions - All the abstractions related to expose the processing units on network

* Storage Abstractions - All the abstractions related to storage management

* Metdata Abstractions - All the abstractions related to discovering above process, network and storage abstractions.

In the following sections, we will be experimenting with each of these abstractions. Most of the applications we deploy on the kubernetes will some 
combination of these abstractions.


## A Brief Word About Containers

Kubernetes is container orchestration framework. But what is container? In simple terms, container is a light weight virtual machine which runs one of the services
of an application. The major difference between VM and Containers is how they share operating systems and underneath resources. In VM world, each VM has it's full copy operating system. But in case of containers, all the containers share a common operating system kernel. So containers are much more light weight than the VM's.

Even though containers are around more than a decade, docker made containers popular. You can get basics of docker or container in general by going through this article.

## Compute Abstractions

Once we know, we can now start discussing the different compute abstractions in kubernetes. Most of these abstractions discuss about how to create, manage and destroy
the containers on scale.


### Pod Abstraction

Pods is a collection of one or more containers. It's smallest compute unit you can deploy on the kubernetes.

One of the important aspects of pods are, they run all the containers in them on single node. This gives the locality to the containers which needs to be low latency conncection between them. Also since they run on same machine, kubernetes creates a networking scheme which allows each containers to address them each other by "localhost"

Let's define and run a pod. 

Kubernetes uses yaml as it's configuration language for defining various resource.
In below configuration, we are defining a pod which runs a single container of nginx. Ngninx is a popular
web server. 

{% highlight yaml %} 

{% highlight sh %}
	

You can read more about pod abstraction [here](https://kubernetes.io/docs/user-guide/pods/)
