---           
layout: post
title: "History of Apache Spark : Journey from Academia to Industry" 
date : 2015-01-02
categories: spark
custom_css : long_heading
---

Apache Spark is one of the most interesting frameworks in big data in recent years. Spark had it's humble beginning as a research project at UC Berkeley. Recently O'Reilly Ben Lorica [interviewed](https://soundcloud.com/oreilly-radar/apache-sparks-journey-from-academia-to-industry) Ion Stoica, UC Berkeley professor and databricks CEO, about the rise of apache spark and Apache mesos. 

This post captures some of the interesting questions from the interview.


### Q : How Apache Spark started?

The story started back in 2009 with mesos. It was a class project in UC Berkley. Idea was to build a cluster management framework, which can support different kind of cluster computing systems.Once mesos was built, then we thought what we can build on top of mesos. That's how spark was born. 

We wanted to show how easy it was to build a framework from scratch in mesos. We also wanted to be different from existing cluster computing systems. At that time, Hadoop targeted batch processing, so we targeted interactive iterative computations like machine learning.

The requirement for machine learning came from the head of department at that time. They were trying to scale machine learning on top of Hadoop. Hadoop was slow for ML as it needed to exchange data between iterations through HDFS. The requirement for interactive querying came from the company, Conviva, which I founded that time. As part of video analytical tool built by company, we supported adhoc querying. We were using Mysql at that time, but we knew it's not good enough. Also there are many companies came out of UC,like Yahoo,Facebook were facing similar challenges. So we knew there was need for framework focused on interactive and iterative processing.

### Q : One of the strength of Spark is, it's nice integration with Hadoop ecosystem. Is it was a conscious choice right from the beginning?

Yes. It was a conscious decision. We never planned for storage layer for Spark,at least at that point of time. So our targeted user base was the one who already had their data in HDFS. 

### Q : Another thing that made Spark popular, is how it's ties together different kind of workloads in data science pipeline. Having common execution engine for machine learning, real time etc was design decision from the beginning?

That's an excellent question. May be it was in between. It was pretty obvious at that time we wanted more unification. At conviva, we had two stack. One for real time and one for historical data. Real time system was home grown one and we used hadoop for historical data. It's hard to integrate between two different stacks. Also, two different stacks comes with higher maintenance costs. 

One of the challenges was having same metrics in both stacks. For example, one of the metric was how many people watched video at given point of time. This metrics is useful both in real time and historical time. But the problem was,each stack had its own data, algorithm and code base to calculate these metrics. So maintaining these metrics consistently was extremely hard.

Even hadoop batch jobs were like real time systems with a delay of 20-30 mins. So Spark, with aggressive in memory usage, we were able to run same batch processing systems in under a min. Then we started to think, if we can run one job so fast, it will be nice to have multiple jobs running in a sequence to solve particular pipeline under very small time interval. That's how having a common execution engine for different computation was born.

### Q : Normally research projects get abandoned after paper is published. But Berkley has a track record with projects like Postgres, BSD and now with Spark to make industry to adopt these projects. So what is your role in this?

There are many components. And if you look back, you can always revise history. Especially if you had success. First of all, we had a fantastic group of students. Matei, the creator of Spark and others who did Mesos. And then another great group of different students who contributed and built different modules on top of Spark, and made what Spark it is today, which is really a platform. So, that’s one: the students

The other one was a great collaboration with the industry. We are seeing first hand what the problems are, challenges, so you’re pretty anchored in reality.

The third thing is, we are early. In some sense, we started very early to look at big data, we started as early 2006, 2007 starting to look at big data problems. We had a little bit of a first-mover advantage, at least in the academic space. So, all this together, plus the fact that the first releases of these tools, in particular Spark, was like 2000 lines of code,very small,so tractable.


### Q : One of the interesting part of community activities was the hosting meetups for discussing Spark. This is unlike any academic projects, as there are not many incentives for students or university.

It was all about aligning different incentives. At one hand, students get to meet people who use their software which is great but other hand, these students are here to get a Phd. It is this belief that, building systems and making people using it, allow you to understand new problems first hand. You can solve them. You will be among the first one to do research on them. So it results in greater research. So this complimentary nature of these activities keep students engaged for years to come.


### Q: In Last year, there are many things happened. Spark became Apache project. You guys started Data bricks. Spark summits attracting more and more people. It seems like spark becoming main stream. So what's the thinking behind becoming Apache project and starting a company?

Once again, excellent question. Right from beginning community was interested in contributing to spark. This interest grew and grew over the years. So being an apache project, made spark more consumable for enterprise customers. Also having a entity behind the project, gives more confidence to enterprise to adopt technologies. 

As of now, main focus of the company is to increase adoption of spark. We want to put spark in hands of as many people as possible. Also we want people to have great experience on their platform. So instead of having our own distribution of spark, we have partnered  with other hadoop distributors like Cloudera, Hortonworks and big data system distributors like datastax to help them to distribute spark in order to satisfy their customers. 

### Q : One of the unique value of Spark is of having API's in python, Java other than native Scala. What's the thinking behind this?

That's excellent observation. In last year there are many interesting new applications are build on top of spark. The reason behind that, from the beginning we have focused a lot to make building new application very easy. Spark has a very rich API with more than 80 operators. Also we added more languages binding over time. Also with excellent libraries, it makes spark a great platform for developers to build their applications.

### Q : Now if you look back to 2009, there was no way you predicted it's going to so big?

Absolutely not. We wanted to have some good, interesting research projects; we wanted to make it as real as possible, but in no way could we have anticipated the adoption and the enthusiasm of people and of the community around what we have built.







