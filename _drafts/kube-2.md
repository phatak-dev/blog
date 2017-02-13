---
layout: post
title: "Scalable Spark Deployment using Kubernetes : Part 2 - Installing Kubernetes Locally using Minikube" 
date : 2017-02-08
categories: scala spark kubernetes-series
---

## Installing Kubernetes on Local Machine

One of the cool features of kubernetes is it can be installed and tried out in locally. It behaves exactly as it will be on a cluster. To try out 
kubernetes on local we need to install minikube and kubectl.

The below are the steps


* #### Step 1 :Pre-Requisites

To install the kubernetes on local machine, we install minikube. But minikube normally uses some kind of virtualisation layer to install the
need code. So for our example, we will use virtualbox as our virtualisation layer. For more requirements can be found [here](https://kubernetes.io/docs/getting-started-guides/minikube/#requirements)

Download and Install virtualbox from [here](http://www.virtualbox.org).

* #### Step 2 : Install MiniKube

Run the below commands to install minikube on linux. For other operating system, refer [here](https://github.com/kubernetes/minikube/releases).
Latest version as of now is 0.16.0

{% highlight sh %}

curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.16.0/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/

{% endhighlight %}

* ### Step 3 : Install KubeCtl

Kubectl is a command line utility which communicates to kubernetes over it's REST API. We can install it using below command

{% highlight sh %}

curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x ./kubectl
sudo mv ./kubectl /usr/local/bin/kubectl
{% endhighlight %}


## Interacting With Minikube

Once we installed the minikube and kubectl we can interact with it. 

We can start minikube using below commmand. It downloads minikube iso and start a virtual machine in virtualbox

{% highlight sh %}
minikube start
{% endhighlight %}

We can open the kubernetes dashboard using below command 

{% highlight sh %}
minikube dashboard
{% endhighlight %}


We can check is anything running or not, using below command

{% highlight sh %}
kubectl get po --all-namespaces
{% endhighlight %}

This command should show some kubernetes container running.

Now we have successfully installed and configured the kubernetes on our machine.

