---           
layout: post
title: "Apache Tuscany : Part 2 -Hello World using Eclipse and Maven"
date: 2011-06-22 14:14:41 UTC
updated: 2011-06-22 14:14:41 UTC
comments: false
categories: eclipse tuscany maven
---

This is the second article in series “ Apache Tuscany” which introduces you to the open source SCA framework Apache tuscany. This post talks about hello world with Apache Tuscany.

###Prerequisites

1. Eclipse 3.4 or above
2. Maven Eclipse Plug in
3. [Apache tuscany Eclipse plugin](apache-tuscany-part-1-installing-tuscany-plug-in-in-eclipse.html)
4. Java 1.6

###Installing Tuscany distribution

* Download Apache tuscany 2.0-M5 from the following [link](http://tuscany.apache.org/sca-java-2x-releases.html)

*  Untar(zip) the downloaded tuscany distribution to a directory location. We will refer this location as TUSCANY_HOME

###Creating a Maven Project in Eclipse

* Goto File->New->Other 
* Select Maven project and press Next
* Just keep default setttings and press Next
* tuscany-contribution-jar as Artifact and press Next

Enter following details

> Group id : com.demo

> Artifact Id : sca

>Package : com.demo.sca

Press Finish . Now you can see a maven project in your eclipse workspace

### Source code

####1.Interface

The project creates a interface called as HelloWorldService . This interface is just a java interface having @Remotable annotation. This annotation is used to make interface a remotable interface .

#####2.Implementation Class

HelloWorldImpl class implements the remotable interface . It uses @Scope(“COMPOSITE”) which says this class (or component) will act as the composite service provider.

####3. Composite File

In resource folder , a HelloWorld.composite file will be created. This file will define the configuration for the compositions. Replace the code in HelloWorld.composite by below code.

{%highlight xml%}
<?xml version="1.0" encoding="UTF-8"?>
<composite xmlns="http://docs.oasis-open.org/ns/opencsa/sca/200912"
xmlns:tuscany="http://tuscany.apache.org/xmlns/sca/1.1"
targetNamespace="http://com.demo.sca"
name="hello-sca2">

<component name="HelloworldComponent">
<implementation.java class="com.demo.sca.HelloworldImpl"/>
<service name="HelloworldService">
<interface.java interface="com.demo.sca.HelloworldService" />
<binding.ws uri="http://localhost:8080/HelloWorld" />
</service>
</component>

</composite>
{% endhighlight%}

This creates a Web service at URL http://localhost:8080/HelloWorld  
Note that the service name should be as same as the Interface Name


####4.Contribution file  
In META-INF , there is a file filed name sca-contribution.xml .It used to specify which component will be used as launching component. Replace the code by below code

{%highlight xml%}
<?xml version="1.0" encoding="UTF-8"?>

<contribution xmlns="http://docs.oasis-open.org/ns/opencsa/sca/200912"
xmlns:sample="http://com.demo.sca">
<deployable composite="sample:hello-sca2"/>
</contribution>
{% endhighlight%}

####5.Building the project  
Right click on the project and select Run as >Maven Install . It will create a jar file containing the classes of the project.

####6.Running the project  
In this example , we will run the project outside of eclipse . Copy the helloworld.jar to $TUSCANY_HOME/bin

* In windows  

> cd $TUSCANY_HOME /bin(replace by actual path)  
>  ./tuscany.bat helloworld.jar

* In linux     

> $ cd $TUSCANY_HOME/bin ( (replace by actual path)  
>  $./tuscany.sh helloworld.jar


If there are no errors, tuscany will create a web service at http://localhost:8080/HelloWorld



