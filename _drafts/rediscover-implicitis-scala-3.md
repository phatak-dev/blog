---
layout: post
title: "Rediscovering Implicits in Scala 3 - Part 1: Implicit Parameters"
date : 2024-11-07
categories: scala scala3 rediscover-implicits-scal3
---
Implicits are one of the most advanced feature of Scala. This feature makes many of the meta programming possible in language. 

The same power of implicits also brings lot of complexity and confusion to beginners. This complexity often leads to the [wrong use of the feature or scares away developers using it altogether](https://docs.scala-lang.org/scala3/reference/contextual/index.html#Critique%20of%20the%20Status%20Quo). 

In Scala 3, the major new version of Scala Language, there are many features that are added to simplify the implicits. These simplifications helps the user of the language to clearly understand what are different mechanism that are available to abstract over different contexts in the code. This simplifications make it more easy for beginners to grasp the power of implicits and also makes it more cleaner code for experts.

In this series of blogs, I will be exploring all these new features as if I am rediscovering the implicits all together again. In this first post, I will be discussing about implicit parameters. You can find all the post in this series [here](/categories/rediscover-implicits-scal3).

##Implicit Parameters
