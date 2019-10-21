---
layout: post
title: "Understanding Kubernetes API - Part 2 : Pods API"
date : 2019-10-21
categories: kubernetes k9-api
---

Kubernetes is a REST API driven system. All the operations in the system are executed by making the API requests to it's API server. This makes kubernetes easy to interact from external systems.

Most of the initial interactions with the API is done using **kubectl**. kubectl is a command line tool which converts the user commands to a API call.But once the user is comfortable with **kubectl**, for advance operations it will be beneficial to know how to interact with API directly. This gives more power to the user to express the operations more effectively. 

So in this series of blog posts, I will be discussing about how to use various kubernetes API's. This is the second post in the series which discusses about pods API. You can access all the posts in the series [here](/categories/k8-api).


## Pods API
Pod is a kubernetes abstraction that runs one or more containers. 

All pods in kubernetes run in a **namespace**. All the system related pods run in a namespace called **kube-system**. By default all the user pods run in **default** namespace.

Pods API is the part of Kubernetes API which allows user to run CRUD operations on pods.


## List Pods

We can list the pods using GET API call to */api/v1/namespaces/{namespace}/pods*.

The below curl request lists all the pods in **kube-system** namespace. 

{% highlight sh %}
curl --request GET \
  --url http://localhost:8080/api/v1/namespaces/kube-system/pods
{% endhighlight %}

The output contains these important fields for each pod

• **metadata** - The labels , name etc

• **spec**  - The spec of the pod. This contains the image, resource requirements etc

• **status** - Status of the pod.

For example, the output looks as below for **etcd** pod

{% highlight json %}

"metadata": {
 "name": "etcd-minikube",
"namespace": "kube-system"
 ...
}

"spec": {
  "containers": [
          {
            "name": "etcd",
            "image": "k8s.gcr.io/etcd-amd64:3.1.12",
            "command": [
              "etcd"
...
}

"status": {
        "phase": "Running",
        "conditions": [
          {
            "type": "Initialized",
            "status": "True",
            "lastProbeTime": null,
            "lastTransitionTime": "2019-10-21T05:11:11Z"
          }
..

{% endhighlight %}

## Get Details about Single Pod

We can access the details of individual pod using *api/v1/namespaces/kube-system/pods/{podname}*.

Example for etcd pod look as below

{% highlight bash %}

curl --request GET \
  --url http://localhost:8080/api/v1/namespaces/kube-system/pods/etcd-minikube

{% endhighlight %}

## Create Pod

Creating Pod requires defining the spec and metadata for the same. Usually user writes a yaml file to define the spec.

The below is the YAML definition for creating ngnix pod.

{% highlight yaml %}

apiVersion: v1
kind: Pod
metadata:
    name: ngnix-pod
spec:
   containers:
   - name: ngnix
     image: nginx:1.7.9
     ports:
     - containerPort: 80       

{% endhighlight %}

Kubernetes API accepts the json rather than YAML. The respective json looks as below

{% highlight json %}

{
	"apiVersion":"v1",
	"kind":"Pod",
	"metadata":{
		"name":"nginx-pod"
	},
	"spec":{
		"containers":[
			{
				"name":"ngnix",
				"image":"nginx:1.7.9",
				"ports":[
				  {
				    "containerPort": 80 
			    }
				]
			}
		]
	}
}

{% endhighlight %}

User needs to send PUT API call to */api/v1/namespaces/default/pods*.

{% highlight sh %}

curl --request POST \
  --url http://localhost:8080/api/v1/namespaces/default/pods \
  --header 'content-type: application/json' \
  --data '{
	"apiVersion":"v1",
	"kind":"Pod",
	"metadata":{
		"name":"nginx-pod"
	},
	"spec":{
		"containers":[
			{
				"name":"ngnix",
				"image":"nginx:1.7.9",
				"ports":[
				  {
				    "containerPort": 80 
			    }
				]
			}
		]
	}
}'

{% endhighlight %}

## Delete Pod
We can delete individual pod using DELETE API call *api/v1/namespaces/kube-system/pods/{podname}*.

{% highlight sh %}

curl --request DELETE \
  --url http://localhost:8080/api/v1/namespaces/default/pods/nginx-pod

{% endhighlight %}

## Conclusion
Pod is a basic abstraction in kubernetes to run one or more containers. In this post, we discussed about how to create, delete and list pods using kubernetes API.
