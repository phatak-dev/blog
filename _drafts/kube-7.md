---
layout: post
title: "Scalable Spark Deployment using Kubernetes - Part 7 : Scaling Spark on Kubernetes" 
date : 2017-02-26
categories: scala spark kubernetes-series
---

In our last post we created two node spark cluster using kubernetes. In this post, we are going to discuss how to scale, manage and restrict
the resource usage in kubernetes. You can access all the posts in the series [here](/categories/kubernetes-series).

### Dynamically Increase Number of Workers


{% highlight sh %}
kubectl scale deployment spark-worker --replicas 2

{% endhighlight %}

### Multiple Clusters using Namespaces

{% highlight sh %}
kubectl create namespace cluster2

export CONTEXT=$(kubectl config view | awk '/current-context/ {print $2}')

kubectl config set-context $CONTEXT --namespace=cluster2

{% endhighlight %}

## Resource Management in Kubernetes

### CPU and Memory limiting for Pods




### Resource Management for Namespaces

	
