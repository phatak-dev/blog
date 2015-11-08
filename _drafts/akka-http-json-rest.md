---
layout: post
title: "Akka Http JSON REST API"
date : 2015-11-08
categories: scala akka-http
---

Akka-Http is a akka based http library for building RESTful services. In this series of posts, I will be talking about using akka-http. This is second post in the series. You can access all the posts here.

In this post, we are going to discuss how to parse json entities in akka-http. 

tl;tr You can access complete project on github.

## Adding depenedency

Akka-Http uses the spray-json for parsing json request and responses. So add the following dependency for your project.

{% highlight scala %}
"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0"
{%endhighlight%}

Though the artifact name says it's experimental many companies are using it in the production.

## Building JSON REST API

In this example, we are going to create an API which can add/list customers. The following are the steps to add the API

### Define customer model 

Akka-Http uses case classes to define the models. So we define a simple customer model which has only name.
{% highlight scala %}

case class Customer(name: String)

{%endhighlight%}

### 2. Create Writer/Reader for the model

In spary, we have to define the write/reader for a given model in order to be used in the request/response. Most of the time it is very simple as defining the implicit as below.


{% highlight scala %}

object ServiceJsonProtoocol extends DefaultJsonProtocol {
    implicit val customerProtocol = jsonFormat1(Customer)
  }

{%endhighlight%} 

In the above code snippet, we are creating a service protocol which extends the default protocol. Inside this protocol, we can define all the models. In above code, the number '1' in jsonFormat1 signifies there is only one field in case class. 

### 3. Defining the route

Once we have the model and parser implicits, we can define a route which has post for adding a customer and get for get all the added customers.

{% highlight scala %}

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
            JsArray(list.asScala.toList.map(_.toJson):_*)
        }
      }
    }

{%endhighlight%} 

In above example, an entity is parsed in post request using entity(as).
Once we have the customer, we are adding it to a concurrent queue.

In the get method, we are taking all the values in the list and converting to a JSArray.

Now you have a working REST api which can handle json data.