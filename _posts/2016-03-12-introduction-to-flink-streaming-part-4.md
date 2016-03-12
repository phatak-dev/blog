---
layout: post
title: "Introduction to Flink Streaming - Part 4 : Understanding Flink's Advanced Stream Processing using Google Cloud Dataflow"
date : 2016-03-12
categories: flink flink-streaming
---
Flink streaming is a new generation of streaming framework which bring a lot of new concepts to the table. It's is not just a low latency batch processing like spark streaming or it's not primitive event processor like storm. It has best of both the worlds and much more. 

As with any new generation framework, it's hard for people coming from the other frameworks to appreciate full power of the new system. We normally end up trying to replicate same old application on new framework. This happened when we moved from Map/Reduce to Spark. So understanding the strengths of these new frameworks is critical to build new generation streaming applications rather than replicating the already existing one. 

When I was researching on flink streaming , I came across this excellent video which demonstrated power of these new generation streaming frameworks. This video is from flink forward conference which talks about google cloud dataflow. Google cloud dataflow is a unified batch and streaming API for big data from Google. Lot of ideas for flink streaming API are inspired from google dataflow. So understanding motivations and programming model of google dataflow may help to understand the power of flink streaming. Also recently google made it open source under the name [apache beam](https://wiki.apache.org/incubator/BeamProposal).

As the fourth blog in the series, I am sharing the google dataflow talk from flink forward below. You can access the slides on [slideshare](http://www.slideshare.net/FlinkForward/william-vambenepe-google-cloud-dataflow-and-flink-stream-processing-by-default).

You can access other blog in the series [here](/categories/flink-streaming/).

<div class="video-container"> <iframe src="https://www.youtube.com/embed/y7f6wksGM6c" frameborder="0" width="560" height="315"></iframe> </div>

