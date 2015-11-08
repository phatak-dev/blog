package com.madhukaraphatak.akkahttp.testable

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model._


/**
  * Created by madhu on 8/11/15.
  */
class RestSpec extends WordSpec with Matchers with ScalatestRouteTest with RestService{
  "Customer API" should {
    "Posting to /customer should add the customer" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "name":"test"
           |}
        """.stripMargin)

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/customer",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))


      postRequest ~> route ~> check {
         status.isSuccess() shouldEqual true
      }
    }

  }
}
