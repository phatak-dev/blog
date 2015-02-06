---           
layout: post
title: "Pipe in Spark"
date : 2015-02-07
categories: spark scala
---

Pipe operator in Spark, allows developer to process RDD data using external applications. Sometimes in data analysis, we need to use an external library which may not be written using Java/Scala. Ex: Fortran math libraries. In that case, spark's pipe operator allows us to send the RDD data to the external application.

In this post, first we are going to look at how we can use pipe operator. Once we understand the usage, then we will see how we can implement pipe operation in normal scala programs. This implementation is taken from spark implementation.

## Pipe in Spark

The following steps shows how to use pipe operator. To start with, we will create an RDD from inmemory list.

### Step 1 : Create a RDD

{% highlight scala%}
 val data = List("hi","hello","how","are","you")
 val dataRDD = sc.makeRDD(data) //sc is SparkContext
{% endhighlight %}

### Step 2 : Create a shell script 

Once we have RDD, then we will pipe it to a shell script. Let's create a file called *echo.sh*, then put the following content.

{% highlight sh %}
#!/bin/sh
echo "Running shell script"
while read LINE; do
   echo ${LINE}    
done
{% endhighlight %}

This is a simple shell script which reads the input from stdin and output that to stdout. You can do any other shell operation in this shell script.

### Step 3 : Pipe rdd data to shell script

One we have the shell script, we can pipe the RDD through this script. Make sure that you change the *scriptPath* variable to match path of your file.

{% highlight scala%}
val scriptPath = "/home/hadoop/echo.sh"
val pipeRDD = dataRDD.pipe(scriptPath)
pipeRDD.collect()
{% endhighlight %}

Now you should able to see, the line printed on console with echo messages from shell script. In place of shell script, you can use any other executable.


## Pipe implementation

Now we understand what pipe does. Let's look at how it is implemented. In this section, we develop a simple scala program which pipes data to the above shell script. 



Every executable is represented as a process in the operating system. So we will create a process which runs the shell script command.

{% highlight scala %}
  
val proc = Runtime.getRuntime.exec(Array(command))

{% endhighlight %}

Every process has three streams associated with it. They are **stdin** - standard input, **stdout** - standard output and **stderr** - Standard error. It's important to capture errors produced by the process. So we redirect the process stderr to java stderr. This runs in separate thread as these streams are asynchronous in nature.

{% highlight scala %}
  
  new Thread("stderr reader for " + command) {
      override def run() {
        for(line <- Source.fromInputStream(proc.getErrorStream).getLines)
          System.err.println(line)
      }
    }.start()

{% endhighlight %}

In the above code, we read from the *proc.getErrorStream* and pass it to the *System.err*

As next step, we create some data using List. Then we pass this data to *proc.getOutputStream*. Output to the stream is piped into the standard input stream of the process.


{% highlight scala %}
  val lineList = List("hello","how","are","you")
  new Thread("stdin writer for " + command) {
      override def run() {
        val out = new PrintWriter(proc.getOutputStream)
        for(elem <- lineList)
          out.println(elem)
        out.close()
      }
    }.start()
{% endhighlight %}

It's not enough to send the data, we also want to collect the output.

{% highlight scala %}
 
  val outputLines = Source.fromInputStream(proc.getInputStream).getLines
  println(outputLines.toList)

{% endhighlight %}

We collect the output by reading from *proc.getInputStream*. You can access complete code on [github]({{ "/PipeExample" | prepend: site.github_code_root }}). 

So now we understand how to use pipe and how is pipe is implemented.
