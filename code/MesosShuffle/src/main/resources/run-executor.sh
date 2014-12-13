#!/bin/sh
echo "running Executor"
export LD_LIBRARY_PATH="/home/madhu/Dev/spark/mesos-0.20.0/build/src/.libs":$LD_LIBRARY_PATH
#update the path to point to jar
java -cp /home/madhu/Dev/mybuild/blog/code/MesosShuffle/target/mesosshuffle-1.0-SNAPSHOT.jar com.madhukaraphatak.mesos.shuffle.TaskExecutor $1
