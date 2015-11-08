---
layout: post
title: "Akka Http Hello world"
date : 2015-11-08
categories: scala akka-http
---

Akka-Http is a akka based http library for building RESTful services.
It's is based new Akka reactive streams. It's also next version of spary.io.

In this series of post, I will be talking about how to build REST services using Akka-Http libraries.

This is the first post in the series, where I will be talking about setting up the project and running a helloworld program.

tl;tr You can access complete project on github.

## Adding depenedency

The following dependency should be added to project. I am using sbt for build management. You can also maven for same.

{% highlight scala %}
"com.typesafe.akka" %% "akka-http-experimental" % "1.0"
{%endhighlight%}

Though the artifact name says it's experimental many companies are using it in the production.

## HelloWorld 


### 1. Create Actor System

Akka-Http is based on spary.io which in turns based Akka Actor system. Akka actor system is used for controlling actuall handling of the requests. So in the first line we have to create akka actor system.

{% highlight scala %}

implicit val actorSystem = ActorSystem("system")

{%endhighlight%}

### 2. Create ActorFlowMaterilizer

Akka-Http is also uses Akka reactive streams. So in a reactive system,
we need to specify flow materializer. In akka-http, actor will be used
to flow materializer. 

{% highlight scala %}

implicit val actorMaterializer = ActorMaterializer()

{%endhighlight%} 

Please make sure both of the variables are implicits. Otherwise you may get strange cryptic errors.

### 3. Defining the route

Route is like a web.xml in J2EE world, which define how the given path has to be served.

A simple path has the following three parts
	
* Directive 
* Http Method
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
   * get is Http Method
   * Return "hello world" as the response

### 4. Bind 

Once we have all pieces in place, we need to create a server and bind to this route, address and port. 

{% highlight scala %}
 
Http().bindAndHandle(route,"localhost",8080)

{%endhighlight%}

This creates and runs a simple http server at [http://localhost:8080](http://localhost:8080)

 