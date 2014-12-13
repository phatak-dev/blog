package com.madhukaraphatak.mesos.shuffle

/**
 * Created by madhu on 28/10/14.
 */
object ShuffleTasks {


  def main(args: Array[String]) {

    val mesosURL = args(0)
    val executorScriptPath = args(1)
    System.setProperty("executor_script_path",executorScriptPath)
    val scheduler = new TaskScheduler(mesosURL)

    scheduler.start()

    val paragraph = List("hello how are you how are you i am fine let's go till end how are you",
      "hello how are you u asked me"
    )

    val twoSplits = paragraph.flatMap(value => value.split(" ")).map(value => (value, 1)).splitAt(10)
    val finalList = List[List[(String, Int)]](twoSplits._1, twoSplits._2)

    val createCombiner = (value: Int) => value
    val mergeValue = (combiner: Int, value: Int) => combiner + value
    val mergeCombiner = (c1: Int, c2: Int) => c1 + c2

    val result = new LocalShuffle().compute(finalList,createCombiner,mergeValue,mergeCombiner,2,scheduler)
    println("result is" +result)




  }


}
