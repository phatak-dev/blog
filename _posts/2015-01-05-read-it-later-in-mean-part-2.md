---           
layout: post
title: "Building Read it later service on MEAN stack - Part 2"
date : 2015-01-05
categories: javascript mean-series
---
This is second post in the [mean-series](/categories/mean-series/). In this post, we are going to discuss briefly about each of the components in MEAN stack. We are also going to go through the installation of each of these components.

## Components of MEAN stack

MEAN is made of Mongodb, Express, Angualar and Node.js

### Node.js

[Node.js](http://nodejs.org/) is a JavaScript runtime written on V8, javascript virtual machine for Google chrome. Node.js is written in C/C++ not in JavaScript itself. Using node.js, you can build server side applications in JavaScript. 

Node.js follows module oriented architecture. It allows third party developers to extend it's capabilities using modules. These modules can be written using JavaScript or C/C++. There are thousands of these modules available for all kinds of tasks. These modules make node very attractive for server side development. 

Follow this [wiki](https://github.com/joyent/node/wiki/installing-node.js-via-package-manager) to install node.js on your system.

### Mongodb

[Mongodb](https://www.mongodb.org/) is a document oriented NoSql database. It stores the data in JSON like data format. As we discussed in earlier post, each components in MEAN stack talk JSON. With mongodb, you can store and query the json without any transformation.

The following are few properties of Mongodb

* Supports powerful query language
* Supports row level atomicity
* Autosharded 

Follow this [wiki](http://docs.mongodb.org/manual/installation/) to install mongodb on your machine. 

### Angular.js

[Angular](https://angularjs.org/) is a front end JavaScript library written by Google. It's used in developing rich single page applications. It provides powerful abstractions like controllers, dependency injection, two way data binding etc. As it's a client side library, there is not installation is needed. 

### Express

[Express](http://expressjs.com/) is a Node.js module used for web development. It supports MVC style of web application development much like J2EE,php etc.

You can install express using npm, node package manager. We will those steps in next section.


## Read it later application

Read it later is a service which allow you to save the links from the net, to read it later. You can add the links from browser, phone etc. Clients use REST api to talk to server. It's similar to services like [pocket](http://getpocket.com), [instapaper](https://www.instapaper.com/) etc.

### Read it later architecture

The below image shows the architecture of the our application.

![Read it later architecture](/images/readitlater_architecture.png)


As you can see in the image, there are two clients

* Chrome extension : To add the link to service
* Angular UI : To view saved links

Also both clients talk to server using REST API. We will be using mongodb to save the urls.

### Getting the code

Now you have all the needed tools installed on your machine. From next post, we will start discussing about how to build read it later on this stack. Before that follow the below steps to download the code and build it.

* Clone the code from [github](https://github.com/phatak-dev/mean-readitlater)

* Cd into  mean-readitlater/mongorest folder

* Run *npm install* to install dependencies


The code is divided into multiple branches in order to understand the evolution of the application. In next post, we will start discussing about code step by step.





