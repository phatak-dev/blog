package com.madhukaraphatak.spark.datasource

import org.apache.spark.SparkContext

/**
 * Loading sales json using DataFrame API
 */
object JsonDataSource {
  def main(args: Array[String]) {

    val sc = new SparkContext(args(0), "Json loading example")
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val df = sqlContext.load("org.apache.spark.sql.json", Map("path" -> args(1)))
    df.printSchema()
    df.registerTempTable("sales")
    val aggDF = sqlContext.sql("select sum(amountPaid) from sales")
    println(aggDF.collectAsList())
  }

}
