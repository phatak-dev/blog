---           
layout: post
title: "Building Read it later service on MEAN stack - Part 2"
date : 23-12-2014
categories: javascript mean mean-series
---
This is second post in the [mean-series](/categories/mean-series/). In this post, we are going to disucss briefly about each of components in MEAN stack. Then we are going to go through the installation of each of these components.

MEAN is made of Mongodb, Express, Angualar and Node.js

## Node.js

[Node.js](http://nodejs.org/) is a javascript runtime written on V8, javascript virtual machine for Google chrome. Node.js is written i C/C++ not in javascript itself. On node.js you can build applications which are written in Javascript. Node.js itself is not something that useful. As Node.js allows third party developers to write modules, there are thousands of modules out there. These modules make node.js invaluable.

Follow this [wiki](https://github.com/joyent/node/wiki/installing-node.js-via-package-manager) to install node.js on your system.

## Mongodb

Mongodb is a document oriented NoSql database. It stores the data in JSON like data format. As we discussed in earlier post, each components in MEAN stack talk JSON. The following are few properties of Mongodb

* Supports powerful query language
* Supports row level atomicity
* Autosharded 

Follow this [wiki](http://docs.mongodb.org/manual/installation/) to install mongodb on your machine. 

## Angular.js

Angular is a javascript library written by Google. It's used in developing rich single page apps. It provides powerful abstractions like controllers, dependency injection, two way data binding. As it's a client side library, there is not installation is needed.

## Express

Express is a Node.js module used for web development on Node.js.
You can install express using npm, node package manager. We will see how exactly do that in next section.


## Read it later application

Read it later is a service which allow you to save the links from the net, to read it later. You can add the links from browser, phone etc. Client use REST api to talk to server. It's similiar to pocket, instapaper etc.

## Read it later architecture

The below image shows the architecture of the our application.




## Preparing our machine for the app

* Clone the code from [github](http://docs.mongodb.org/manual/installation/)

* Cd into  mean-readitlater/mongorest folder

* Run *npm install* to install dependencies


The code is divided into multiple branches in order to see the evolution of the application. In next post, we will be start discussing about each of these pieces.  



