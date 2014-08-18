---           
layout: post
title: "Sbt on ubuntu"
date : 2014-08-18
categories: scala
---
[Sbt](http://www.scala-sbt.org/) is the default build tool for Scala. This post talks about steps to install sbt on ubuntu.

##Steps to install Sbt on ubuntu
{%highlight  sh %}

#Add typesafe repository     
wget http://apt.typesafe.com/repo-deb-build-0002.deb `

#Install the repository
sudo dpkg -i repo-deb-build-0002.deb

#Refresh
sudo apt-get update

#Install sbt
sudo apt-get install sbt

#check whether sbt is installed
sbt 

{% endhighlight %}

##Add specific version of sbt

Some project need specific version of sbt like 0.13.   

If you get error saying __Detected sbt version 0.12.2 Cannot find sbt launcher 0.12.2 Please download: From typesafe.artifactoryonline.com/typesafe/ivy-releases/... To /home/{username}/.sbt/.lib/0.12.2/sbt-launch.jar__

Run the following commands by replacing <username> with you username.

{%highlight  sh %}
  mkdir -p /home/{username}/.sbt/.lib/0.12.2/ 
 wget -O /home/{username}/.sbt/.lib/0.12.2/sbt-launch.jar 
 http://typesafe.artifactoryonline.com/typesafe/ivy-releases/
 org.scala-sbt/sbt-launch/0.12.2/sbt-launch.jar
{% endhighlight %}





