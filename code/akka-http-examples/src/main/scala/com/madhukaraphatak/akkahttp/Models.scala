package com.madhukaraphatak.akkahttp

import spray.json.DefaultJsonProtocol

/**
  * Created by madhu on 8/11/15.
  */
object Models {
  case class Customer(name: String)
  object ServiceJsonProtoocol extends DefaultJsonProtocol {
    implicit val customerProtocol = jsonFormat1(Customer)
  }
}
