package com.madhu.mesos.customexecutor

import java.util.concurrent.{ExecutorService, Executors}

import org.apache.log4j.Logger
import org.apache.mesos.Protos._
import org.apache.mesos.{Executor, ExecutorDriver, MesosExecutorDriver}


/**
 * Created by madhu on 1/10/14.
 */
object TaskExecutor {

  def main(args: Array[String]) {
    val logger = Logger.getLogger(this.getClass.getName)
    System.loadLibrary("mesos")
    var classLoader: ClassLoader = null
    var threadPool: ExecutorService = null

    val exec = new Executor {
      override def launchTask(driver: ExecutorDriver, task: TaskInfo): Unit = {
        val arg = task.getData.toByteArray
        threadPool.execute(new Runnable() {
          override def run(): Unit = {
            val runningTask = Utils.deserialize[Task[Any]](arg, classLoader)
            Thread.currentThread.setContextClassLoader(classLoader)
            try {
              runningTask.run
              driver.sendStatusUpdate(TaskStatus.newBuilder()
                .setTaskId(task.getTaskId)
                .setState(TaskState.TASK_FINISHED).build())
            } catch {
              case e: Exception => {
                logger.error("the execption is",e)
                logger.error("error in task id" + task.getTaskId.getValue)
                System.exit(1)
              }
            }
          }

        })
      }

      override def registered(driver: ExecutorDriver, executorInfo: ExecutorInfo, frameworkInfo: FrameworkInfo, slaveInfo: SlaveInfo): Unit = {
        classLoader = this.getClass.getClassLoader
        Thread.currentThread.setContextClassLoader(classLoader)
        threadPool = Executors.newCachedThreadPool()
      }

      override def frameworkMessage(driver: ExecutorDriver, data: Array[Byte]): Unit = {}

      override def error(driver: ExecutorDriver, message: String): Unit = {}

      override def reregistered(driver: ExecutorDriver, slaveInfo: SlaveInfo): Unit = {}

      override def killTask(driver: ExecutorDriver, taskId: TaskID): Unit = {}

      override def disconnected(driver: ExecutorDriver): Unit = {}

      override def shutdown(driver: ExecutorDriver): Unit = {}
    }

    new MesosExecutorDriver(exec).run()
  }


}
