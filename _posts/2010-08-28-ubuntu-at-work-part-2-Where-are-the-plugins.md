---           
layout: post
title: "Ubuntu at work - Part 2 Where are the plugins"
date: 2010-08-28 19:31:58 UTC
updated: 2010-08-28 19:31:58 UTC
comments: false
categories: ubuntu 
---

Plug-ins are the most exciting things in Eclipse. They are the one which make Eclipse so exciting as a development tool. Virtually there is an Eclipse plug-in for every java tool out there. Our product also uses a lot of plug-ins. But today I discovered a strange thing in Eclipse. Some of the plug-ins simply disappeared from the IDE window. OMG!!! it took hours to download them and now they are missing and another strange thing is that every time eclipse starts it loads different set of plug ins. This post discuss how to solve this mystery.

Eclipse to Ubuntu : Hey I want to load 2000 plug ins
Ubuntu : No dude ! You can load only 1024 of them.

###No.of open files in Ubuntu

If you execute

> $ulimit -a 

you can see the system parameters limiting the resources. In those parameters no.of open files for a given process is defined. In ubuntu its 1024 files by default. But as no.of plug-ins grow in the eclipse, this limit is inadequate. So we have to set this parameter to a higher number. But sadly there is no direct way to do this. So we have to follow the following steps to change the parameters

###Step 1: 
Add “session required pam_limit.so” to /etc/pam.d/common-session

>$sudo vi /etc/pam.d/common-session

###Step 2 : 

Un-comment same line in /etc/pam.d/su

> $sudo vi /etc/pam.d/su

###Step 3 : 

Add “ * hard nofile 10000” to the /etc/security/limits.conf. Here 10,000 is the limit which we want to set for per process basis. You can choose your own limit.

> $sudo vi /etc/security/limits.conf

###Step 4 : 

Reboot your system

###Step 5 : 
Login and in terminal type

> $ulimit -n 10000

Now this will set to no.of open files to 10,000.

Hurray!!! Plug ins are back again. Time to work again ...see you
