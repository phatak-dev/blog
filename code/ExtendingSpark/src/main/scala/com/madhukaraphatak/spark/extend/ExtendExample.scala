package com.madhukaraphatak.spark.extend

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import CustomFunctions._

/**
 * Created by madhu on 27/2/15.
 */
object ExtendExample {

  def main(args: Array[String]) {

    val sc = new SparkContext(args(0), "extendingspark")
    val dataRDD = sc.textFile(args(1))
    val salesRecordRDD = dataRDD.map(row => {
      val colValues = row.split(",")
      new SalesRecord(colValues(0),colValues(1),colValues(2),colValues(3).toDouble)
    })

    salesRecordRDD.map(_.itemValue).sum

    println(salesRecordRDD.totalSales)

    // discount RDD
    val discountRDD = salesRecordRDD.discount(0.1)
    println(discountRDD.collect().toList)
  }

}
