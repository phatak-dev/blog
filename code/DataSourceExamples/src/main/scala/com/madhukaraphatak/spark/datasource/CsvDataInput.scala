package com.madhukaraphatak.spark.datasource

import org.apache.spark.SparkContext

/**
 * Loading sales csv using DataFrame API
 */
object CsvDataInput {
  def main(args: Array[String]) {

    val sc = new SparkContext(args(0), "Csv loading example")
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val df = sqlContext.load("com.databricks.spark.csv", Map("path" -> args(1),"header"->"true"))
    df.printSchema()
    df.registerTempTable("sales")
    val aggDF = sqlContext.sql("select sum(amountPaid) from sales")
    println(aggDF.collectAsList())
  }

}
