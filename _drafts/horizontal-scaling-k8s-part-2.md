---
layout: post
title: "Horizontal AutoScaling Spark in Kubernetes - Part 2 : Spark Setup"
date : 2019-10-27
categories: kubernetes k8s-horizontal-scaling spark
---


<<<<<<<<<<<<< Introduction >>>>>>>>>>>>

In this series of post, I will be discussing about kubernetes horizontal pod auto scaler with respect to auto scaling spark. This is the second post in the series which talks about how to setup spark cluster to use the autoscaling. You can find all the posts in the series [here](/categories/k8s-horizontal-scaling).


## Spark Cluster Setup on Kubernetes

In earlier series of post we have discussed how to setup the spark cluster on kubernetes. If you have not read it, read it in below link before continuing.


[Spark Cluster Setup on Kubernetes](/categories/kubernetes-series/).



## Enabling Metrics Server in Minikube

As we discussed before, metrics server is an important part of the autoscaling. In normal kubernetes clusters, it's enabled by default. But if you are using minikube to test the HPA you need to enabled it explicitly.

The below is the command. This minikube restart.


{% highlight sh %}

minikube addons enable metrics-server
{% endhighlight %}


Once it's enabled, you should be able to see it in the list of addson.

{% highlight sh %}

minikube addons list

{%endhighlight %}

{% highlight text %}

- addon-manager: enabled
- dashboard: enabled
- default-storageclass: enabled
- efk: disabled
- freshpod: disabled
- gvisor: disabled
- heapster: disabled
- helm-tiller: disabled
- ingress: disabled
- ingress-dns: disabled
- logviewer: disabled
- metrics-server: enabled
- nvidia-driver-installer: disabled
- nvidia-gpu-device-plugin: disabled
- registry: disabled
- registry-creds: disabled
- storage-provisioner: enabled
- storage-provisioner-gluster: disabled
{%endhighlight %}


## Defining the Resource Usage For Spark Worker

In our spark setup, we need to autoscale spark worker. To auto scale the same, we need to define the resource needs as below


### Restricting at Pod Level

{% highlight yaml %}

apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    name: spark-worker
  name: spark-worker
spec:
  replicas: 1
  selector:
    matchLabels:
       name: spark-worker
  template:
    metadata:
      labels:
        name: spark-worker
    spec:
      containers:
      - image: spark-2.1.0-bin-hadoop2.6
        imagePullPolicy : "IfNotPresent"
        name: spark-worker
        ports:
        - containerPort: 7078
          protocol: TCP
        resources:
          limits:
            cpu: "1"
          requests:
            cpu: "1"
        command:
         - "/bin/bash"
         - "-c"
         - "--"
        args :
                - './start-worker.sh; sleep infinity'
{% endhighlight%}

In above YAML, we have request for the single cpu for every worker in our cluster.


### Restricting at Spark Level

By default spark doesn't respect the resource restriction set by kubernetes. So we need to pass this information when we start the slave. So the below changes are done to our **start-worker.sh**.

{% highlight sh %}

#!/bin/sh

. /start-common.sh

/opt/spark/sbin/start-slave.sh --cores 1 spark://spark-master:7077

{% endhighlight %}

In above code, **--cores 1 ** will tell to the spark that this slave should use only one core.


## Enabling External Shuffle Service

To use the autoscale pods, we need to run the spark in dynamic allocation mode. We will talk more about this mode in next post. One of the pre requisite for the dynamic allocation is external shuffle service. This is enabled in each worker node using below configuration in **spark-default.conf**.

{% highlight text %}

spark.shuffle.service.enabled   true

{% endhighlight %}

## Rebuilding the Docker Image

As we number of changes to the our setup, we need to rebuild the image. You can read about building the image in [here](/scaling-spark-with-kubernetes-part-5/).

## Starting the Cluster

Now we have done all the neccessary changes. We can start the cluster. You can follow steps layout out [here](scaling-spark-with-kubernetes-part-6) for the same.


## The State of the Cluster

<<< image of the spark master>>>

## Conclusion
In this pod we discussed about what is Horizontal Pod Autoscaler and how it's used.
