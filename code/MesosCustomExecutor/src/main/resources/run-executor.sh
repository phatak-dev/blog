#!/bin/sh
echo "running Executor"
export LD_LIBRARY_PATH="/home/madhu/Dev/spark/mesos-0.20.0/build/src/.libs":$LD_LIBRARY_PATH
# -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044
echo $PWD
#update the path to point to jar
java -cp /home/madhu/Dev/mybuild/blog/code/MesosCustomExecutor/target/mesoscustomexecutor-1.0-SNAPSHOT.jar com.madhu.mesos.customexecutor.TaskExecutor
