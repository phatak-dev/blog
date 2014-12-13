package com.madhukaraphatak.mesos.shuffle

import java.io.File
import java.net.InetAddress

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.{DefaultHandler, HandlerList, ResourceHandler}
import org.eclipse.jetty.util.thread.QueuedThreadPool

/**
 * Created by madhu on 20/10/14.
 */
class HttpServer(resourceBase: File) {
  var server: Server = null
  var port: Int = -1

  def start() = {
    if (server != null) {
      throw new RuntimeException("server already running")
    }
    else {

      val threadPool = new QueuedThreadPool()
      threadPool.setDaemon(true)

      server = new Server(0)
      server.setThreadPool(threadPool)


      val resourceHandler = new ResourceHandler
      resourceHandler.setResourceBase(resourceBase.getAbsolutePath)

      val handlerList = new HandlerList
      handlerList.setHandlers(Array(resourceHandler, new DefaultHandler))

      server.setHandler(handlerList)
      server.start()
      port = server.getConnectors()(0).getLocalPort
    }

  }

  def stop() {
    if (server == null) {
      throw new RuntimeException("server already stopped")

    }
    else {
      server.stop()
      server = null
      port = - 1
    }

  }

  def uri:String = {
    if(server==null) {
      throw new RuntimeException("server not started")
    }
    else {
      return "http://"+ getLocalIpAddress+":"+port
    }



  }

  private def getLocalIpAddress: String = {
    // Get local IP as an array of four bytes
    InetAddress.getLocalHost().getHostAddress
  }
}

object HttpServer {

  def main(args: Array[String]) {

    val httpServer = new HttpServer(new File("/home/madhu/Media/Books"))
    httpServer.start()
    println(httpServer.uri)
    System.in.read()
    httpServer.stop()
  }

}