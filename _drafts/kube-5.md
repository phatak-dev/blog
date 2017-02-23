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
apiVersion : v1
kind : Service
metadata :
   name : nginx-service
   labels : 
     name : nginx-service
spec :
  selector :
        name : nginx
  ports : 
     - port : 80

{% endhighlight %}

The above configuration defines the service. The import sections to focus are

* kind - As we specified with pod and deployment abstractions, we specify the service using this parameter.

* selector - Connecting pod with service. This is the way kubernetes knows which pod to forward the requests to the service. In this , we
are specifying the selector on label called *name* and it's value *nginx*. This should be same labels that we have specified in the 
nginxdeployment.yaml. The below was the our deployment definition

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
         ports :
          - containerPort : 80
{% endhighlight %}

In above configuration, we have specified the labels in our template. This shows how label abstraction is used to connect service and pod abstractions.


* ports - This specifies the ports which service should connect on the container. By default the service port on which it listens is same as container
port. You can change it if you want by specifying the *targetPort* parameter.

You can access complete configuration on [github]() 
### Starting Service

Once we have defined the configuration, we can start the service using below command.

{% highlight sh %}

kubctl create -f nginxservice.yaml

{% endhighlight %}

We can list all the services, as below 

{% highlight sh %}

kubectl get svc 

{% endhighlight %}

It should the service runnig below.

{% highlight text %}

nginx-service   10.0.0.197   <none>        80/TCP    23h

{% endhighlight %}

Now we have succesfully started the service.

### Service EndPoint

Service we have created above is only accessible within the kubernetes cluster. There is a way to expose the service to external world, but we will be not discussing that currently. 

To connect to the service, we need to know the machine it runs. As we are running kubernetes in local mode, it will be virtual machine running minikube.

Run below command to get the end point details

{% highlight sh %}
kubectl describe svc 

{% endhighlight %}

It should show the output as below.


{% hightlight text %}
Name:                   nginx-service
Namespace:              default
Labels:                 name=nginx-service
Selector:               name=nginx
Type:                   ClusterIP
IP:                     10.0.0.197
Port:                   <unset> 80/TCP
Endpoints:              172.17.0.4:80
Session Affinity:       None

{% endhighlight %}

In above command, we are describing the complete information about service. We are interested in the *EndPoints* parameter. This gives the IP and port of the machine to which we can connect


### Testing with busy box

Now we have end point to call. But we need another pod in cluster to connect to this machine. So let's run another pod 

{% highlight sh %}
kubectl run -i --tty busybox --image=busybox --restart=Never -- sh 
{% endhighlight %}

The above command shows another way creating and running the pods. In above command the below are different pieces

* run - Specifies create and run pod

* -i - Specifies run the pod interactively. This allows us to send commands using pod

* --tty - Gives access to the terminal of the pod

* busybox - Name of the pod.

* --image - image to run inside the container. We are a running an image called busybox, which gives minimal linux shell utilites

* -- restart-never - Since it's a temporary pod, we don't need HA

* sh - Specifies run shell command to access

Once you run the above command, you should drop into a familiar linux shell. 

From the shell, run below command 

{% highlight sh %}

wget -O - http://172.17.0.4

{% endhighlight 5}

Replace the IP address with the one you got from end point. This should print the welcome page of nginx as below

{% highlight text %}

<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

{% end highlight %}

Now we have succesfully connected our service and used our pod.

Service layer of the kubernetes may look little complicated. It is. It's built for varieties of use cases. So it has multiple layer of redirection. We will explore more about this abstraction in upcoming blogs. 

### Conclusion

In this blog, we have discussed how to define and consume services. Services are one of the important features of the kubernetes which makes it powerful platform to deploy clustered applications.

## What's Next?

Now we know pod, deployment and service abstractions. These are minimal abstractions we need to build our spark cluster on kubernetes. In upcoming blog, we will be discussing how to build and scale spark cluster.




