---
layout: post
title: "Scala Integration Testing with TestContainers Library"
date : 2020-03-20
categories: scala testing
---
Many of the times when we write Scala unit test cases, we need access to external services like databases, caches etc. Even though mocking works well for most of the cases, it's the not the same thing. So it will be desirable to write test cases against the actual services. These test cases are known as integration test cases.

## Challenge with Integration Test Cases
One of the biggest challenge with integration test case is to setup the environment correctly. For example, if we need to write test cases again Mysql we need to setup the database and connection etc. Making it available for every developer's environment including CI (continuous Integration) is tricky. This is one of the reasons where integration test are run only in CI environments. But this discourages individual developers to write them and test them in their local environments.

## Docker Based Environments
In recent years, using docker for setting up environment is becoming popular. Most of the databases, caches make their tools available as docker images. Most of the times CI tools will be setup using docker images. So if we need Mysql in CI, we will run the Mysql docker container. 

This still doesn't help in running these test cases in local machine. Expecting every developer to run the containers with right setup is not ideal. So most of these setup will be limited to CI systems. 

## Automating Docker Environment Setup

What if we can automate this docker based setup where the individual developer doesn't need to worry about the same? This makes integration tests as easy as unit test cases as the developer doesn't need to worry about setting up environments. Also now local and CI systems will behave exactly same. 

That's what **testcontainers** library helps to do. 

## Test Containers Library

TestContainers is a Java library which exposes running docker containers as test library. This exposes a simple library to run docker containers and interact with them as normal Java Library.

**testcontainers-scala** is a Scala port the same library which support ScalaTest integration.

In rest of the post, I will be discussing about how to use the library to run a test case running against mysql.


## Mysql Integration Testing

This section of the post, we will be discussing how to run integration tests in Scala which needs Mysql.

### Add Dependencies

The below dependencies should be added to build.sbt.

{% highlight scala %}
val testcontainersScalaVersion = "0.36.0"
"com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % "test",
"com.dimafeng" %% "testcontainers-scala-mysql" % testcontainersScalaVersion % "test",
{% endhighlight %}

First dependency is the scala library with scala test integration. The second dependency is Mysql specific. There is built-in support for a lot more [databases](https://www.testcontainers.org/modules/databases/).

### Mixing ForAllTestContainer Trait

There are mainly two traits to create containers. **ForAllTestContainer** creates a container per test suite. **ForEachTestContainer** creates container for each test case. As creating and destroying mysql container for each test case is costly, we will be using the former one for our test cases.

The below code is used for mixing the above trait in our test cases.

{% highlight scala %}

class MysqlTestSpec extends FlatSpec with ForAllTestContainer  with Matchers{

{% endhighlight %}

### Implementing container abstract method

When we mix the **ForAllTestContainer**, we are required to implement **container** method, which defines which container to create.

The below code implements the method by creating container.

{% highlight scala %}

override val container = MySQLContainer()

{% endhighlight %}

In above code, we are creating **MySQLContainer**. This constructor is available from the dependency we added earlier.


### Using Mysql Container in Test Case

Once the container is created, we can use that instance for running test cases. The below test case create a table and runs show table.

{% highlight scala %}

 it should "create table and list Table" in {

    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl,
      container.username, container.password)

    val createTableStatement = connection.prepareStatement("create table test(a  Int)")
    createTableStatement.execute()

    val preparedStatement = connection.prepareStatement("show tables")
    val result = preparedStatement.executeQuery()

    while (result.next()) {

      val tableName = result.getString(1)
      tableName shouldEqual "test"
    }
  }

{% endhighlight %}

From code we can observe that, we can connect to database using **container** variables like **jdbcUrl** so that we don't need to hard code connection strings. This gives maximum flexibility to run database on any port which is available.

### Running Test Case

When we run the test case, we can observe that it automatically pulls the image and runs a docker container for us. Before we run, let's see the docker containers running on our machines using **docker ps** command.

{% highlight text %}

CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES

{% endhighlight %}


When we run the test case, we can see the below containers by running same command

{% highlight text %}

CONTAINER ID        IMAGE                               COMMAND                  CREATED             STATUS              PORTS                     NAMES
15d602797c5e        mysql:5.7.22                        "docker-entrypoint.sh"   3 seconds ago       Up 2 seconds        0.0.0.0:32803->3306/tcp   sick_lalande
27cb3b50aa9d        quay.io/testcontainers/ryuk:0.2.3   "/app"                   3 seconds ago       Up 3 seconds        0.0.0.0:32802->8080/tcp   testcontainers-ryuk-75e25220-255a-4c39-a9fa-7e21cad2a78a

{% endhighlight %}

As you can see, we are running two containers. One containers is for our mysql and other one is for life cycle management. These containers automatically go away once test cases are done.


### Reusing Containers in Test Suite

As we discussed earlier, if we use **ForAllTestContainer** container state will preserved for all test cases. We can verify by listing tables in second test case which was created in first test case.

{% highlight text %}

it should "Checks the table exist as test cases are sharing same container" in {

      Class.forName(container.driverClassName)
      val connection = DriverManager.getConnection(container.jdbcUrl,
        container.username, container.password)

      val preparedStatement = connection.prepareStatement("show tables")
      val result = preparedStatement.executeQuery()

      while (result.next()) {
        val tableName = result.getString(1)
        tableName shouldEqual "test"
      }


  }

{% endhighlight %}

This test case passes as the same container will be preserved.


## Using Generic Container

Let's say you may have service which doesn't have built in support like we had for Mysql. Then what you can do?. 

The library exposes a generic container API called **GenericContainer** which allows running any container image. So you are not restricted by the built in services. You can read more about the same [here](https://www.testcontainers.org/features/creating_container/#examples).

## Code

You can access complete code [here](https://github.com/phatak-dev/ScalaExperiments/blob/master/src/test/scala/com/madhukaraphatak/scala/testcontainers/MysqlTestSpec.scala).
