---           
layout: post
title: "Mesos Hello world in Scala"
categories: mesos
---

In previous [post](/mesos-single-node-setup-ubuntu), we installed mesos on ubuntu. In this post we are going to look at Simple hello world on mesos. Here we are going to code a distributed shell which run shell commands on any machine on the mesos cluster.

This post assumes that you have running mesos. If not install and configure mesos do it using [this](/mesos-single-node-setup-ubuntu) post.

## Mesos framework 

Every application running on mesos is called as framework. In our example, our framework will be called as Distributed shell and it will coded using Mesos Java API.


## Mesos framework components
Every mesos framework has three important components
 
* **Client**    
Client is code which submits the tasks to the framework.


* **Scheduler**    
   This is the important piece of the framework. A scheduler implement Mesos Scheduler interface. Scheduler takes tasks from client and process on mesos


* **Executor**    
 Executor sets up environment to run each task given by client. Scheduler uses this executor to run each task. In this example we will use default executor provided by the mesos.



If you want you can read more about the mesos architecture [here](http://mesos.apache.org/documentation/latest/mesos-architecture/). 

## Distributed shell on Mesos
   
### Client
   
Mesos client is a simple scala program which create instance of Scheduler and submit tasks to it.

Mesos uses Google protocol buffer internally to do serialization. So each data structure in mesos follows builder design pattern. 

The following are the steps to create the client
   
* Create framework info

{%highlight scala %} 
val framework = FrameworkInfo.newBuilder.
                setName("DistributedShell").
                setUser("").
                setRole("*").
                setCheckpoint(false).
                setFailoverTimeout(0.0d).
                build()
{%endhighlight%}

* Create instance of Scheduler 
   
{%highlight scala %} 
val scheduler = new ScalaScheduler
{%endhighlight%}

* Submit tasks 
{%highlight scala %} 
scheduler.submitTasks(args:_*)
{%endhighlight%}

* Start mesos driver with our scheduler and framework 

{%highlight scala %} 
//url pointing to mesos master
val mesosURL = "localhost:5050"
val driver = new MesosSchedulerDriver(scheduler,
    framework,mesosURL)
//run the driver
driver.run()
{%endhighlight%}
   
You can access complete code listing [here](https://github.com/phatak-dev/mesos-helloworld-scala/blob/master/src/main/scala/com/madhu/mesos/DistributedShell.scala).

### Scheduler

ScalaScheduler extends Mesos interface "Scheduler". There are many call backs we have to only concentrate on resourceOffers.

* Create resource request for the task
   
{%highlight scala %} 
val cpus = Resource.newBuilder.
           setType(org.apache.mesos.Protos.Value.Type.SCALAR).
           setName("cpus").
           setScalar(org.apache.mesos.Protos.Value.
           Scalar.newBuilder.setValue(1.0)).
           setRole("*").
           build
{%endhighlight%}

  
* Generate task id
{%highlight scala %} 
val id = "task" + System.currentTimeMillis()
{%endhighlight%}

* Create taskinfo using command
{%highlight scala %} 
val task = TaskInfo.newBuilder.
           setCommand(cmd).
           setName(id).
           setTaskId(TaskID.newBuilder.setValue(id)).
           addResources(cpus).
           setSlaveId(offer.getSlaveId).
           build
{%endhighlight%}   

  * Launch the tasks
 
  {%highlight scala %} 
    driver.launchTasks(offer.getId, List(task).asJava)
  {%endhighlight%}       

You can access complete code listing [here](https://github.com/phatak-dev/mesos-helloworld-scala/blob/master/src/main/scala/com/madhu/mesos/ScalaScheduler.scala).

## Running

Every mesos application needs mesos shared directory to available in path. So you can build using

{%highlight bash %} 
mvn clean install
{%endhighlight%}       

Run like 
{%highlight bash %} 
java -cp target/Mesos-0.0.1-SNAPSHOT.jar 
-Djava.library.path=$MESOS_HOME/src/.libs 
com.madhu.mesos.DistributedShell "/bin/echo hello"  
{%endhighlight%}       



  









