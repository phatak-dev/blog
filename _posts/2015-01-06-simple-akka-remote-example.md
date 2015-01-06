---           
layout: post
title: "A Simple Akka Remote example"
date : 2015-01-06
categories: akka scala
---

It's hard to find a simple example that shows how to do akka remoting. Akka [remote](http://doc.akka.io/docs/akka/snapshot/scala/remoting.html) documentation is highly cryptic. Other [blogs](http://alvinalexander.com/scala/simple-akka-actors-remote-example) and [stackoverflow](http://stackoverflow.com/questions/14934782/akka-2-1-minimal-remote-actor-example) answers are not much help as they makes you to create multiple projects and follow non obvious steps. 

So I was thinking there is should be an easier way to do this. It should be as simple as having two actors in a given project talking through remote interface. After digging through akka documentation I was able to create one such example. In this post, I am going to discuss about that.


tl;dr Access complete code on [github](https://github.com/phatak-dev/akka-remote-simple-scala).

The following are the steps to create a simple akka remote project.

## Step 1: Create a sbt project

Create a single sbt project using IDE or any other tools. Once you created the project, add the following dependencies.

{% highlight scala %}

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.3.7"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-remote" % "2.3.7"


{% endhighlight %}


Access complete code [here](https://github.com/phatak-dev/akka-remote-simple-scala/blob/master/build.sbt).

Once we have the dependencies in place, let's start with actors.

## Step 2 : Create Remote actor
Remote actor is an actor which listens on some given port. The following are the steps to create a remote actor.


### Step 2.1 Define Remote Actor

{% highlight scala %}
 class RemoteActor extends Actor {
  override def receive: Receive = {
    case msg: String => {
      println("remote received " + msg + " from " + sender)
      sender ! "hi"
    }
    case _ => println("Received unknown msg ")
  }
}
{% endhighlight %}

As you can see, there is nothing special about this actor. It's like any other actor. So the magic of remoting should be happening somewhere else.

### Step 2.2 : Define a remote configuration

Akka uses configuration to define how to instantiate actor systems. If you define actor system as remote, then all the actors running in that system will become remote actors.

Create a file name "remote_application.conf" in resources folder. Then place following code

{% highlight scala %}
 akka {
  loglevel = "INFO"
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = "127.0.0.1"
      port = 5150
    }
    log-sent-messages = on
    log-received-messages = on
  }
}
{% endhighlight %}

In this configuration, we are specifying following things.

* Actor ref provider : We are specifying the references should be remote aware.
* Transports used : tcp is the transport layer protocol used
* hostname : 127.0.0.1 
* port : 5150 is the port on which the actor system listen on

other details are for logging purposes. As you can see, we want the remote actor to be listening on this specific port so that it can be discoverable for other clients.

By convention akka looks for *application.conf* in class path for configuring actors. But as we have multiple actors which should listen on different ports, we are going to explicitly parse the configuration and pass it to the actor system.



### Step 2.3 Configuring Actor system to listen on remote

* Get the file path from classpath

{% highlight scala %}
  val configFile = getClass.getClassLoader.
  getResource("remote_application.conf").getFile
{% endhighlight %}


* Parse the config file to create config object

{% highlight scala %}
 val config = ConfigFactory.parseFile(new File(configFile))
{% endhighlight %}

* Create an actor system with this config

{% highlight scala %}
val system = ActorSystem("RemoteSystem" , config)
{% endhighlight %}

As you can observe, the configuration is always set in the level of actor system, not at actors level.

* Create an instance of remote actor using this actor system

{% highlight scala %}
val remote = system.actorOf(Props[RemoteActor], name="remote")
{% endhighlight %}

Once you create actor, it will be available at *akka:tcp://RemoteSystem@127.0.0.1:5150/user/remote*.

Access complete code [here](https://github.com/phatak-dev/akka-remote-simple-scala/blob/master/src/main/scala/com/madhukaraphatak/akka/remote/RemoteActor.scala).

Once we are done with remote actor, let's create local actor.

## Step 3 : Local actor

The following are steps to create local actor

### Step 3.1 : Define local actor

{% highlight scala %}
 class LocalActor extends Actor{
  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    /*
      Connect to remote actor. The following are the different parts of actor path

      akka.tcp : enabled-transports  of remote_application.conf

      RemoteSystem : name of the actor system used to create remote actor

      127.0.0.1:5150 : host and port

      user : The actor is user defined

      remote : name of the actor, passed as parameter to system.actorOf call

     */
    val remoteActor = context.actorSelection("akka.tcp://RemoteSystem@127.0.0.1:5150/user/remote")
    println("That 's remote:" + remoteActor)
    remoteActor ! "hi"
  }
  override def receive: Receive = {

    case msg:String => {
      println("got message from remote" + msg)
    }
  }
}

{% endhighlight %}

In the prestart, we will connect to the remote actor using *context.actorSelection* api. Comments in code explain different section of URL. Once we are able to connect, we will send messages.


### Step 3.2 : Configuration file for local actor
As with remote actor, we need to specify the configuration file. We call it "local-configuration.conf"

{% highlight scala %}

  loglevel = "INFO"
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = "127.0.0.1"
      port = 0
    }
    log-sent-messages = on
    log-received-messages = on
  }
}

{% endhighlight %}

You may be wondering why local actor needs remote ref provider. The reason is, in akka actors behave like peers rather than client-server. So both local and remote actors should talk on similar transport. So the only difference between remote and local, is which machine they running. If it's running in remote machine, then it's a remote actor and if it's in your machine then it's local actor.

The configuration is exactly same other than the port. Port *0* means any free port.

Configuring the local actor is exactly same as remote actor.

Access complete code [here](https://github.com/phatak-dev/akka-remote-simple-scala/blob/master/src/main/scala/com/madhukaraphatak/akka/local/LocalActor.scala).

## Step 4 : Building and running

* Download complete code from [github](https://github.com/phatak-dev/akka-remote-simple-scala)

* Run *sbt install*

* Run main methods of Remote and Local actors.

Now you have an Akka remote example working without any hassles.





