---
layout: post
title: "Scalable Spark Deployment using Kubernetes - Part 4 : Service Abstractions" 
date : 2017-02-20
categories: scala spark kubernetes-series
---

In last blog, we discussed about the compute abstraction with the help of nginx. In that blog, we discussed about how to expose ngnix for consumption. In this fourth blog of the series, we are going to discuss various network related abstractions provided kubernetes. You can access all the blog in the series [here](/categories/kubernetes-series).

## Network Abstractions

Network abstractions in the kubernetes are the one which facilitate the communication between the pods or the communication of the pods from external world. Commonly these are known as service abstractions.

In the following sections, we are going to explore different service abstractions.


### Container Port

As part of the pod definition, we can  define which ports to be exposed from the container using *containerPort* property. This will expose that specific port to all
the container on it's ip address

Let's define port at 80, for nginx example.

{% highlight yaml %}
- containerPort : 80
{% endhighlight %}

You can access complete file [here]().


### Service

Once we defined the container port, next step is to define service.

Service abstraction gives way to define a set of logical pods. This is a network abstraction which defines a policy to expose micro service to other parts of the application.

This seperation of container and it's service layer allows us to upgrade the different parts of the applications independent of each other. This is the strength of the microservice.

Let's define a service for our nginx deployment.

{% highlight yaml %}
apiVersion : 




{% endhighlight %}


### Getting End Point







### Testing with busy box

i kubectl run -i --tty busybox --image=busybox --restart=Never -- sh 
