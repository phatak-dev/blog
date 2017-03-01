---
layout: post
title: "Scalable Spark Deployment using Kubernetes - Part 5 : Building Spark 2.0 Docker Image" 
date : 2017-02-26
categories: scala spark kubernetes-series
---
In last few posts of our kubernetes series, we discussed about the various abstractions available in the framework. In next set of posts, we will be
building a spark cluster using those abstractions. As part of the cluster setup, we will discuss how to use various different configuration available
in kubernetes to achieve some of the import features of clustering. This is the fifth blog of the series, where we will discuss about building a spark
2.0 docker image for running spark stand alone cluster. You can access all the posts in the series [here](/categories/kubernetes-series).

TL;DR you can access all the source code on [github](https://github.com/phatak-dev/kubernetes-spark).

### Need for Custom Spark Image

Kubernetes already has documented creating a spark cluster on [github](https://github.com/kubernetes/kubernetes/tree/master/examples/spark). But currently it uses old version of the spark. Also it has some configurations which are specific to google cloud. These configurations are not often needed in most of the use cases. So in this blog, we will developing a simple spark image which is based on kubernetes one.

This spark image is built for standalone spark clusters. From my personal experience, spark standalone mode is more suited for containerization
compared to yarn or mesos.

### Docker File

First step of creating a docker image is to write a docker file. In this section, we will discuss how to write a docker file needed
for spark.

The below are the different steps of docker file.

* Base Image

{% highlight sh %}
FROM java:openjdk-8-jdk
{% endhighlight %}

The above statement in the docker file defines the base image. We are using
a base image which gives us a debian kernel with java installed. We need 
java for all spark services.

* Define Spark Version 
{% highlight sh %}
ENV spark_ver 2.1.0
{% endhighlight %}

The above line defines the version of spark. Using ENV, we can defines a variable and use it in different places in the script. Here we are building the spark with version 2.1.0. If you want other version, change this configuration.

* Download and Install Spark Binary

{% highlight sh %}
RUN mkdir -p /opt && \
    cd /opt && \
    curl http://www.us.apache.org/dist/spark/spark-${spark_ver}/spark-${spark_ver}-bin-hadoop2.6.tgz | \
        tar -zx && \
    ln -s spark-${spark_ver}-bin-hadoop2.6 spark && \
    echo Spark ${spark_ver} installed in /opt

{% endhighlight %}

The above curl command and downloads the spark binary. It will be symlinked into /opt/spark.


* Add start scripts to image

{% highlight sh %}

ADD start-common.sh start-worker.sh start-master.sh /
RUN chmod +x /start-common.sh /start-master.sh /start-worker.sh

{% endhighlight %}

The above lines add some start scripts. We discuss more about these scripts
in next section.

Now we have our docker file ready. Save it as *Dockerfile*.

You can access the complete script on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/docker/Dockerfile).


### Scripts

In above, we have added some scripts for starting master and worker. Let's see what's inside them.


* start-common.sh

This is a script which runs before starting master and worker.

{% highlight sh%}

#!/bin/bash

unset SPARK_MASTER_PORT 

{% endhighlight %}

The above script unsets a variable set by kubernetes. This is needed as this configuration interferes with the
spark clustering. We will discuss more about service variable in next post.

You can access complete script on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/docker/start-common.sh).

* start-master.sh

This is a script for starting master.

{% highlight sh %}

#!/bin/sh

. /start-common.sh

echo "$(hostname -i) spark-master" >> /etc/hosts

/opt/spark/sbin/start-master.sh --ip spark-master --port 7077

{% endhighlight %}

In the first step, we run the common script. We will be using *spark-master* as the host name for our master container. So we are adding that into */etc/hosts* file.

Then we start the master using *start-master.sh* command. We will be listening on 7077 port for the master.

You can access complete script on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/docker/start-master.sh).

* start-worker.sh

This is the script for starting worker containers.

{% highlight sh %}

#!/bin/sh

. /start-common.sh

/opt/spark/sbin/start-slave.sh spark://spark-master:7077

{% endhighlight %}

It is similar to master script. The only difference is we are using *start-slave.sh* for starting our worker nodes.

You can access complete script on [github](https://github.com/phatak-dev/kubernetes-spark/blob/master/docker/start-worker.sh).


Now we have our docker script ready. To build an image from the script, we need docker.

### Installing Docker

You can install the docker on you machine using the steps [here](https://docs.docker.com/engine/installation/). I am using docker version *1.10.0*.

### Using Kubernetes Docker Environment

Whenever we want to use docker, it normally runs a daemon on our machine. This daemon is used for building and pulling docker images. Even though we can build our docker image in our machine, it will be not that useful as our kubernetes runs in a vm. In this case, we need to push our docker image to vm and then only we can use the image in kubernetes.

Alternative to that, another approach is to use minikube docker daemon. In this way we can build the docker images directly on our virtual machine.

To access minikube docker daemon, run the below command

{% highlight sh %}

eval $(minikube docker-env)

{% endhighlight %}

Now you can run 

{% highlight sh %}

docker ps 

{% endhighlight %}

Now you can see all the kubernetes containers as docker containers. Now you have successfully connected to minikube docker environment.


### Building image

Clone code from github as below

{% highlight sh %}

git clone https://github.com/phatak-dev/kubernetes-spark.git

{% endhighlight%}

cd to *docker* folder then run the below docker command.

{% highlight sh %}

cd docker

docker build -t spark-2.1.0-bin-hadoop2.6 .

{% endhighlight%}

In above command, we are tagging (naming) the image as *spark-2.1.0-bin-hadoop-2.6*. 

Now our image is ready to deploy, spark 2.1.0 on kubernetes.


### Conclusion

In this post, we discussed how to build a spark 2.0 docker image from scratch. Having our own image gives more flexibility than using
off the shelf ones.

### What's Next?

Now we have our spark image ready. In our next blog, we will discuss how to use this image to create a two node cluster in kubernetes.
