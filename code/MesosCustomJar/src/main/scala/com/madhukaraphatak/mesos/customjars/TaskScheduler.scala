package com.madhukaraphatak.mesos.customjars

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.{Collections, UUID}

import com.google.protobuf.ByteString
import com.madhukaraphatak.mesos.jarhandling.HttpServer
import org.apache.mesos.Protos._
import org.apache.mesos.{Scheduler, SchedulerDriver}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * Created by madhu on 30/9/14.
 */
class TaskScheduler(mesosURL: String,jars: Seq[String] = Nil) extends Scheduler {

  var _tasks = mutable.Queue[Task[_]]()
  var jarServer: HttpServer = null
  var jarUris: String = ""

  override def error(driver: SchedulerDriver, message: String) {}

  override def executorLost(driver: SchedulerDriver, executorId: ExecutorID, slaveId: SlaveID, status: Int) {}

  override def slaveLost(driver: SchedulerDriver, slaveId: SlaveID) {}

  override def disconnected(driver: SchedulerDriver) {}

  override def frameworkMessage(driver: SchedulerDriver, executorId: ExecutorID, slaveId: SlaveID, data: Array[Byte]) {}

  override def statusUpdate(driver: SchedulerDriver, status: TaskStatus) {
    println(s"received status update $status")
  }

  override def offerRescinded(driver: SchedulerDriver, offerId: OfferID) {}

  /**
   *
   * This callback is called when resources are available to  run tasks
   *
   */
  override def resourceOffers(driver: SchedulerDriver, offers: java.util.List[Offer]) {

    //for every available offer run tasks
    for (offer <- offers.asScala) {
      _tasks.dequeueFirst(value => true) map (task => {


        //our task will use one cpu
        val cpus = Resource.newBuilder.
          setType(org.apache.mesos.Protos.Value.Type.SCALAR)
          .setName("cpus")
          .setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder.setValue(1.0))
          .setRole("*")
          .build

        //generate random task id
        val taskId = "task" + System.currentTimeMillis()
        val taskInfo = TaskInfo.newBuilder()
          .setSlaveId(SlaveID.newBuilder().setValue(offer.getSlaveId.getValue).build())
          .setTaskId(TaskID.newBuilder().setValue(taskId.toString))
          .setExecutor(getExecutorInfo(driver))
          .setName(UUID.randomUUID().toString)
          .addResources(cpus)
          .setData(ByteString.copyFrom(Utils.serialize(task)))
          .build()
         driver.launchTasks(Collections.singleton(offer.getId), Collections.singleton(taskInfo))

      })
    }
  }

  def submitTasks[T](tasks: Task[T]*) = {
    this.synchronized {
      this._tasks.enqueue(tasks: _*)
    }
  }

  def getExecutorInfo(d: SchedulerDriver): ExecutorInfo = {
    val scriptPath = System.getProperty("executor_script_path","~/run-executor.sh")
    ExecutorInfo.newBuilder().
      setCommand(CommandInfo.newBuilder().setValue("" +
      "/bin/sh "+scriptPath+ s" $jarUris"))
      .setExecutorId(ExecutorID.newBuilder().setValue("1234"))
      .build()
  }

  override def reregistered(driver: SchedulerDriver, masterInfo: MasterInfo) {}

  override def registered(driver: SchedulerDriver, frameworkId: FrameworkID, masterInfo: MasterInfo): Unit = {
    //create the jar server if there are any jars have to be distributed
    if(jars.size > 0 )  createJarServer()
  }


  def createJarServer() = {
    val dirFile = Utils.createTempDir()
    println("jar directory is" + dirFile.getAbsolutePath)
    val fileNames = new ArrayBuffer[String]()
    for ((path, index) <- jars.zipWithIndex) {
      val file = new File(path)
      val fileName = index + "_" + file.getName
      copyFile(file, new File(dirFile, fileName))
      fileNames += fileName
    }

    jarServer = new HttpServer(dirFile)
    jarServer.start()
    val uri = jarServer.uri
    println("jar server started at " + uri)
    jarUris = fileNames.map(f => uri + "/" + f).mkString(",")
  }


  private def copyFile(src: File, dest: File) = {
    val srcFile = new FileInputStream(src)
    val destFile = new FileOutputStream(dest)
    Utils.copyStream(srcFile, destFile)
  }


}
