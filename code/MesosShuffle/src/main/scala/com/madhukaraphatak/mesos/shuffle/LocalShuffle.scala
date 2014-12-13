package com.madhukaraphatak.mesos.shuffle

import java.io._
import java.net.URL

import scala.Serializable
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
 * Shuffle algorithm implemented using local files
 * It generates partitions based on the hash of the key value. Once the
 * local ccombineris ran the output is written into the local files and
 * a http server is started to serve them.
 *
 */
class LocalShuffle[K, V, C] extends Shuffle[K, V, C] with Serializable {
  override def compute(rows: List[List[(K, V)]], createCombiner: (V) => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numberOfOutputSplit: Int, scheduler: TaskScheduler):
  List[(K, C)] = {

    def reduceTask(index: Int, splitsByUri: mutable.HashMap[String, ArrayBuffer[Int]]) = {
      new FunctionTask[HashMap[K, C]](() => {
        val combiners = new HashMap[K, C]
        for ((uri, splits) <- Utils.shuffle(splitsByUri)) {
          for (split <- splits) {
            val url = "%s/%d-%d".format(uri,split,index)
            val inputStream = new ObjectInputStream(new URL(url).openStream())
            try {
              while (true) {
                val (k, c) = inputStream.readObject().asInstanceOf[(K, C)]
                combiners(k) = combiners.get(k) match {
                  case Some(oldC) => mergeCombiners(oldC, c)
                  case None => c
                }
              }
            } catch {
              case e: EOFException => {}
            }
          }}
          combiners
        })
    }

    /**
     * forEach split run the following function.
     * @param list of key value pairs
     * @return
     */

    def forEachTask(pair: (List[(K, V)], Int)) = {
      new FunctionTask[(Int, String)](() => {
        val tempDir = Utils.createTempDir("/tmp/shuffleLocal").getAbsoluteFile
        println(s"using tempDir $tempDir")
        val index = pair._2
        val iterator = pair._1
        val buckets = Array.tabulate(numberOfOutputSplit)(_ => new HashMap[K, C])
        for ((k, v) <- iterator) {
          var bucketId = k.hashCode() % numberOfOutputSplit
          if (bucketId < 0) {
            bucketId += numberOfOutputSplit
          }
          val bucket = buckets(bucketId)
          bucket(k) = bucket.get(k) match {
            case Some(c) => mergeValue(c, v)
            case None => createCombiner(v)
          }
        }

        //write the output to the local directory
        val paths = (0 until numberOfOutputSplit).map {
          i =>
            val path = new File(tempDir, "%d-%d".format(index, i))
            val out = new ObjectOutputStream(new FileOutputStream(path))
            buckets(i).foreach(pair => out.writeObject(pair))
            out.close()
            path.getName
        }


        //serve the local files using http server
        val httpServer = new HttpServer(tempDir)
        httpServer.start()
        (index, httpServer.uri)
      })


    }
    //number each of the splits
    val numberedList = rows.zipWithIndex

    //for each split run forEachTask
    val tasks = numberedList.map(value => forEachTask(value))
    val mapping = scheduler.runTasks(tasks: _*).map(_.get)

    //map the splits to Uri
    val splitsByUri = new mutable.HashMap[String, mutable.ArrayBuffer[Int]]()

    for ((index, uri) <- mapping) {
      splitsByUri.getOrElseUpdate(uri, ArrayBuffer[Int]()) += index
    }

    //now run the global reduceTasks
    val reduceTasks = (0 until numberOfOutputSplit).map(index => reduceTask(index,splitsByUri)).toList
    val reducedValues = scheduler.runTasks(reduceTasks:_*)
    reducedValues.map(_.get).flatten.toList

  }

}
