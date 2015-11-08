package com.madhukaraphatak.akkahttp.testable

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

/**
  * Created by madhu on 8/11/15.
  */


class RestServer(implicit val system:ActorSystem,
implicit  val materializer:ActorMaterializer) extends RestService{
  def startServer(address:String, port:Int) = {
    Http().bindAndHandle(route,address,port)
  }
}

object RestServer {

  def main(args: Array[String]) {

    implicit val actorSystem = ActorSystem("rest-server")
    implicit val materializer = ActorMaterializer()

    val server = new RestServer()
    server.startServer("localhost",8080)
  }
}

