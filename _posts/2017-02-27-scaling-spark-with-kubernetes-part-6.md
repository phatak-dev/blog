---
layout: post
title: "Scalable Spark Deployment using Kubernetes - Part 6 : Building Spark 2.0 Two Node Cluster" 
date : 2017-02-26
categories: scala spark kubernetes-series
---
In last post, we have built spark 2.0 docker image. As a next step we will be building two node spark standalone cluster using that image. In the context of of kubernetes,  node analogues to a container. So in the sixth blog of the series, we will be building two node cluster containing single master and single worker.You can access all the posts in the series [here](/categories/kubernetes-series).

TL;DR you can access all the source code on [github](https://github.com/phatak-dev/kubernetes-spark).


### Spark Master Deployment

To start with we define our master using kubernetes deployment abstraction. As you can recall from [earlier](/scaling-spark-with-kubernetes-part-3) post, deployment abstraction is used for defining one or morepods. Even though we need single master in our cluster, we will use deployment abstraction over pod as it gives us more flexiblity.

{% highlight yaml %}
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  labels:
    name: spark-master
  name: spark-master
spec:
  replicas: 1
  template:
    metadata:
      labels:
        name: spark-master
    spec:
      containers:
      - name : spark-master
        image: spark-2.1.0-bin-hadoop2.6 
        imagePullPolicy: "IfNotPresent"
        name: spark-master
        ports:
        - containerPort: 7077
          protocol: TCP
        command:
         - "/bin/bash"
         - "-c"
         - "--"
        args :
         - './start-master.sh ; sleep infinity'
{% endhighlight %}

The above yaml configuration shows the configuration for the master. The noteworthy pieces are

* image - We are using the image we built in our last post. This is availble in local docker images.

* imagePullPolicy - By default kubernetes tries to pull the image from remote servers like dockerhub. But as our image is only available locally, we need to tell to kubernetes not to pull from remote. *imagePullPolicy* property of configuration allows to us to control that. In our example, we say *IfNotPresent* , which means pull only if there is no local copy. As we already have built the image, it will be avaialble and kubernetes will not try to pull from remote.

* ports - We are exposing port *7077* on which spark master will listen.

* command - Command is the configuration which tells what command to run when container bootstraps. Here we are specifying it to run *start-master* script

You can access complete configuration on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/spark-master.yaml).

### Starting Spark Master

Once we have our configuration ready, we can start the spark master pod using below command

{% highlight sh %}

kubectl create -f spark-master.yaml 

{% endhighlight %}

### Spark Master Service

Once we have defined and ran the spark master, next step is to define the service for spark master. This service exposes the spark master on network and other workers can connect to it.

{% highlight yaml %}

apiVersion: v1
kind: Service
metadata:
  name: spark-master
  labels:
    name: spark-master
spec:
  ports:
    # the port that this service should serve on
  - name: webui
    port: 8080
    targetPort: 8080
  - name: spark
    port: 7077
    targetPort: 7077
  selector:
    name: spark-master
{% endhighlight %}

The above yaml configuration for spark master service. We are naming the our service also *spark-master* which helps in resolving proper hosts on cluster.

We are also exposing the additional port 8080 for accessing spark web ui.

You can access complete configuration on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/spark-master-service.yaml).

### Starting Spark Master Service

Once we have defined the master service, we can now start the service using below command.

{% highlight sh %}

kubectl create -f spark-master-service.yaml

{% endhighlight %}


### Spark Worker Configuration

Once we have our spark master and it's service started, we can define the worker configuration.

{% highlight yaml %}

apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  labels:
    name: spark-worker
  name: spark-worker
spec:
  replicas: 1
  template:
    metadata:
      labels:
        name: spark-worker
    spec:
      containers:
      - image: spark-2.1.0-bin-hadoop2.6 
        imagePullPolicy : "IfNotPresent"
        name: spark-worker
        ports:
        - containerPort: 7078
          protocol: TCP
        command:
         - "/bin/bash"
         - "-c"
         - "--"
        args :
         - './start-worker.sh ; sleep infinity'

{% endhighlight %}

As we are building two node cluster, we will be running only single worker as of now. Most of the configuration are same as master other than command which starts the worker.

You can access complete configuration on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/spark-worker.yaml).

### Starting Worker

You can start worker deployment using below command

{% highlight sh %}

kubectl create -f spark-worker.yaml

{% endhighlight %}

Now we have all services are ready


### Verifying the Setup

Run below command to verify that both spark master and spark worker deployments are started.

{% highlight sh %}

kubectl get po 

{% endhighlight %}

The above command should two pods running as below

{% highlight text %}

NAME                            READY     STATUS    RESTARTS   AGE
spark-master-498980536-6ljcw    1/1       Running   0          15h
spark-worker-1887160080-nmpq5   1/1       Running   0          14h

{% endhighlight %}

Please note that exact name of the pod will differ from machine to machine.

Once we verified the pods, verify the service using below command


{% highlight sh %}

kubectl describe svc spark-master

{% endhighlight %}

The above command should show result as below

{% highlight text %}

Name:                   spark-master
Namespace:              default
Labels:                 name=spark-master
Selector:               name=spark-master
Type:                   ClusterIP
IP:                     10.0.0.147
Port:                   webui   8080/TCP
Endpoints:              172.17.0.3:8080
Port:                   spark   7077/TCP
Endpoints:              172.17.0.3:7077
Session Affinity:       None

{% endhighlight %}

If both of the commands ran successfully, then we have spark cluster running successfully.


### Testing our spark cluster

We can test our spark deployment using observing web ui and running some commands from spark shell.

#### Accessing Web UI

In our configuration of spark master, we have exposed the UI port 8080. Normally it will be only available within spark cluster. But using the port forwarding, we can access the port on our local machine.

First let's see the pods running on cluster using below command

{% highlight sh %}

kubectl get po

{% endhighlight %}

It should show the below result

{% highlight text %}

NAME                           READY     STATUS    RESTARTS   AGE
spark-master-498980536-kfgg8   1/1       Running   0          14m
spark-worker-91608803-l22pw    1/1       Running   0          56s

{% endhighlight %}

We should port forward from master pod. Run below command. The exact name of the pod will differ from machine to machine.

{% highlight sh%}
 kubectl port-forward spark-master-498980536-kfgg8 8080:8080
{% endhighlight %}

Port-forward takes two parameters. One is the pod name and then port pair. In port pair the first port is container port and next one is local.

Once port is forwarded, go to this link [http://localhost:8080](http://localhost:8080).

You should see the below image

![spark-ui-kube](/images/spark-ui-kube.png)

#### Spark Shell

Once we have spark ui, we can test the spark from shell. Let's run the spark shell from master container.

First we need to login to our master pod. Run below command

{% highlight sh %}

kubectl exec -it spark-master-498980536-kfgg8 bash

{% endhighlight %}

Start the spark shell using below command 

{% highlight sh %}

/opt/spark/bin/spark-shell --master spark://spark-master:7077

{% endhighlight %}

Run below command to run some spark code

{% highlight scala %}

sc.makeRDD(List(1,2,4,4)).count

{% endhighlight %}

If the code runs successfully, then our cluster setup is working.

### Conclusion

In this blog, we have succesfully built two node spark cluster using kubernetes absttractions.

### What's Next?

Now we have defined our barebone cluster. In next blog, we will how to scale the cluster using kubernetes tools. Also we will discuss how to do resource management in the cluster.
