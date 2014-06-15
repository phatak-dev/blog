---           
layout: post
title: "Ubuntu at work - Part1"
date: 2010-08-21 05:55:13 UTC
updated: 2010-08-21 05:55:13 UTC
comments: false
categories: ubuntu
---

I am a happy user of Ubuntu from 2 years. It worked smoothly over 5 upgrades.These years I primarily used ubuntu for the academic and personal uses. But now I am out of college and working for a start up company “Zinnia Systems”. Since I am so used to ubuntu , i din’t wanted to switch to windows at work place . Luckily I am allowed to use ubuntu at work place too .So the in next series posts I will share experience of ubuntu at work

###Background

Our company is a telecommunication product company . We use java stack for developing our products. So java employs “Write once, Run anywhere” policy should work on any platform, i am trying to develop the product in ubuntu when all working on the windows systems.

###System
My system is dell Vostro with 2GB RAM and 320 GB hard disk . It runs ubuntu karmic kola (9.10) . Though 10.04 is the latest i dint had much time to test it, so i am using 9.10 itself.

###Day 1 : Setting up the environment

Any company you work ,first requirement will be learning the working environment . So in this post i talk about how i managed to install the tools for getting started

Installing IBM RTC Express-C  eclipse client
Since we use Java for development we use Eclipse for coding which is a favorite of most of java developers.We also use Rational Team Concert for source control , I have to use IBM RTC for development .
So i downloaded the IBM RTC from here ( It’s trickier to find )
https://jazz.net/downloads/rational-team-concert/releases/2.0?p=allDownloads (requires registration)
Just download the Linux version.

Button.......Don't click me!!!!!! 

“ With choice comes the responsibility”
Though i download the correct version , the buttons in eclipse dint worked.After a lot of googling, I came to know that It’s a  bug in 9.10. So i used to the following shell script to launch the eclipse 

#!/bin/sh
export GDK_NATIVE_WINDOWS=1
$path to ur downloaded eclipse

This script make sure that correct GDK   available to the Eclipse. After configuring this Eclipse is happily responding to the button clicks. 

Installing and configuring Maven plug in to the Eclipse
This is one of the most time consuming thing in whole setup . Though maven is small plug in it has too many gotchas with the IBM RTC.

###Update sites don't make life easier 
The m2eclipse repository failed to fetch the plug in for eclipse. After many hours of googling we found that the update site http://m2eclipse.sonatype.org/sites/m2e incompatible with the eclipse 3.4
So if you want maven plug in for Eclipse 3.4 use this repository
http://m2eclipse.sonatype.org/sites/m2e/0.10.0.20100209-0800/
It may save you a day.

###Dependencies hell

Though I am able to find the correct repository , it always refused to install by saying that it needs a lot of things . I tried to install all those dependencies but it wanted more . Finally i discovered that there is an easy way to do it. Just click on manage sites and check all the sites.Then it automatically downloads the all the required dependencies. Yes it is a long download ..U can take a nap and come back.

So at the end of day , I was able to get the eclipse running and maven was happy to build projects for me.
 
Its enough for a day.Will be back with another story .Till then happy ubunting :)
