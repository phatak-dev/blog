---           
layout: post
title: "Apache YARN Hello world in Scala"
date : 2014-12-21
categories: yarn scala hadoop
---

Apache YARN is  *Yet Another Resource Negotiator* for distributed systems. It's a distributed system resource scheduler similar to mesos. Yarn was created as effort to diversify the hadoop for different use cases. Yarn is available in all hadoop 2.x releases.

In this post, we are going to discuss about how to run a scala program in yarn. You may have seen [distributed shell example ](https://github.com/hortonworks/simple-yarn-app) which run shell commands on yarn. This example extends that code to run scala programs in place of shell commands. 

If you are new to yarn please go through [YARN architecture](http://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html) before continuing.

## Yarn and Scala
Yarn is written in Java. So the API it exposes is primarily in java. There is no special support for Scala. We just use the java api in our example. 

tl;dr Access the complete code on [github](https://github.com/phatak-dev/blog/tree/master/code/YarnScalaHelloWorld).


The following are the steps to write a YARN application which runs scala helloworld program on hadoop cluster.

## Step 1 : Add Yarn dependencies 

{% highlight xml %}

<dependency>
  <groupId>org.apache.hadoop</groupId>
  <artifactId>hadoop-common</artifactId>
  <version>2.2.0</version>
 </dependency>
 <dependency>
 <groupId>org.apache.hadoop</groupId>
 <artifactId>hadoop-yarn-client</artifactId>
 <version>2.2.0</version>
</dependency>

{% endhighlight %}

I am adding version 2.2.0 as I have that version installed on my system. If you have different version of hadoop installed please change accordingly.

*hadoop-yarn-client* dependency contains all protocols to talk to resource manager and node manager . We need *hadoop-common* to do hdfs operations.

## Step 2 : Yarn Client

For every Yarn application, there will be a client which will launch application specific master.

So let's start implementing one

### Step 2.1 : Start YARN client

First we have to start a YarnClient, which will talk to Resource manager on our behalf.

{% highlight scala %}

val client = YarnClient.createYarnClient()
client.init(conf)
client.start()

{% endhighlight %}


### Step 2.2 : Specify command to launch Application master

{% highlight scala %}

 val app = client.createApplication()
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    //application master is a just java program with given commands
    amContainer.setCommands(List(
      "$JAVA_HOME/bin/java" +
        " -Xmx256M" +
        " com.madhukaraphatak.yarn.helloworld.ApplicationMaster"+
        "  " +jarPath +"   "+ numberOfInstances + " "+
        " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
        " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
    ).asJava)

{% endhighlight %}

Launching an application master is just running a command from shell. Yarn will not know anything about application or it's environment. So you have to specify the complete command how to launch the application master.

Please note that we call  *asJava* to convert scala list to java. The reason being all Yarn API take Java collections.

Now you may be wondering, how YARN will get the code which contains this main class to launch. That's the next step

### Step 2.3 : Add the application jar to local resource

{% highlight scala %}
 val appMasterJar = Records.newRecord(classOf[LocalResource])
 setUpLocalResource(new Path(jarPath), appMasterJar)
 amContainer.setLocalResources(Collections.singletonMap("helloworld.jar", appMasterJar))

{% endhighlight %}

Here we instruct the YARN to make the specific jar available in classpath when we launch the application master. These jars has to be there in HDFS not on your local system. How to copy and specify the path we will see in running section.

### Step 2.4: Add hadoop and yarn jars to classpath

As our code depends on hadoop and yarn api, we have to add them to classpath. The following code does that.

{% highlight scala %}

def setUpEnv(env: collection.mutable.Map[String, String])
(implicit conf:YarnConfiguration) = {
 
 val classPath =  conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,YarnConfiguration.
 DEFAULT_YARN_APPLICATION_CLASSPATH:_*)

 for (c <- classPath){
    Apps.addToEnvironment(env.asJava, Environment.CLASSPATH.name(),
        c.trim())
 }
      Apps.addToEnvironment(env.asJava,
      Environment.CLASSPATH.name(),
      Environment.PWD.$() + File.separator + "*")
 }

{% endhighlight %}

We fill up our env map using the jar name from *YARN classpath* 

{% highlight scala %}
  amContainer.setEnvironment(env.asJava)
{% endhighlight %}

Once we have map, set the map as environment for application master.

### Step 2.5: Specifying resource requirement for Application master

Everything in YARN runs on a container which consumes part of resources on cluster. So before launching any container you have to specify how much resource it needs. 

{% highlight scala %}
 
 val resource = Records.newRecord(classOf[Resource])
 resource.setMemory(300)
 resource.setVirtualCores(1)

{% endhighlight %}

Here we are telling to YARN that we need 300 mb of memory and one cpu to run our application master.


### Step 2.5: Setup the context and submit the application

Once everything is ready, create an application submission context which will request a new application id from RM. Then submit the application.

{% highlight scala %}

val appContext = app.getApplicationSubmissionContext
appContext.setApplicationName("helloworld")
appContext.setAMContainerSpec(amContainer)
appContext.setResource(resource)
appContext.setQueue("default")

//submit the application
val appId = appContext.getApplicationId
println("submitting application id" + appId)
client.submitApplication(appContext)

{% endhighlight %}

Find complete code listing here.


## Step 3 : Application master

Application Master is a simple java program which runs in YARN container. Application master is responsible for talking to RM and NM to request for containers to run the tasks. Here our task is to run our hello world program.

### Step 3.1 : Start RM and NM client

We have to start RM and NM client in order to talk to these components.

{% highlight scala %}

// Create a client to talk to the RM
val rmClient = AMRMClient.createAMRMClient().asInstanceOf[AMRMClient[ContainerRequest]]
rmClient.init(conf)
rmClient.start()
rmClient.registerApplicationMaster("", 0, "")

//create a client to talk to NM
val nmClient = NMClient.createNMClient()
nmClient.init(conf)
nmClient.start()

{% endhighlight %}


### Step 3.2 : Request for containers
Once we have established communication to RM and NM, we will request for containers which allows us to run our program. No.of containers is specified as command line argument. If you specify more than 1, hello world runs more than ones.

{% highlight scala %}

for ( i <- 1 to n) {
 val containerAsk = new ContainerRequest(resource,null,null,priority)
 println("asking for " +s"$i")
 rmClient.addContainerRequest(containerAsk) }
    
{% endhighlight %}


### Step 3.3 : Waiting for Container allocation

Whenever you request for containers in Yarn, they will be not allocated immediately. If there is high traffic on cluster, your application has to wait till the resources are free.

### Step 3.4 : Launch Hellworld on allocated container

Once resources are available, YARN will allocate requested containers. Once we have container we will launch the our hello world. Setting up jar and environment is exactly same like client.

{% highlight scala %}

while( completedContainers < n) {

val appMasterJar = Records.newRecord(classOf[LocalResource])
setUpLocalResource(new Path(jarPath),appMasterJar)

val env = collection.mutable.Map[String,String]()
setUpEnv(env)

val response = rmClient.allocate(responseId+1)
responseId+=1

for (container <- response.getAllocatedContainers.asScala) {
val ctx =
Records.newRecord(classOf[ContainerLaunchContext])
ctx.setCommands(
List(
"$JAVA_HOME/bin/java" +
" -Xmx256M " +
" com.madhukaraphatak.yarn.helloworld.HelloWorld" +
" 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
" 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
 ).asJava
)

ctx.setLocalResources(Collections.singletonMap("helloworld.jar",
appMasterJar))
ctx.setEnvironment(env.asJava)

System.out.println("Launching container " + container)
nmClient.startContainer(container, ctx)

}
{% endhighlight %}


## Step 4 : Hello world program

Our hello world is just simple scala class.

{% highlight scala %}
object HelloWorld {
 def main(args: Array[String]) {
    println("helloworld")
 }
}
{% endhighlight %}


## Step 5 : Build

Download code from [here]() and run *mvn clean install*

## Step 6 : Running

* Create jars folder
 {% highlight sh %}
 hdfs dfs -mkdir /jars
 {% endhighlight%} 

* Put the jar file in /jars
 {% highlight sh %}
 hdfs dfs -put <jar-path> /jars
 {% endhighlight%} 

* Run the code
 {% highlight sh %}
 hadoop jar <jar-path>  com.madhukaraphatak.yarn.helloworld.Client hdfs://<namenode-host:namenode-port>/jars/yarn-helloworld-scala-1.0-SNAPSHOT.jar 1
 {% endhighlight%} 

If everything runs fine, you should see hello world in logs available in *$HADOOP_HOME/logs/userlogs/<application-id>/*.
