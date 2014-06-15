---           
layout: post
title: "Web forms: Do it right, A DZone Story"
date: 2011-06-17 08:38:24 UTC
updated: 2011-06-17 08:38:24 UTC
comments: false
categories: design javascript
---
Web is full of forms. Every day when you browse the web, you come across a lot of forms. May be to register to a new site or to write a comment on a blog which you liked. Over the years, building forms has turned an art, where designers put a lot of effort to make forms less boring and more intuitive and interactive to the users. But most of the time you will come across some of designs which drives you crazy .I came across one of those recently and this post is about it.
              
DZone is well known place to get great articles regarding technology. I am a regular user of DZone from a long time and I have never shared any links using it. But after revamping this blog, I thought let me share it on DZone. When I tried to add a new link, it took me to registration page, which is a standard form to signup. Everything looked normal until I hit with strange problem. 

Every time, I tried to signup, it failed with an error saying “First address is not correct “. I just started wondering how the system is evaluating the addresses. Does it have some magical map based address validation tool? The field was not blank. So there is no question of null value. I changed the country and all the other options but it kept on failing with same error. It started to drive me crazy. The error message was not giving any information about what might have gone wrong. After so many tries I thought of giving up. But suddenly everything worked and I was a registered user of Dzone. I thought of doing research on this mystery form. After a little poking in source I came to know that the field was expecting two characters “#” and any number, which is used to tell home number or street number. But how should I know that? May be, the developers who build the page thought that every address will have a number which may not be the case always. There are some addresses in my country that doesn’t have any numbers both for home and streets.

So after going through the crazy form, I thought its time to rephrase the rules to build good forms. So the following simple rules to make sure your form don’t suck.

* Don’t make any assumptions. Because your site going to be accessed from different parts of world.
* Keep the error message information as informative as possible. Even you can suggest example information to say how the field should be filled.
* Use Ajax whenever possible rather than waiting till user to click submit 

So when you build your next signup pages, make sure you get it right. Users are becoming smart; they may not have the patience or would have very little interest in wasting time on your forms. So work smart, while building smart forms!

