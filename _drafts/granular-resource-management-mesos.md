---           
layout: post
title: "Granular resource management with Mesos"
date :  2014-10-27
categories: mesos
---

Mesos allows developers granularly manage the resources on the cluster. We can specify how much CPU,Memory and disk a given slave consume. With having these fine granularities helps us to run multiple frameworks on mesos without those competing for same resources.

In this post, we are going to emulate a three node cluster of mesos on single machine. We are going to divide our quad core cpu machine and 8GB ram machine to two slave which controls 50% of the resources. Your machine capabalities may vary. You can adjust those parameters to according to resources. This not only shows how mesos resource management capabilities, it also allows us to run the a mesos cluster on single machine which a very useful for testing and debugging purposes.

To follow the instructions in the post, you need to have a working installation of mesos on your machine. If not install mesos using [this](/mesos-single-node-setup-ubuntu). From now on we assume that $MESOS_HOME points to the mesos installation.

The following are the step to configure multi node cluster of mesos on single machine

## Master
There will be only one master. So create a working dir for master under home and point master working director to it.
{% highlight bash %}
 //create a working dir under home folder
 mkdir $HOME/mesos-master-work 
 //start mesos with working directory
 $MESOS_HOME/bin/mesos-master.sh --work_dir=$HOME/mesos-master-work  --ip=127.0.0.1
{% endhighlight %}

## Slaves
To run multiple mesos slaves on same machine, we have to differentiate them from each other. The mesos slaves are differentiating using     

 * port  
 * hostname    
 * working dir

 Using above three information we can run multiple slaves on single machine.

Follow the below instructions to run multiple slaves. 

### 1. Create work directories for slaves
{% highlight bash %}
mkdir -p $HOME/mesos_slave_work/slave1          
mkdir -p $HOME/mesos_slave_work/slave2 
{% endhighlight %}

### 2. Configure /etc/hosts
We specify two slaves with slave1 and slave2. Put the following entries inside /etc/hosts

{% highlight bash %}
127.0.1.2    slave1      
127.0.1.3    slave2
{% endhighlight %}

### 3. Start the slaves 
Once we have different working directories and host names, we can start slave1 on 5053 port and slave2 on 5054 port. 

{% highlight bash %}

$MESOS_HOME/bin/mesos-slave.sh --master=localhost:5050 --port=5053 --hostname=slave1  --work_dir=$HOME/mesos_slave_work/slave1 --resources="cpus(*):2; mem(*):3500"

$MESOS_HOME/bin/mesos-slave.sh --master=localhost:5050 --port=5054 --hostname=slave2  --work_dir=$HOME/mesos_slave_work/slave2

{% endhighlight %} 

_resources_ flag on the command allows us to specify how much ram and memory has to be dedicated to the slaves. Tune the parameters according your machine resources.

So by having fine granular control over resources, we can run multiple mesos slave on single machine which allows you to test your application on multi node setup from single machine.











