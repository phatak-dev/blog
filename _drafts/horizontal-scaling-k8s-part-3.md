---
layout: post
title: "Horizontal AutoScaling Spark in Kubernetes - Part 3 : AutoScaling Spark Cluster"
date : 2019-10-27
categories: kubernetes k8s-horizontal-scaling spark
---


<<<<<<<<<<<<< Introduction >>>>>>>>>>>>

In this series of post, I will be discussing about kubernetes horizontal pod auto scaler with respect to auto scaling spark. This is the third post in the series which talks about how to auto scale the spark cluster. You can find all the posts in the series [here](/categories/k8s-horizontal-scaling).


## Registering the Horizontal Pod AutoScaler

We can register a HPA for **spark-worker** deployment using below command

{% highlight sh %}

kubectl autoscale deployment spark-worker --max=2 --cpu-percent=50

{% endhighlight %}

In above command we specified below information

* Deployment is spark-worker

* Maximum number of replicas is 2

* Threshold is 50 percent


## Get current State of HPA

Once we create the HPA we can see the current status using below command

{% highlight sh %}

kubectl get hpa

{% endhighlight %}

The result will look as below

{% highlight text %}

NAME           REFERENCE                 TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
spark-worker   Deployment/spark-worker   0%/50%    1         2         1          4h43m


{% endhighlight %}

As you can see, the current state says the load is 0 as there is nothing running in the spark layer.

## Describing HPA

The above command just gives the high level information. If we want to know more, we can run the **describe** command to get all the events.

{% highlight sh %}

kubectl describe hpa spark-worker

{% endhighlight %}

The result looks as below

{% highlight text %}

Name:                                                  spark-worker
Namespace:                                             default
Labels:                                                <none>
Annotations:                                           <none>
CreationTimestamp:                                     Sun, 27 Oct 2019 11:30:50 +0530
Reference:                                             Deployment/spark-worker
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  0% (1m) / 50%
Min replicas:                                          1
Max replicas:                                          2
Deployment pods:                                       1 current / 1 desired
Conditions:
  Type            Status  Reason            Message
  ----            ------  ------            -------
  AbleToScale     True    ReadyForNewScale  recommended size matches current size
  ScalingActive   True    ValidMetricFound  the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
  ScalingLimited  True    TooFewReplicas    the desired replica count is less than the minimum replica count
Events:           <none>

{% endhighlight %}

The conditions field of the output says the current state.

## Running Dynamic Allocated Spark Job

Let's run the spark pi example in dynamic allocation mode. The dynamic allocation mode of spark starts with minimum number of executors. But as the more number of tasks are schedule it will start requesting the more executors. This intern should request more resources from kubernetes which will kickin the autoscaling.

Run the below command from **spark-master** container.

{% highlight sh %}

/opt/spark/bin/spark-submit --master spark://spark-master:7077 --conf spark.shuffle.service.enabled=true --conf spark.dynamicAllocation.enabled=true --class org.apache.spark.examples.SparkPi /opt/spark/examples/jars/spark-examples_2.11-2.1.0.jar 10000

{% endhighlight %}

## Observing the AutoScale in HPA

Once the spark jobs start running, the cpu usage will go higher. We can start describing the HPA state using below command to see did the auto scaling kick in. You need to keep running this command for 1-2 minutes to see the change

{% highlight sh %}

kubectl describe hpa spark-worker

{% endhighlight %}

You should see below result after sometime

{% highlight text %}

Deployment pods:                                       1 current / 2 desired
Conditions:
  Type            Status  Reason              Message
  ----            ------  ------              -------
  AbleToScale     True    SucceededRescale    the HPA controller was able to update the target scale to 2
  ScalingActive   True    ValidMetricFound    the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
  ScalingLimited  False   DesiredWithinRange  the desired count is within the acceptable range
Events:
  Type    Reason             Age                 From                       Message
  ----    ------             ----                ----                       -------
  Normal  SuccessfulRescale  4s (x2 over 4h52m)  horizontal-pod-autoscaler  New size: 2; reason: cpu resource utilization (percentage of request) above target

{% endhighlight %}

Here you can see the autoscaling kicked in. You can confirm by spark UI also.


## Observing AutoScaling in Spark UI

<<image>>


## CoolDown of Spark Scaling

The pods that are allocated with kept for **5mins** by default. After this cooldown time they will be released.

## Conclusion
In this pod we discussed about what is Horizontal Pod Autoscaler and how it's used.
