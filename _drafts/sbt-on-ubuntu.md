---           
layout: post
title: "Sbt on ubuntu"
categories: scala
---
Sbt is official build tool used by Scala ecosystem project. So as you have maven on system
whenever you deal with java projects, it's handy to have sbt on path to do thing with
scala projects. Some days back i need to do some scala developement and I chose a different
path of using Sbt rather than maven as usual. I need to have sbt to compile the project
and getting sbt on ubuntu was not that straight forward. So after going through few blog
posts and Stackover flow , i thought I document in this post

Steps to install Sbt on ubuntu


{%highlight  sh %}

#Add typesafe repository     
wget http://apt.typesafe.com/repo-deb-build-0002.deb `

#Install the repository
sudo dpkg -i repo-deb-build-0002.deb

#Refresh
sudo apt-get update

#Install sbt
sudo apt-get install sbt

{% endhighlight %}

Add specific version of sbt
#Some project need specific version of sbt like 0.13. If you get
error saying " Detected sbt version 0.12.2 Cannot find sbt launcher 0.12.2 Please download: From typesafe.artifactoryonline.com/typesafe/ivy-releases/… 
 To /home/<username>/.sbt/.lib/0.12.2/sbt-launch.jar" 

Run the following commands by replacing <username> with you username.

{%highlight  sh %}
 mkdir -p /home/<username>/.sbt/.lib/0.12.2/ 
 wget -O /home/<username>/.sbt/.lib/0.12.2/sbt-launch.jar 
 http://typesafe.artifactoryonline.com/typesafe/ivy-releases/
 org.scala-sbt/sbt-launch/0.12.2/sbt-launch.jar
{% endhighlight %}



