---
layout: post
title: "Scalable Spark Deployment using Kubernetes : Part 3 - Kubernetes Abstractions" 
date : 2017-02-08
categories: scala spark kubernetes-series
---
In last blog, we discussed about how to install kubernetes in our local machine.In this blog, we will discuss what are the different abstractions
provided by the kubernetes. You can access all the blogs in the series [here](/categories/kubernetes-series).


## Kubernetes Abstractions

Kubernetes is a production grade system. So it will have a vast concept surface. We are not going to cover each of those abstractions here. We are
going to focus on few of the one which are used most of the times. For all the abstractions, refer to [user guide](https://kubernetes.io/docs/user-guide/).

The Kubernetes abstractions can be divided into following four major categories

* Compute Abstractions - All the abstractions related running a computing unit

* Network Abstractions - All the abstractions related to expose the processing units on network

* Storage Abstractions - All the abstractions related to storage management

* Metadata Abstractions - All the abstractions related to discovering above process, network and storage abstractions.

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

One of the important aspects of pods are, they run all the containers in them on single node. This gives the locality to the containers which needs to be low latency connection between them. Also since they run on same machine, kubernetes creates a networking scheme which allows each containers to address them each other by "localhost"

### Defining the Pod

Kubernetes uses yaml as it's configuration language for defining various resource.
In below configuration, we are defining a pod which runs a single container of nginx. Ngninx is a popular
web server. 

{% highlight yaml %} 
apiVersion : v1
kind: Pod
metadata:
  name : nginx-pod
spec:
  containers:
   - name : nginx
     image : nginx
{% endhighlight %}
	

The above yaml snippet defines the pod. The below are the different pieces.

*apiVersion - parameter talks about the version of API of kubernetes we are using. This allows kubernetes to support
multiple versions of the API's at same time. 

* kind - This parameter define for which abstraction of kubernetes we are defining this configuration. Here we are defining for a pod

* metadata - Metadata of the pod. This allows kubernetes to locate the pod uniquely across the cluster.

* spec - This defines the all the containers we want to run

For each container we define

 * name - Name of the container. This will be also used as the host name of the container. So this has to be unique within the pod
 * image - Docker image that needs to be used to create the container.

You can read more about pod abstraction [here](https://kubernetes.io/docs/user-guide/pods/)

You can find the complete yaml file on [github]()

### Creating Pod from Configuration

Once we define the pod, then we can use *kubectl create* command to create a pod

{% highlight sh %}

kubectl create -f ngnixpod.yaml

{% endhighlight %}

This will download the latest ngnix image from dockerhub and starts the container inside
the pod.

If you run the below command 

{% highlight sh %}

kubectl get po

{% endhighlight %}

You should see the results as below 

{% highlight text %}

nginx-pod                          1/1       Running   0          37s

{% endhighlight %}

Now you have successfully ran a pod on your kubernetes instance.

### Deployment Abstraction

In earlier section, we discussed about pod abstraction. Pod abstraction works well when we need to create single copy of the code. But in clustered use cases like, we may need
multiple instance of same containers. For example, each spark worker will run same image. So in those cases using pod abstraction is not good enough. Also pod abstraction doesn't
allow us to update the code inside the pod without changing the configuration file. This will be challenging in cluster environment where we may want to dynamically update configs/ version of software.

So to overcome these challenges. Kubernetes gives us another abstraction called deployments. As name suggest, this abstraction allows end to end deployment of a pod. This allows us to create, update and destroy pods with much cleaner abstractions than the bare bone pod abstraction. So kubernetes documentation prefers the deployment abstraction over simple pod abstraction.

So let's rewrite our nginx pod example using deployment abstraction. The below is the yaml configuration for deployment

{% highlight yaml %}
apiVersion : extensions/v1beta1
kind: Deployment
metadata:
  name : nginx-deployment
spec:
  replicas : 1
  template:
    metadata :
     labels :
        name : nginx
    spec :
      containers:
       - name : nginx
         image : nginx

{% endhighlight %}

The below are the major differences are

 * replicas - We can create multiple instances of the pod using this. As we need only instance here we are specifying as the 1.

 * template - This holds the template for the pod. This information is same whatever we specified in the pod definition.

You can access the complete file on [github]()
### Running the deployment

Use the below command to run the deployment

{% highlight sh %}

kubectl create -f ngnixdeployment.yaml

{% endhighlight %}

You can see all running deployments using below command

{% highlight sh %}

kubectl get deployments 

{%  endhighlight %}

Now you have successfully ran the deployment.

Even though we have run the pod, we not access anything from it. So you may be asking how
to access the front-page nginx. To understand that, we need to understand the network/service abstractions provided by the kubernetes. We will be discussing about them in the next blog.


