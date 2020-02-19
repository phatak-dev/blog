---
layout: post
title: "ClickHouse Clustering for Spark Developer"
date : 2019-12-16
categories: spark  scala clickhouse
---
Clickhouse is a leading big data distributed database. It provides variety of options for distribution. The overall clustering will be tricky to understand for a new developer. For a spark developer, if we can understand it by comparing it to Hadoop and Spark Cluster it will make it easy to understand.

## Blocks in Hadoop and Shards in ClickHouse for Storage

Whenever a file is copied to HDFS, it will break it down to multiple blocks. This block creation is done by file size. 

In clickhouse, whenever a row is added to table it is added to a shard. This sharding is done using shard key. 

Both of block and shard creation allows these system to keep data in multiple systems.


## Replication for Storage

HDFS makes multiple replica for the block for safer storage.

Clickhouse makes multiple replicas of shard for safer storage.


## Blocks and Shard for Processing

In Map/Reduce or Spark, number of blocks dictate the amount of parallelism.

In clickhouse query, number of shards will dictate the parallelism across the machines.

## Replication for Processing

By default, Spark uses replication for failure handling. But in spark, by increasing the partitions it can also use replicated data for better performance.

By default clickhouse uses replication for failure handling . But by making below changes we can use replication for more parallelism. 

 * Need to set sample key when creating table

 * set these properties from clickhouse client 
 
 **max_parallel_replicas = 10, prefer_localhost_replica = 0**


## Conclusion

Clickhouse distributed mode introduce many concepts which may be confusing to beginner. But comparing it to hadoop and spark, it will make it understanding much easier.


