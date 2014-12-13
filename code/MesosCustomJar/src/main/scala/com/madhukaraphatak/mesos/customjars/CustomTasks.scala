package com.madhukaraphatak.mesos.customjars

import org.apache.mesos.MesosSchedulerDriver
import org.apache.mesos.Protos.FrameworkInfo


/**
  * Created by madhu on 30/9/14.
  */


object CustomTasks {

  // a task which dynamically loads the class file
  def mysqlTask() = {
    new FunctionTask[Unit](
      () => {
        try {
          Thread.currentThread.getContextClassLoader.loadClass("com.mysql.jdbc.Driver")
          println("successfully loaded")
        }
        catch {
          case e: Exception => {
            e.printStackTrace()
            throw e
          }
        }
      }
    )
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
     val mysqlJarPath = args(2)
     System.setProperty("executor_script_path",executorScriptPath)
     val scheduler = new TaskScheduler(mesosURL,List(mysqlJarPath))
     scheduler.submitTasks(List(mysqlTask()): _*)
     val driver = new MesosSchedulerDriver(scheduler,framework,mesosURL)
     driver.run()

   }


 }
