---
layout: post
title: "Akka HTTP Hello world"
date : 2015-11-13
categories: scala akka-http
---

Akka HTTP is a akka based http library for building RESTful services in Scala.
It's is based on new Akka reactive streams library. 

In this series of posts, I will be talking about how to build REST services using akka-http library.

This is the first post in the series, where I will be talking about setting up the project and running a hello world program.

TL;TR You can access complete code on [github](https://github.com/phatak-dev/akka-http-examples).

## Adding dependency

To start using akka-http, you should add the following dependency to project. I am using sbt for build management. You can also use other build tools like maven.

{% highlight scala %}
"com.typesafe.akka" %% "akka-http-experimental" % "1.0"
{%endhighlight%}

Though the artifact name says it's experimental many companies are using it in the production.

You can access complete build.sbt [here](https://github.com/phatak-dev/akka-http-examples/blob/master/build.sbt)

## HelloWorld application

The following are the steps to be followed to create a hello world application.

### 1. Create Actor System

Akka HTTP uses akka actors for handling concurrent requests. So in the first line we have to create akka actor system.

{% highlight scala %}

implicit val actorSystem = ActorSystem("system")

{%endhighlight%}

### 2. Create ActorFlowMaterilizer

Akka HTTP uses akka reactive streams for stream processing on TPC. So in a reactive system,we need to specify flow materializer which specifies the how requests/repose flow get processed. In akka-http, actors will be used for handling request and response flows. So we use *ActorMaterializer* here. 

{% highlight scala %}

implicit val actorMaterializer = ActorMaterializer()

{%endhighlight%} 

Please make sure both of the variables are implicits. Otherwise you may get strange cryptic compilation errors.

### 3. Defining the route

Route specifies the URI endpoints REST server exposing. It is combination of multiple paths.

A simple path has the following three parts
	
* Directive/URI
* HTTP Method
* Response

{% highlight scala %}

val route =
  pathSingleSlash {
    get {
      complete {
        "Hello world"
      }
    }
  }

{%endhighlight%} 

In above example, we are creating a route with following properties
   
   * / is the directive
   * get is HTTP Method
   * Returns "hello world" as the response

### 4. Bind 

Once we have all pieces in place, we need to create a server and bind to this route, address and port. 

{% highlight scala %}
 
Http().bindAndHandle(route,"localhost",8080)

{%endhighlight%}

This creates and runs a simple http server at [http://localhost:8080](http://localhost:8080).

You can access complete code for this server [here](https://github.com/phatak-dev/akka-http-examples/blob/master/src/main/scala/com/madhukaraphatak/akkahttp/AkkaHttpHelloWorld.scala). 

Now we have a working simple http server using akka-http library.