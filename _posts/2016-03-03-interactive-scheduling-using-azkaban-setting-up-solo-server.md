---
layout: post
title: "Interactive Scheduling using Azkaban - Part 1 : Setting up Solo Server"
date : 2016-03-03
categories: scala azkaban
---
Azkaban is a scheduler for big data workloads. One of the differentiator of azkaban compared to other schedulers like oozie, airflow is it has good support for REST API to interact with scheduler interactively. So in these series of blogs I will be discussing about setting up azkaban and using azkaban AJAX(REST) API.

This is the first post in series, where we discuss about setting up azkaban. In this post, we will be setting up azkaban 3.0.

## Building Azkaban

Though azkaban provides binary [downloads](http://azkaban.github.io/downloads.html) it is not up to date. So we will be getting latest code from the github in order to build azkaban 3.0. 

The following are the steps to get code and build.

* ###Clone code
{% highlight sh %}
 git clone https://github.com/azkaban/azkaban.git
{%endhighlight%}

* ###Build
{% highlight sh %}
./gradlew distZip
{%endhighlight%}

* ###Copy from build 

{% highlight sh %}
  cp build/distributions/azkaban-solo-server-3.0.0.zip ~
{%endhighlight%} 

## Installing solo server

Azkaban supports different mode of executions like solo server, two server mode and multiple executor mode. Solo server is used for initial developments where as other ones are geared towards production scenarios. In this blog, we discuss about setting up solo server, for other modes refer [azkaban documentation](http://azkaban.github.io/azkaban/docs/latest/#getting-started).

The below are the steps for installing.

* ###Unzip 
{% highlight sh %}
unzip ~/azkaban-solo-server-3.0.0.zip
cd ~/azkaban-solo-server-3.0.0
{%endhighlight%} 

* ###Starting solo server

{% highlight sh %}
bin/azkaban-solo-start.sh
{%endhighlight%} 
  
* ###Accessing log

{% highlight sh %}
tail -f logs/azkaban-execserver.log
{%endhighlight%} 


## Accessing web UI

 Once azkaban solo server started, you can access at [http://localhost:8081](http://localhost:8081/). By default username is *azkaban* and password is *azkaban*. You can change it in *conf/azkaban-users.xml*.

 Now you have successfully installed azkaban server. In the next set of posts, we will explore how to use this installation to do scheduling.