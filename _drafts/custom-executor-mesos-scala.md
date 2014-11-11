---           
layout: post
title: "Custom mesos executor in Scala"
categories: mesos scala
---
In previous [post](/mesos-helloworld-scala), we discussed about how to write a simple mesos scheduler in Scala. In this post, we are going to see how to run general scala functions rather simple shell commands. 

This post assumes you already know different components of writing a scheduler in Mesos. If not refer to this [post](/mesos-helloworld-scala).

tl;dr Access the complete example code [here](https://github.com/phatak-dev/blog/tree/master/code/MesosCustomExecutor).

##Executor in Mesos
In last example, we used built in mesos executor called **CommandExecutor**. Command executor is capable of running any operating system binaries. But if we want to run java/scala code we will be needing to write our own executor to setup the environment and run the code.

Writing custom executor is not that straight forward. There are multiple pieces to write and run a full fledged custom executor. 

Follow the below steps to create a custom executor.


## Step 1 : Task abstraction

To run any function, we need to represent that function as a mesos task. The following trait represent the task abstraction.

{%highlight scala %} 
trait Task[T] extends Serializable{
  def run: T
}
{%endhighlight%}
  
Our task has a single method. It does not take anything. Type T signifies the return type of function. Note that task extends the serialization which allows us to send this function over wire to execute on Mesos cluster slaves.

{%highlight scala %} 
class  FunctionTask[T]( body: => () => T) extends Task[T] {
  override def run: T = body()
}  
{%endhighlight%}

FunctionTask is one of the implementation which wraps a given function inside task.

Now we have a task abstraction which we can instruct our scheduler to run



## Step 2 : TaskExecutor

TaskExecutor is our custom executor which takes above task abstraction. It's just a normal scala program which has a main method. It creates an instance of
mesos.Executor and listens on launchTask callback.

{%highlight scala %} 
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

    ......
    new MesosExecutorDriver(exec).run()
  }
{%endhighlight%}


In the code, we deserialize the task we got. As we know each of Task has a run method. We run each task on different thread so that our executor is blocked single thread.

Once the execution is done, we wrap the result and set the task state to finished. 

You can access complete code listing [here](https://github.com/phatak-dev/blog/blob/master/code/MesosCustomExecutor/src/main/scala/com/madhu/mesos/customexecutor/TaskExecutor.scala).


## Step 3 : Plug Custom Executor in Scheduler

Once we have the custom executor, next step is plug to our mesos scheduler. Mesos doesn't understand any specific language, it just understands how to run shell script. So we run our task executor from a shell script and specify the shell script path in executor info.

{%highlight scala %} 

 def getExecutorInfo(d: SchedulerDriver): ExecutorInfo = {
    val scriptPath = System.getProperty("executor_script_path","~/run-executor.sh")
    ExecutorInfo.newBuilder().
      setCommand(CommandInfo.newBuilder().setValue("" +
      "/bin/sh "+scriptPath))
      .setExecutorId(ExecutorID.newBuilder().setValue("1234"))
      .build()
  }

 
 //call setExecutor to set the executor info
 val taskInfo = TaskInfo.newBuilder()
          .setSlaveId(SlaveID.newBuilder().setValue(offer.getSlaveId.getValue).build())
          .setTaskId(TaskID.newBuilder().setValue(taskId.toString))
          .setExecutor(getExecutorInfo(driver))
          .setName(UUID.randomUUID().toString)
          .addResources(cpus)
          .setData(ByteString.copyFrom(Utils.serialize(task)))
          .build()

{%endhighlight%}

You can access complete code listing [here](https://github.com/phatak-dev/blog/blob/master/code/MesosCustomExecutor/src/main/scala/com/madhu/mesos/customexecutor/TaskScheduler.scala).

## Step 4 : run-executor script file
This script file, used by the executor info to instantiate. 

{%highlight sh %} 
  #!/bin/sh
echo "running Executor"
export LD_LIBRARY_PATH=$MESOS_HOME/src/.libs:$LD_LIBRARY_PATH
#update the path to point to jar
java -cp <project-path>/target/mesoscustomexecutor-1.0-SNAPSHOT.jar com.madhu.mesos.customexecutor.TaskExecutor

{%endhighlight%}

update <project-path> to the directory which has the build jar from code.


## Step 5 : Creating tasks
We create 2 simple tasks to just to print hi. Note how we can even access closure inside
our tasks.
{%highlight scala %} 
 
 def multipleTasks() = {
     0 to 1 map {
       index => {
         val f = new FunctionTask[Unit](() => println("hi" + index))
         f
       }
     }
 }

{%endhighlight%}

## Running 
Clone complete code from [github](https://github.com/phatak-dev/blog/tree/master/code/MesosCustomExecutor).

Run CustomTasks main method with mesos master url and path to the run-executor.sh shell script.

## Output

The output should be available in mesos logs as specified in this [post](mesos-helloworld-scala)




