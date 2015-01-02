---           
layout: post
title: "Building Read it later service on MEAN stack - Part 1"
date : 23-12-2014
categories: javascript mean mean-series
---

Recently I gave a talk on how to build RESTful services on MEAN stack. In the talk, I went through a step by step tutorial to build a Read it later service on top of the MEAN stack. These series post will be excerpt from the talk.

This is first post in the series, where will be discussing about what is MEAN stack and what is it good for.

tl;tr If you are just interested in code and slides, you can grab it from [slideshare]((http://www.slideshare.net/madhukaraphatak/mean-41838061))and [github](https://github.com/phatak-dev/mean-readitlater).

## What is a stack?

Software stack is a set of tools working together to solve specific problem. Typically these tools are created in independent manner and also can be used separately. But when these tools are used together they give better results.One of the example is LAMP stack, which contains Linux, Apache web server, Mysql and PHP. As you can all these technologies are created independently and can be used separately. But if you put them together, you get a powerful set to create great websites.


## What is MEAN stack?

MEAN stands for Mongodb, Express, Angular and Node. It's a software stack to build web applications. All the technologies in the stack talk same language i.e is Java script.

## Why MEAN stack?

You may have heard about LAMP stack before. LAMP stack was build for building powerful websites. It was in 1990's. But time has changed now. We are building more and more web applications rather than building websites. So we need a better stack which can support the building web applications rapidly. MEAN stack helps you to build the application rapidly compare to LAMP stack.



## MEAN vs LAMP

 LAMP              MEAN

 Linux             V8
 Apache            Node.js
 Mysql             Mongodb
 Php               Express

 The above table shows the difference between LAMP and MEAN. I am cheating little bit here. V8, the javascript runtime of Chrome, is not a operating system. It's a virtual machine which is available on all the operating system. With trend of virtual machines like JVM,CLR the dependence on operating system is fading.

 We use mongodb in place of Mysql for storage. We use express in place of Php for MVC. Node.js will replace apache for web server.



## Why to move from LAMP to MEAN

 LAMP was created to build websites. It's not suitable for building web applications, where we will be building REST based API's. There is lot of over head to create the REST services on LAMP. 


 ## REST in LAMP vs REST in MEAN

 The following picture shows what it takes to build a REST service in LAMP





 As you can see the following are the over heads

 ### Adding a new field

 If you want to add a new field, you have to change in lot of places. First you have to change in client side json, then in the server side Java model, then in ORM mapping and finally in the database table. These kind of over head adds strain on rapid development.

 ### Mapping nested json to RDBMS

 Typically json has great support for nested structures. But expressing the nested structures in RDBMS is hard.

 ### Lost in translation 

 It's very easy to forget model in one of the places mentioned above and loose the data.

 Now let's see how a REST application looks in MEAN stack






 As you can see in the above picture, there is almost no translation. It's easy to add new field in one place and have it update in all places simultaneously.


 ## JSON is the secret sauce of MEAN stack

 JSON stands for JavaScript Object Notation. It's a data format like XML. It's the JSON which makes MEAN work like magic. All components in MEAN talk JSON natively. This means there is no time wasted translating from one language to another language.

 ## What MEAN is good for?

 Now you understand, building REST applications are much easier in MEAN stack. Now you may be wondering what else REST can do?. The following are few places you can use MEAN

 * CRUD web applications
 * REST API servers
 * Single page apps
 * Static content servers

 ## What MEAN is not good for?

 MEAN is not good for all the applications.

 * Computation intensive applications
 * Number crunching systems
 * Transactional systems.

In the next post, we will start building the a application on top of MEAN stack.






