---
layout: post
title: "Scalable Spark Deployment using Kubernetes - Part 7 : Dynamic Scaling and Namespaces" 
date : 2017-03-06
categories: scala spark kubernetes-series
---

In our last post we created two node spark cluster using kubernetes. Once we have defined and created the cluster
we can easily scale up or scale down using kubernetes. This elastic nature of kubernetes makes easy to scale
the infrastructure as and when the demand increases rather than setting up everything upfront. 

In this seventh blog of the series, we will discuss how to scale the spark cluster on kubernetes.
You can access all the posts in the series [here](/categories/kubernetes-series).

## Dynamic Scaling

When we discussed deployment abstraction in our previous blog, we talked about *replica* factor. In deployment configuration, we can specify the number
of replications we need for a given pod. This number is set to 1 in our current spark worker deployment.

One of the nice thing about deployment abstraction is, we can change replica size dynamically without changing configuration. This
allows us to scale our spark cluster dynamically.

### Scale Up

Run below command to scale up workers from 1 to 2.

{% highlight sh %}
kubectl scale deployment spark-worker --replicas 2
{% endhighlight %}

The above command takes deployment name as parameters and number of replicas. 
You can check results using

{% highlight sh %}
kubectl get po
{% endhighlight %}

When you run the above command, kubernetes creates more pods using template specified in spark-worker. Whenever these
pods come up they automatically connect to spark-master and scales the cluster.

### Scale Down

We can not only increase the workers, we can also scale down by setting lower replica numbers. 
 
{% highlight sh %}
kubectl scale deployment spark-worker --replicas 1
{% endhighlight %}

When above command executes, kubernetes will kill one of the worker to reduce the replica count to 1. 

Kubernetes automatically manages all the service related changes. So whenever we scale workers spark will automatically scale.

## Multiple Clusters 

Till now, we have run single cluster. But sometime we may want to run multiple clusters on same kubernetes cluster. If we try to run
same configurations twice like below

{% highlight sh %}
kubectl create -f spark-master.yaml

{% endhighlight %}

You will get below error

{% highlight text %}

Error from server: error when creating "spark-master.yaml": deployments.extensions "spark-master" already exists

{% endhighlight %}

Kubernetes is rejecting the request as the spark-master named deployment is already exist. One of the way to solve this issue is to
duplicate the configurations with different name. But it will be tedious and difficult to maintain.

Better way to solve this issue to use  namespace abstraction of kubernetes.

### Namespace Abstraction

Kubernetes allows users to create multiple virtual clusters on single physical cluster. These are called as namespaces. 

Namespace abstraction is used for allowing multiple users to share the same physical cluster. This abstraction gives scopes for names. This makes us to have same named services in different namespace. 

By default our cluster is running in a namespace called *default*. In next section, we will create another namespace where we can run one more single node cluster.

### Creating Namespace

In order to create new cluster, first we need to cluster new namespace. Run below command to create namespace called *cluster2*.

{% highlight sh %}
kubectl create namespace cluster2

{% endhighlight %}

You can list all the namespaces using below command

{% highlight sh%}

kubectl get namespaces

{% endhighlight %}

You should see the below  result

{% highlight sh %}

NAME          STATUS    AGE
cluster2      Active    16s
default       Active    81d
kube-system   Active    81d
{% endhighlight %}

*kube-system* is the namespace in which all the kubernetes related pods run.

### Setting Context

By default, kubectl points to default namespace. We should change it to point to other one to create pods in our namespace. We can do it using changing the context variable. 

Run below command to change the context

{% highlight sh %} 
export CONTEXT=$(kubectl config view | awk '/current-context/ {print $2}')
kubectl config set-context $CONTEXT --namespace=cluster2
{% endhighlight %}

In the first step, we get *CONTEXT* variable. In the next command, we set namespace to *cluster2*.

### Creating cluster in Namespace

Once we set the context, we can use same commands to create cluster. Let's run below the command 

{% highlight sh %}

kubectl create -f .

{% endhighlight %}

Now our second cluster is started. We can see all the pods across the namespaces using below command

{% highlight sh %}

kubectl get po --all-namespaces

{% endhighlight %}


You should see some result something like below 

{% highlight text %}

NAMESPACE     NAME                           READY     STATUS    RESTARTS   AGE
cluster2      spark-master-498980536-bxda1   1/1       Running   0          1m
cluster2      spark-worker-91608803-p1mfe    1/1       Running   0          1m
default       spark-master-498980536-cfw97   1/1       Running   0          46m
default       spark-worker-91608803-7pwhv    1/1       Running   0          46m
kube-system   kube-addon-manager-minikube    1/1       Running   17         81d
kube-system   kube-dns-v20-s0yyp             3/3       Running   80         81d
kube-system   kubernetes-dashboard-rb46j     1/1       Running   17         81d

{% endhighlight %}

As you can observe from the result, there are multiple spark-master running in different namespaces.

So using the namespace abstraction of kubernetes we can create multiple spark clusters on same kubernetes cluster.

## Conclusion

In this blog we discussed how to scale our clusters using kubernetes deployment abstraction. Also we discussed how to use 
namespace abstraction to create multiple clusters.

## What's Next?

Whenever we run services on kubernetes we may want to restrict their resource usage. This allows better infrastructure planning
and monitoring. In next blog, we will discuss about resource management on kubernetes.
