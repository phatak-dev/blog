package com.madhukaraphatak.spark.extend

/**
 * Created by madhu on 20/1/15.
 */
class SalesRecord(val transactionId: String,
                  val customerId: String,
                  val itemId: String,
                  val itemValue: Double) extends Comparable[SalesRecord]
with Serializable {

  override def compareTo(o: SalesRecord): Int = {
    return this.transactionId.compareTo(o.transactionId)
  }

  override def toString: String = {
    transactionId+","+customerId+","+itemId+","+itemValue
  }
}
