---
layout: post
title: "Barrier Execution Mode in Spark 3.0 - Part 1 : Introduction"
date : 2020-11-11
categories: barrier-execution spark spark-three 
---
Barrier execution mode is a new execution mode added to spark in 3.0 version. This marks a significant change to platform which had only supported Map/Reduce based execution till now. This will allow spark to diversify the kind of workloads it can support on it's platform.

In this series posts we will discuss about this execution mode in detail.This is the first post in the series. In this post we will discuss about what is barrier execution mode and why it is needed. You can access all the posts in this series [here](/categories/barrier-execution).


## Execution Mode 

An execution mode in spark is way of executing the jobs in platform. The mode will dictate how jobs are divided into multiple parallel tasks and how they are scheduled. The execution mode defines what kind of processing can be handled in platform. 

Map/Reduce been a popular execution mode in majority of big data frameworks including spark. This execution mode is flexible enough to handle wide variety workloads like ETL, SQL and ML etc.

## Map/Reduce Execution Mode

In this section of the post we will look into the Map/Reduce from a execution point of view. Understanding this will help us how its different from the barrier execution mode.

In Map/ Reduce
 
  *  A job is collection of stages. Each stage can be Map or Reduce. Between these stages there will usually be shuffling.

  * Each stage is collection of tasks. These tasks are independent of each other. This approach is called **shared nothing**. This allows system to scale as more resources are available.

  * As the tasks are independent of each other, when one of the tasks is failed only that task is retried.  

  * Number of tasks in Map task is determined by amount of data and number of tasks in reduce phase is determined by developer


The above points summarises the Map/Reduce approach in very high level. Even though there are  many implementation details, this information is enough for our discussion.

## Need for New Execution Mode

Map/Reduce execution mode has served well for many years for different workloads. Why we need different execution mode now?

One of the reasons is to support deep learning frameworks on spark. Deep learning frameworks don't lend themselves to Map/Reduce model. They work well with other kind of execution model called MPI ( Message Passing Interface). For example, Horovod, an open source framework to do deep learning on scale by Uber, uses the MPI to implement the distributed deep learning for variety of DL frameworks. You can learn more [here](https://github.com/horovod/horovod#documentation).

In order to support the deep learning natively, spark need to support an execution model that is different than Map/Reduce. The new execution Model is modeled after MPI

## Barrier Execution Mode

The below are how the barrier execution model, which is inspired from MPI, different from the Map/Reduce execution model.

  *  A job is collection of stages. Between these stages there will usually shuffling. This remains same as Map/Reduce model

  * Each stage is collection of tasks. These tasks are all started together and they are dependent. This is one of major departure compared to Map/Reduce. In MPI model the tasks can communicate to each other and dependent. So they need to be started together. 

  * As the tasks are dependent of each other, when one of the tasks are failed all tasks are retried. Again this is different from Map/Reduce model.

  * Number of tasks is always decided by developer. This is because even though data may be small the computation may be much more complicated and may need more resource than typical processing. Also there should be enough resources to run all tasks together. 


From above points you can see how barrier execution mode is much different than standard Map/Reduce mode.


## References

[https://www.mcs.anl.gov/~itf/dbpp/text/node95.html](https://www.mcs.anl.gov/~itf/dbpp/text/node95.html)

[https://eng.uber.com/horovod/](https://eng.uber.com/horovod/)

[https://issues.apache.org/jira/browse/SPARK-24374](https://issues.apache.org/jira/browse/SPARK-24374)


## Conclusion

Barrier execution mode brings a new execution mode to spark in last 10 years. This bring new kind of capabilities to the platform.
