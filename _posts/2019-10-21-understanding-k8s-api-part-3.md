---
layout: post
title: "Understanding Kubernetes API - Part 3 : Deployments API"
date : 2019-10-21
categories: kubernetes k8-api
---

Kubernetes is a REST API driven system. All the operations in the system are executed by making the API requests to it's API server. This makes kubernetes easy to interact from external systems.

Most of the initial interactions with the API is done using **kubectl**. kubectl is a command line tool which converts the user commands to a API call.But once the user is comfortable with **kubectl**, for advance operations it will be beneficial to know how to interact with API directly. This gives more power to the user to express the operations more effectively. 

So in this series of blog posts, I will be discussing about how to use various kubernetes API's. This is the third post in the series which discusses about deployments API. You can access all the posts in the series [here](/categories/k8-api).


## Deployments API
Deployment is a kubernetes abstraction that is responsible for running one or more replicas of a pod. Most of the time deployments are preferred over pod as they provide more control over failures of a pod.

Deployments API is the part of Kubernetes API which allows user to run CRUD operations on deployments.

## List Deployments

We can list the deployments using GET API call to */apis/apps/v1/namespaces/{namespace}/deployments*.

The below curl request lists all the deployments in **kube-system** namespace. 

{% highlight sh %}
curl --request GET \
  --url http://localhost:8080/apis/apps/v1/namespaces/kube-system/deployments
{% endhighlight %}

The output contains same fields as pods. But status field of the deployment has information about number of replicas. You can observe in below output

{% highlight json %}
  "status": {
        "observedGeneration": 1,
        "replicas": 1,
        "updatedReplicas": 1,
        "readyReplicas": 1,
        "availableReplicas": 1
}
{% endhighlight %}

## Get Details about Single Deployment

We can access the details of individual deployment using */apis/apps/v1/namespaces/{namespace}/deployments/{deployment-name}*.


Example for kube-dns deployment look as below

{% highlight bash %}
curl --request GET \
  --url http://localhost:8080/apis/apps/v1/namespaces/kube-system/deployments/kube-dns
{% endhighlight %}

## Create Deployment

Creating deployment requires defining the spec and metadata for the same. Usually user writes a yaml file to define the spec.

The below is the YAML definition for creating ngnix deployment.

{% highlight yaml %}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.7.9
        ports:
        - containerPort: 80
{% endhighlight %}

Kubernetes API accepts the json rather than YAML. The respective json looks as below

{% highlight json %}
{
    "apiVersion":"apps/v1",
    "kind":"Deployment",
    "metadata":{
        "name":"nginx-deployment",
        "labels":{
            "app":"nginx"
        }
    },
    "spec": {
    "replicas" : 3,
    "selector": {
        "matchLabels" : {
            "app":"nginx"
        }
    },
    "template" : {
    "metadata" : {
        "labels" : {
            "app":"nginx"
        }
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
}
}
{% endhighlight %}

User needs to send PUT API call to *apps/v1/namespaces/default/deployments*.

{% highlight sh %}
curl --request POST \
  --url http://localhost:8080/apis/apps/v1/namespaces/default/deployments \
  --header 'content-type: application/json' \
  --data '{
	"apiVersion":"apps/v1",
	"kind":"Deployment",
	"metadata":{
		"name":"nginx-deployment",
		"labels":{
			"app":"nginx"
		}
	},
	"spec": {
	"replicas" : 3,
	"selector": {
		"matchLabels" : {
			"app":"nginx"
		}
	},
	"template" : {
	"metadata" : {
		"labels" : {
			"app":"nginx"
		}
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
}
}'
{% endhighlight %}


## Delete Deployment
We can delete individual deployment using DELETE API call */apis/apps/v1/namespaces/default/deployments/nginx-deployment*.

{% highlight sh %}
curl --request DELETE \
  --url http://localhost:8080/apis/apps/v1/namespaces/default/deployments/nginx-deployment
{% endhighlight %}

## Conclusion
Deployment is a abstraction in kubernetes to run multiple replicas of a pod. In this post, we discussed about how to create, delete and list deployments using kubernetes API.
