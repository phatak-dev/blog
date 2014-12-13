package com.madhukaraphatak.mesos.shuffle

import java.io.{File, FileOutputStream}
import java.net.{URL, URLClassLoader}
import java.util.concurrent.{ExecutorService, Executors}

import com.google.protobuf.ByteString
import org.apache.log4j.Logger
import org.apache.mesos.Protos._
import org.apache.mesos.{Executor, ExecutorDriver, MesosExecutorDriver}

import scala.collection.mutable.ArrayBuffer


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
              val value = runningTask.run
              val result = new TaskResult(value)
              driver.sendStatusUpdate(TaskStatus.newBuilder()
                .setTaskId(task.getTaskId)
                .setState(TaskState.TASK_FINISHED)
                .setData(ByteString.copyFrom(Utils.serialize(result))).build())
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
        classLoader = getClassLoader()
        Thread.currentThread.setContextClassLoader(classLoader)
        threadPool = Executors.newCachedThreadPool()
      }


      def getClassLoader(): ClassLoader = {
        var loader = this.getClass.getClassLoader
        val localfiles = new ArrayBuffer[String]()
        if (args.length > 0) {
          for (uri <- args(0).split(",").filter(_.size > 0)) {
            val url = new URL(uri)
            val fileName = url.getPath.split("/").last
            downloadFile(url, fileName)
            localfiles += fileName
          }

          if (localfiles.size > 0) {
            val urls = localfiles.map(file => new File(file).toURI.toURL).toArray
            loader = new URLClassLoader(urls, loader)
          }
        }
        loader
      }

      private def downloadFile(url: URL, localPath: String) {
        val stream = url.openStream()
        val out = new FileOutputStream(new File(localPath))
        Utils.copyStream(stream, out, true)
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
