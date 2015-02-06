package com.madhukaraphatak.spark

import java.io.PrintWriter

import scala.io.Source

/**
 * Pipe implementation in Scala.
 */
object PipeExample {


  def main(args: Array[String]) {

    //pass complete path of echo.sh
    val command = args(0)

    val proc = Runtime.getRuntime.exec(Array(command))
    val lineList = List("hello","how","are","you")

    // redirect proc stderr to System.err
    new Thread("stderr reader for " + command) {
      override def run() {
        for(line <- Source.fromInputStream(proc.getErrorStream).getLines)
          System.err.println(line)
      }
    }.start()

    //pipe data to proc stdin
    new Thread("stdin writer for " + command) {
      override def run() {
        val out = new PrintWriter(proc.getOutputStream)
        for(elem <- lineList)
          out.println(elem)
        out.close()
      }
    }.start()


   //read data from proc stdout
   val outputLines = Source.fromInputStream(proc.getInputStream).getLines

   println(outputLines.toList)

  }

}
