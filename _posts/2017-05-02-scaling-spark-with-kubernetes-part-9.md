---
layout: post
title: "Scalable Spark Deployment using Kubernetes - Part 9 : Service Update and Rollback" 
date : 2017-05-03
categories: scala spark kubernetes-series
---
In last few blog posts on kubernetes, we have discussed about how to build and scale spark cluster. Once services are deployed, we also
need to update services time to time. When we update a service, we need to make sure that, it don't interrupt the working of other
services. Kubernetes has built in support for the service update and rollbacks. This makes changing services on kubernetes
much easier than doing them manually in other platforms.

In this ninth blog of the series, I will be discussing about service update and rollback in kubernetes.
You can access all the posts in the series [here](/categories/kubernetes-series).

## Updating Service

In our discussion of deployment abstraction, I told that deployment helps us to handle life cycle of a service. Currently,
we are running the spark version 2.1.0. Let's say we want to change it to 1.6.3 without changing the 
configuration. We can use deployment abstraction for achieving the same.

The below are the steps to change spark version from 2.1.0 to 1.6.3 using deployment abstraction.

### 1. Generate Spark Image for 1.6.3

As we did for spark 2.1.0, we first need to have an image of the spark 1.6.3. This can be easily done by changing docker file as below.

{% highlight sh%}

 ENV spark_ver 1.6.3

{% endhighlight %}

Once we update the docker file, we need to build new image with below command.

{% highlight sh %}

docker build -t spark-1.6.3-bin-hadoop2.6 .

{% endhighlight %}

Now we have  our new spark image ready.

###  2. Set Images to Deployment

The below two commands sets new images to already running spark-master and spark-worker deployments

{% highlight sh %}

kubectl set image deployment/spark-master spark-master=spark-1.6.3-bin-hadoop2.6

kubectl set image deployment/spark-worker spark-worker=spark-1.6.3-bin-hadoop2.6

{% endhighlight %}

In the command, we are setting the image for *spark-master*  and *spark-worker* container inside the deployment. This helps only update needed containers
inside deployment rather than updating all.

This only sets new images. It has not updated the service yet. We need to use roll out command for that.

### 3. Rollout the Deployment

The below commands rolls out the changes to deployment.

{% highlight sh %}

kubectl rollout status deployment/spark-master
kubectl rollout status deployment/spark-worker

{% endhighlight %}


When you roll out changes, kubernetes first brings up new pods with 1.6.3 version. Then once they are running
the old pods will be deleted.

This shown in the below output.

{% highlight sh %}

NAME                               READY     STATUS        RESTARTS   AGE
nginx-deployment-619952658-16z1h   1/1       Running       1          11d
spark-master-1095292607-mb0xh      1/1       Running       0          37s
spark-worker-1610799992-4f701      0/1       Pending       0          25s
spark-worker-671341425-xxlxn       1/1       Terminating   0          53s

{% endhighlight %}

As part of the roll out, kubernetes will update all dns entries to point to new pods.

This graceful switch over from older version to new version of pods makes sures that service is least interrupted when we
update services.

You can verify the spark version using *spark-ui* or logging into one of the pods.

## Service Rollback

As part of the service update, kubernetes remembers state of last two deployments. This helps us to roll back the changes
we made it to deployment using *undo* command. 

If we want to undo our change of spark version, we can run the below commands

{% highlight sh %}

kubectl rollout undo deployment/spark-master
kubectl rollout undo deployment/spark-worker

{% endhighlight %}

The above commands reverses the spark version back to 2.1.0. This ability to quickly undo the service is
powerful. If something goes wrong, we can rollback the service to it's previous state without much effort.

## Conclusion

Kubernetes has native support for service update and rollback. Using deployment abstraction we can easily roll out the changes to
our services without effecting other services.


