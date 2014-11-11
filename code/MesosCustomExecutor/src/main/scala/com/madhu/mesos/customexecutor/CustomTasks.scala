package com.madhu.mesos.customexecutor

import org.apache.mesos.MesosSchedulerDriver
import org.apache.mesos.Protos.FrameworkInfo


/**
  * Created by madhu on 30/9/14.
  */


object CustomTasks {
   def multipleTasks() = {
     0 to 1 map {
       index => {
         val f = new FunctionTask[Unit](() => println("hi" + index))
         f
       }
     }
   }


   def main(args: Array[String]) {
     val framework = FrameworkInfo.newBuilder.
       setName("CustomExecutor").
       setUser("").
       setRole("*").
       setCheckpoint(false).
       setFailoverTimeout(0.0d).
       build()

     val mesosURL = args(0)
     val executorScriptPath = args(1)
     System.setProperty("executor_script_path",executorScriptPath)
     val scheduler = new TaskScheduler(mesosURL)
     scheduler.submitTasks(multipleTasks(): _*)
     val driver = new MesosSchedulerDriver(scheduler,framework,mesosURL)
     driver.run()

   }


 }
