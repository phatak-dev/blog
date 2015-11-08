package com.madhukaraphatak.akkahttp

import java.util.concurrent.ConcurrentLinkedDeque

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.madhukaraphatak.akkahttp.Models.{ServiceJsonProtoocol, Customer}
import spray.json.{JsArray, pimpAny, DefaultJsonProtocol}
import scala.collection.JavaConverters._

/**
  * Created by madhu on 8/11/15.
  */
object AkkaJsonParsing {



  def main(args: Array[String]) {

    implicit val actorSystem = ActorSystem("rest-api")

    implicit val actorMaterializer = ActorMaterializer()

    val list = new ConcurrentLinkedDeque[Customer]()

    import ServiceJsonProtoocol.customerProtocol
    val route =
      path("customer") {
        post {
          entity(as[Customer]) {
            customer => complete {
              list.add(customer)
              s"got customer with name ${customer.name}"
            }
          }
        } ~
          get {
            complete {
              JsArray(list.asScala.toList.map(_.toJson):_*)
            }
          }
      }

    Http().bindAndHandle(route, "localhost", 8080)
  }


}
