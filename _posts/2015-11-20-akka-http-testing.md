---
layout: post
title: "Akka HTTP testing"
date : 2015-11-20
categories: scala akka-http
---

Akka-Http is a akka based http library for building RESTful services in scala. In this series of posts, I will be talking about using akka-http to build REST services. This is the third post in the series. You can access all the posts in this series [here](/categories/akka-http/).

In this post, we are going to discuss testing REST API's in akka-http.

TL;TR You can access complete project on [github](https://github.com/phatak-dev/akka-http-examples).

## Testing in Akka HTTP

Akka HTTP puts a lot of focus on testability of code. It has a dedicated module *akka-http-testkit* for testing rest api's. When you use this testkit you are not need to run external web server or application server to test your rest API's. It will do all needed the stubbing and mocking for you which greatly simplifies the testing process.

In this post, first we are going to discuss how to structure our code which can be easily testable with akka testkit. Once we have structured code, then we will discuss how to write unit test cases which tests the behavior of the rest API.

## Adding dependency

You need to add akka-http-testkit library to test your rest services.

{% highlight scala %}
"com.typesafe.akka" %%"akka-http-testkit-experimental" % "1.0",
{%endhighlight%}

## Code structure

Before we can do any unit testing, structuring our code in a way which can allow us to unit test is very important. The below gives one of the way to structure your REST API's. Please note that it's one of the many structuring schema. You can follow any other ones which gives you same effect.

Normally we divide our REST API to two following pieces
  
  * RestService - Defines the route for the rest service.
  * Rest server - Defines and creates the environment need to run the rest service.

This kind of way separating concerns of the API allows us to decouple the environment in which the rest service actually runs. In testing, it runs in an emulated server and in production it may runs inside an application server or it's own server.

The following sections discusses a simple API which we use to a simple customer. We have already discussed about the details of the API in [previous](/json-in-akka-http) post.

### Rest service
{% highlight scala %}

  trait RestService {
   implicit val system:ActorSystem
   implicit val materializer:ActorMaterializer

   val list = new ConcurrentLinkedDeque[Customer]()

   import ServiceJsonProtoocol._
   val route =
      path("customer") {
         post {
            entity(as[Customer]) {
               customer => complete {
                  list.add(customer)
                  s"got customer with name ${customer.name}"
               }
            }
         } ~
           get {
              complete {
                list.asScala
              }
           }
      }
}
{%endhighlight %} 

The above code defines a trait called *RestService* . Normally a service is a trait because it has to be mixed with some class/object to give the environment. The environment expected by the service includes

* system - Actor System on which this service runs
* materializer - Flow materializer as discussed in [earlier](/akka-http-helloworld) blog posts.

These values are implicits. This means we inject these externally when we instantiate this service. This is one of the way to dependency injection in scala. 

You can access complete code [here](https://github.com/phatak-dev/akka-http-examples/blob/master/src/main/scala/com/madhukaraphatak/akkahttp/testable/RestService.scala).

Once we have rest service ready, now we can define a REST server which serves this service.

### REST server

{%highlight scala %}
class RestServer(implicit val system:ActorSystem,
implicit  val materializer:ActorMaterializer) extends RestService{
  def startServer(address:String, port:Int) = {
    Http().bindAndHandle(route,address,port)
  }
}

object RestServer {

  def main(args: Array[String]) {

    implicit val actorSystem = ActorSystem("rest-server")
    implicit val materializer = ActorMaterializer()

    val server = new RestServer()
    server.startServer("localhost",8080)
  }
}

{%endhighlight%}

The above code creates a REST server which extends our *RestService*. If you observe the code, we are creating and injecting both actor system and actor materializer. 

This way of separating service and server allows us to inject these environment from test cases as shown in below.

You can access complete code [here](https://github.com/phatak-dev/akka-http-examples/blob/master/src/main/scala/com/madhukaraphatak/akkahttp/testable/RestServer.scala).

### Testing Rest API

#### 1. Create Spec with ScalatestRouteTest

{% highlight scala%}
class RestSpec extends WordSpec with Matchers with ScalatestRouteTest with RestService{
  
}
{% endhighlight %}

The above code uses scala-test for testing. In our spec, we mix *ScalatestRouteTest* which comes from akka-http-testkit library. It provides the actor system and flow materializer for test environment. Also we extend our *RestService* from where we get access to route.

#### 2. Prepare the request 


{%highlight scala%}
 
 "Customer API" should {
    "Posting to /customer should add the customer" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "name":"test"
           |}
        """.stripMargin)

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/customer",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

{% endhighlight %}

Once we prepare the spec, we prepare the POST request. The above code shows how to create HTTP post request, using HttpRequest API akka-http models.

#### 3. Send request 

Once we have the request, we can send the request using ~> operator as below code.

{%highlight scala%}

      postRequest ~> route ~> check {
         status.isSuccess() shouldEqual true
     
{% endhighlight %}

Once we send request, we can test results using many check methods. In our code, we are using status to check is our request returned 200 response. You can not only check for status, you can also test different pieces like response headers, response entity etc.

You can access complete code [here](https://github.com/phatak-dev/akka-http-examples/blob/master/src/test/scala/com/madhukaraphatak/akkahttp/testable/RestSpec.scala).

Now you have a rest service which can be easily unit tested.