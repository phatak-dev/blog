---           
layout: post
title: "Granular resource management in  Mesos"
date :  2014-11-04
categories: mesos
---

Mesos allows developers to granularly manage the resources on the cluster. We can specify how much Cpu, memory and disk a given slave can consume. With having these fine granularities helps us to share slave resources across different mesos frameworks.

In this post, we are going to run a three node cluster of mesos on a single machine. We are going to divide our quad core, 8GB ram machine to two slaves in which each slave controls 50% of the resources. This kind of setup allows us to run a mesos cluster on single machine for testing and debugging purposes.

Your machine capabilities may vary. You should adjust resource parameters to according to your machine. 


## Mesos installation

To follow the instructions in the post, you need to have a working installation of mesos on your machine. If not install mesos using [this](/mesos-single-node-setup-ubuntu) post. From now on we assume that $MESOS_HOME points to the mesos installation.


## Three node cluster setup

The following are the step to configure multi node cluster of mesos on single machine.

### Step 1 : Configure master
There will be only one master. So create a working dir for master under home and point master working directory to it.
{% highlight bash %}
 //create a working dir under home folder
 mkdir $HOME/mesos-master-work 
 //start mesos with working directory
 $MESOS_HOME/bin/mesos-master.sh --work_dir=$HOME/mesos-master-work  --ip=127.0.0.1
{% endhighlight %}

### Step 2 : Configure Slaves
To run multiple mesos slaves on same machine, we have to differentiate them from each other. The mesos slaves are differentiating using     

 * port  
 * hostname    
 * working dir

Follow the below instructions to configure and run multiple slaves. 

#### 2.1 Create work directories for slaves
{% highlight bash %}
# create working directories for slaves
mkdir -p $HOME/mesos_slave_work/slave1          
mkdir -p $HOME/mesos_slave_work/slave2 
{% endhighlight %}

#### 2.2 Configure /etc/hosts
We specify two slaves with hostname slave1 and slave2. Put the following entries inside /etc/hosts

{% highlight bash %}
# append this to end of the file
127.0.1.2    slave1      
127.0.1.3    slave2
{% endhighlight %}

#### 2.3 Start the slaves 
Once we have different working directories and host names, we can start slave1 on 5053 port and slave2 on 5054 port. 

{% highlight bash %}
# start the slaves

$MESOS_HOME/bin/mesos-slave.sh --master=localhost:5050 --port=5053 --hostname=slave1  --work_dir=$HOME/mesos_slave_work/slave1 --resources="cpus(*):2; mem(*):3500"

$MESOS_HOME/bin/mesos-slave.sh --master=localhost:5050 --port=5054 --hostname=slave2  --work_dir=$HOME/mesos_slave_work/slave2 --resources="cpus(*):2; mem(*):3500"

{% endhighlight %} 

_resources_ flag on the command allows us to specify how much ram and memory has to be dedicated to the slaves. Tune the parameters according your machine resources.

Now you have a multi node cluster of mesos on your machine. 











