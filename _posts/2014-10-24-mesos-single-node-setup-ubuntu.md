---           
layout: post
title: "Mesos single node cluster on Ubuntu"
date : 2014-10-24
categories: mesos
---
[Apache Mesos](http://mesos.apache.org/) is a distributed scheduling framework which allows us to build a fault tolerant distributed system from scratch. In this post we are going to see step by step instructions to install mesos on a ubuntu system.

To follow these steps, make sure that you have a 64-bit machine with Ubuntu 12.04 or later.

### Step 1 : Download Mesos

Run the following commands to download and extract mesos distribution. Here we are downloading latest stable mesos version 0.20.0.

{% highlight bash %}
wget http://www.apache.org/dist/mesos/0.20.0/mesos-0.20.0.tar.gz      
tar -zxf mesos-0.20.0.tar.gz   
{% endhighlight %}

After running above commands, we will get a folder named **mesos-0.20.0** which we refer as **MESOS_INSTALL** from now on.

### Step 2 : Install Prerequisites

Mesos is written in C and C++. So to install mesos, we need to install it's dependencies. So run the following command to install all the needed dependencies.

{% highlight bash %}
sudo apt-get update; sudo apt-get install build-essential openjdk-6-jdk python-dev python-boto libcurl4-nss-dev libsasl2-dev
{% endhighlight %}


### Step 3 : Build mesos 
Mesos is distributed as a source code package, rather than as binary. So we have to build the source code in order to get running binaries. Run the following commands to build the source code. Please replace MESOS_INSTALL in the commands with the path to *mesos-0.20.0* directory.

{% highlight bash %}
cd MESOS_INSTALL # Replace MESOS_INSTALL with actual path to mesos-0.20.0
 mkdir build     #create directory where the compiled code lives
 cd build
 ../configure
 make 
 make check 
{% endhighlight %}

Compiling and running test cases may take while to complete. It took around 1hr on my machine. So be patient.

Once *make check* completes successfully, mesos is successfully installed on your machine.


### Step 4 : Setting up single node cluster
Mesos cluster can have single master and multiple slaves. In this post, we will discuss how to setup single master and single slave.

Make sure that you are in *mesos-0.20.0/build* folder to run the following commands.

{% highlight bash %}
#start master
./bin/mesos-master.sh --ip=127.0.0.1 --work_dir=/tmp/mesos
#start slave
./bin/mesos-slave.sh --master=127.0.0.1:5050
{% endhighlight %}

You can check if the cluster started or not by pointing your web browser at [127.0.0.1:5050](http://127.0.0.1:5050). If you can see 1 at under "Slaves -> Activated" then you have single node cluster running.

*bin* folder contains all the shell scripts to start master and slave daemons. *mesos-master.sh* starts the mesos master with a checkpoint directory supplied at *--work-dir*. For this example I have used /tmp directory. But in real use case please use some other directory. 

The script starts the mesos master at localhost:5050. To see all the options for the script, run 

{% highlight bash %}
./bin/mesos-master.sh --help 
{% endhighlight %}

Same way, you can start mesos slave using *mesos-slave.sh*. You have to pass master info in order to mesos slave to discover it's master. To see all the options for the script, run

{% highlight bash %}
./bin/mesos-slave.sh --help 
{% endhighlight %}


### Step 5 : Testing single node cluster
Now we have single node cluster running. But how we can be sure that it is rightly configured to run tasks?. To test that, mesos provides few tests. We are going to use only the java test cases, as we are interested in Mesos Java API. Mesos also provides C++,Python
API's.

Run the following command to make sure mesos is rightly configured to run Java frameworks.

{% highlight bash %}
./src/examples/java/test-framework 127.0.0.1:5050
{% endhighlight %}
If the test framework is able to run all the tasks successfully you have the mesos single node cluster working.






