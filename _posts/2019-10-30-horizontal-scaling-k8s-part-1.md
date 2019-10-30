---
layout: post
title: "Auto Scaling Spark in Kubernetes - Part 1 : Introduction"
date : 2019-10-30
categories: kubernetes k8s-horizontal-scaling spark
---
Kubernetes makes it easy to run services on scale. With kubernetes abstractions, it's easy to setup a cluster of spark, hadoop or database on large number of nodes. Kubernetes takes care of handling tricky pieces like node assignment,service discovery, resource management of a distributed system. We have discussed how it can be used for spark clustering in our earlier [series](/categories/kubernetes-series).

As the services are becoming more and more dynamic, handling resource needs statically is becoming challenge. With cloud being prevalent, users expect their infrastructure to scale with usage. For example, spark cluster on kubernetes should be able to scale up or down depending upon the load.

Kubernetes system can scaled manually by increasing or decreasing the number of replicas. You can refer to [this](/scaling-spark-with-kubernetes-part-7) post for more information. But doing this manually means lot of work. Isn't it better if kubernetes can auto manage the same?

Kubernetes Horizontal Pod AutoScaler(HPA) is one of the controller in the kubernetes which is built to the auto management of scaling. It's very powerful tool which allows user to utilize resources of the cluster effectively.

In this series of post, I will be discussing about  HPA with respect to auto scaling spark. This is the first post in series which is an introduction to HPA. You can find all the posts in the series [here](/categories/k8s-horizontal-scaling).


## Horizontal Pod AutoScaler
The Horizontal Pod Autoscaler automatically scales the number of pods in a replication controller, deployment or replica set based on observed CPU utilization. Essentially it's similar to YARN auto scaling which increases or decrease number of container by observing the resource utilization. HPA is more generic compared to YARN as HPA can auto scale any service which is running on kubernetes, where as YARN is more of a Hadoop focused tool.

![HPA picture](https://d33wubrfki0l68.cloudfront.net/4fe1ef7265a93f5f564bd3fbb0269ebd10b73b4e/1775d/images/docs/horizontal-pod-autoscaler.svg)


HPA works as below

* Continuously monitor the resource usage of a registered pod. 15 seconds is default polling time.

* Compare the usage with defined threshold. 80 percent is default threshold.

* Increase or decrease number of instances depending on the threshold.


## Resource Monitoring using Metric Server

How does HPA gets the information about resource usage and how does it knows pod using more than it's requested? We will answer these question in this section.


### Metric Server

Kubernetes uses metric server to collect the cpu or other resource metrics from each pod. Metric server is a cluster wide aggregator which collects the metrics from all the pods running and exposes the API's for the same. You can read more about it in below link

[https://kubernetes.io/docs/tasks/debug-application-cluster/resource-metrics-pipeline/#metrics-server](https://kubernetes.io/docs/tasks/debug-application-cluster/resource-metrics-pipeline/#metrics-server).



### Calculating Threshold

Each pod in kubernetes can define how much resource it's request using below format.

{%highlight yaml %}
resources:
  limits:
    cpu: "1"
  requests:
    cpu: "1" 
{% endhighlight %}

In above example, the pod is requesting for 1 cpu. HPA uses this information to how much of the requested resource is getting used. If the metric server says the resource usage is higher than the threshold, then HPA kicks in auto scaling.
 
## References

[https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale).


## Conclusion
HPA is one of the powerful tool provided by the kubernetes which allows user to auto scale their infrastructure based on usage. This leads to effective use of the infrastructure. In this post we discussed what is HPA and how it works.
