---           
layout: post
title: "Running multiple Mesos slaves in single machine"
categories: mesos
---

Install mesos on machine using this [guide](http://mesos.apache.org/gettingstarted/). This guide assumes the mesos is installed in <mesos_download_path>/build and MESOS_HOME point to it.

#Starting master

	bin/mesos-master.sh --work_dir=/home/madhu/Dev/spark/mesos_work --ip=127.0.0.1

#Differentiating slaves
 
 * port  
 * hostname    
 * work-dir  

#Create work directories

    mkdir -p /tmp/mesos/mesos_slave_work/slave1          
	mkdir -p /tmp/mesosmesos_slave_work/slave2 

#Configure /etc/hosts

127.0.1.2 &nbsp; &nbsp; &nbsp; slave1      
127.0.1.3 &nbsp;&nbsp; &nbsp;  slave2

#Start the slaves 

MESOS_HOME/bin/mesos-slave.sh --master=localhost:5050 --port=5053 --hostname=slave1  --work_dir=/tmp/mesos/mesos_slave_work/slave1

MESOS_HOME/bin/mesos-slave.sh --master=localhost:5050 --port=5054 --hostname=slave2  --work_dir=/tmp/mesosmesos_slave_work/slave2 

#Constraining resources
--resources="cpus(*):2; mem(*):3500"







