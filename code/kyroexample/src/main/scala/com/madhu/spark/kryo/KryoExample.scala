package com.madhu.spark.kryo

import java.io.ByteArrayOutputStream

import com.esotericsoftware.kryo.io.{Input, Output}
import org.apache.hadoop.io.{BytesWritable, NullWritable}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoSerializer
import org.objenesis.strategy.StdInstantiatorStrategy

import scala.reflect.ClassTag


/**
 * Example code showing how to kryo serialization for disk
 */

object KryoExample {

  /*
   * Used to write as Object file using kryo serialization
   */
  def saveAsObjectFile[T: ClassTag](rdd: RDD[T], path: String) {
    val kryoSerializer = new KryoSerializer(rdd.context.getConf)

    rdd.mapPartitions(iter => iter.grouped(10)
      .map(_.toArray))
      .map(x => {
      //initializes kyro and calls your registrator clas
      val kryo = kryoSerializer.newKryo()

      //convert data to bytes
      val bao = new ByteArrayOutputStream()
      val output = new Output(bao)
      kryo.writeClassAndObject(output, x)
      output.close()

      // We are ignoring key field of sequence file
      val byteWritable = new BytesWritable(bao.toByteArray)
      (NullWritable.get(), byteWritable)
    }).saveAsSequenceFile(path)
  }

  /*
   * Method to read from object file which is saved kryo format.
   */
  def objectFile[T](sc: SparkContext, path: String, minPartitions: Int = 1)(implicit ct: ClassTag[T]) = {
    val kryoSerializer = new KryoSerializer(sc.getConf)

    sc.sequenceFile(path, classOf[NullWritable], classOf[BytesWritable], minPartitions)
      .flatMap(x => {
      val kryo = kryoSerializer.newKryo()
      // using StdInstantiatorStrategy to skip requirement of default constructor in custom classes
      // refer https://github.com/EsotericSoftware/kryo#object-creation
      kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
      val input = new Input()
      kryo.setClassLoader(Option(Thread.currentThread().getContextClassLoader)
        .getOrElse(getClass.getClassLoader))
      input.setBuffer(x._2.getBytes)
      val data = kryo.readClassAndObject(input)
      val dataObject = data.asInstanceOf[Array[T]]
      dataObject
    })

  }

  // user defined class that need to serialized
  class Person(val name: String)

  def main(args: Array[String]) {

    if (args.length < 1) {
      println("Please provide output path")
      return
    }
    val outputPath = args(0)

    val conf = new SparkConf().setMaster("local").setAppName("test")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sc = new SparkContext(conf)

    //create some dummy data
    val personList = 1 to 10000 map (value => new Person(value + ""))
    val personRDD = sc.makeRDD(personList)

    saveAsObjectFile(personRDD, outputPath)
    val rdd = objectFile[Person](sc, outputPath)
    println(rdd.map(person => person.name).collect().toList)
  }

}
