---
layout: post
title: "Understanding Kubernetes API - Part 1 : Introduction"
date : 2019-10-20
categories: kubernetes k8-api
---

Kubernetes is a REST API driven system. All the operations in the system are executed by making the API requests to it's API server. This makes kubernetes easy to interact from external systems.

Most of the initial interactions with the API is done using **kubectl**. kubectl is a command line tool which converts the user commands to a API call.But once the user is comfortable with **kubectl**, for advance operations it will be beneficial to know how to interact with API directly. This gives more power to the user to express the operations more effectively. 

So in this series of blog posts, I will be discussing about how to use various kubernetes API's. This is the first post in the series which gives introduction to Kubernetes API and setting up access. You can access all the posts in the series [here](/categories/k8-api).


## Kubernetes API

Kubernetes is a API driven system. Kubernetes master runs **kubeapi-server** to interact with the complete kubernetes control plane. This API is a REST API driven API which allows user to control every aspect the deployment.


## Pre-requisites 

To use the kubernetes API  you need to have a kubernetes cluster running. You can run one on a single node using minikube. For more information you can refer to the this [post](/scaling-spark-with-kubernetes-part-2).

## Getting Access

There is [wide variety of ways](https://kubernetes.io/docs/tasks/access-application-cluster/access-cluster/) to setting up access to the kube api server. Easiest one is using **kubectl-proxy**. This is a reverse proxy which auto locates the API server and then allows user interaction with default access. 

You can start the proxy using

{% highlight sh %}

kubectl proxy --port 8080

{% endhighlight %}

Now the proxy is available on 8080 port of the local system.


## Sending API Request

Once the proxy is available, you can send an API request using below curl command

{% highlight sh %}

curl --request GET \
  --url http://localhost:8080/api/v1

{% endhighlight %}

The truncated output look like as below

{% highlight text %}

{
  "kind": "APIResourceList",
  "groupVersion": "v1",
  "resources": [
    {
      "name": "bindings",
      "singularName": "",
      "namespaced": true,
      "kind": "Binding",
      }
]
}
...
{% endhighlight %} 

If you are able to see above result, you have been successfully connected to kubernetes API.

## Conclusion

Kubernetes is a REST API system which allows user to control it's object using this API. By learning this API, user can make effective use of kubernetes.
