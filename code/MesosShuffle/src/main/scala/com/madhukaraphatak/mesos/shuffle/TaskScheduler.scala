package com.madhukaraphatak.mesos.shuffle

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.{Collections, UUID}

import com.google.protobuf.ByteString
import org.apache.mesos.Protos._
import org.apache.mesos.{MesosSchedulerDriver, Scheduler, SchedulerDriver}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/**
 * Created by madhu on 30/9/14.
 */
class TaskScheduler(mesosURL: String, jars: Seq[String] = Nil) extends Scheduler {

  trait StatusCallaback {
    def statusUpdate(driver: SchedulerDriver, status: TaskStatus)
  }

  var _tasks = mutable.Queue[(Task[_], Int)]()
  var taskIdToIndex = mutable.Map[Long, Int]()
  val random = new Random(10)
  var jarServer: HttpServer = null
  var jarUris: String = ""
  var driver: SchedulerDriver = null
  var statusCallaback: StatusCallaback = null


  override def error(driver: SchedulerDriver, message: String) {}

  override def executorLost(driver: SchedulerDriver, executorId: ExecutorID, slaveId: SlaveID, status: Int) {}

  override def slaveLost(driver: SchedulerDriver, slaveId: SlaveID) {}

  override def disconnected(driver: SchedulerDriver) {}

  override def frameworkMessage(driver: SchedulerDriver, executorId: ExecutorID, slaveId: SlaveID, data: Array[Byte]) {}

  override def statusUpdate(driver: SchedulerDriver, status: TaskStatus) {
    if (statusCallaback != null) statusCallaback.statusUpdate(driver, status)
  }


  def start() = {
    val framework = FrameworkInfo.newBuilder.
      setName("CustomExecutor").
      setUser("").
      setRole("*").
      setCheckpoint(false).
      setFailoverTimeout(0.0d).
      build()
    driver = new MesosSchedulerDriver(this, framework, mesosURL)
    driver.start()
  }

  override def offerRescinded(driver: SchedulerDriver, offerId: OfferID) {}

  override def resourceOffers(driver: SchedulerDriver, offers: java.util.List[Offer]) {
    for (offer <- offers.asScala) {
      val taskOption = _tasks.dequeueFirst(_ => true)
      taskOption match {
        case Some((task, index)) => {
          val cpus = Resource.newBuilder.
            setType(org.apache.mesos.Protos.Value.Type.SCALAR)
            .setName("cpus")
            .setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder.setValue(1.0))
            .setRole("*")
            .build

          val taskId = random.nextLong()
          taskIdToIndex += taskId -> index
          val taskInfo = TaskInfo.newBuilder()
            .setSlaveId(SlaveID.newBuilder().setValue(offer.getSlaveId.getValue).build())
            .setTaskId(TaskID.newBuilder().setValue(taskId.toString))
            .setExecutor(getExecutorInfo(driver))
            .setName(UUID.randomUUID().toString)
            .addResources(cpus)
            .setData(ByteString.copyFrom(Utils.serialize(task)))
            .build()
          driver.launchTasks(Collections.singleton(offer.getId), Collections.singleton(taskInfo))
        }
        case None => {
          //decline offer so that it will re offered
          driver.declineOffer(offer.getId)
        }
      }
    }
  }

  def runTasks[T](tasks: Task[T]*): Array[Option[T]] = {
    var finishCount = 0
    val allFinishedLock = new Object()
    var allFinished = false
    var taskCount = 0
    this._tasks.enqueue(tasks.zipWithIndex: _*)
    taskCount = _tasks.size
    val results = new Array[Option[T]](_tasks.length)
    statusCallaback = new StatusCallaback {
      override def statusUpdate(driver: SchedulerDriver, status: TaskStatus): Unit = {
        val taskId = status.getTaskId.getValue.toLong
        val message = status.getState match {
          case TaskState.TASK_FAILED | TaskState.TASK_KILLED | TaskState.TASK_LOST => {
            finishCount += 1
            val index = taskIdToIndex.get(taskId).get
            results(index) = None
            if (finishCount == taskCount) {
              allFinishedLock.synchronized {
                allFinished = true
                allFinishedLock.notifyAll()
              }
            }


            "failed"
          }
          case TaskState.TASK_FINISHED => {
            finishCount += 1
            val index = taskIdToIndex.get(taskId).get
            val result = Utils.deserialize[TaskResult[T]](status.getData.toByteArray)
            results(index) = Some(result.value)
            if (finishCount == taskCount) {
              allFinishedLock.synchronized {
                allFinished = true
                allFinishedLock.notifyAll()
              }
            }
            "successfully completed"
          }
          case _ => ""
        }
        println(s"$taskId  $message")

      }
    }

    allFinishedLock.synchronized {
      while (!allFinished) {
        allFinishedLock.wait()
      }
    }
    println("all finished")
    results
  }

  override def reregistered(driver: SchedulerDriver, masterInfo: MasterInfo) {

  }

  override def registered(driver: SchedulerDriver, frameworkId: FrameworkID, masterInfo: MasterInfo) {
    if (jars.size > 0) {
      createJarServer()
    }
  }

  def getExecutorInfo(d: SchedulerDriver): ExecutorInfo = {
    val scriptPath = System.getProperty("executor_script_path", "~/run-executor.sh")
    ExecutorInfo.newBuilder().
      setCommand(CommandInfo.newBuilder().setValue("" +
      "/bin/sh " + scriptPath + s" $jarUris"))
      .setExecutorId(ExecutorID.newBuilder().setValue("1234"))
      .build()
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
