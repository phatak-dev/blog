package com.madhukaraphatak.mesos.customjars

import java.io._
import java.util.UUID

/**
 * Created by madhu on 1/10/14.
 */
object Utils {

  def serialize[T](o: T): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(bos)
    oos.writeObject(o)
    oos.close
    return bos.toByteArray
  }

  def deserialize[T](bytes: Array[Byte]): T = {
    val bis = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bis)
    return ois.readObject.asInstanceOf[T]
  }

  def deserialize[T](bytes: Array[Byte], loader: ClassLoader): T = {
    val bis = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bis) {
      override def resolveClass(desc: ObjectStreamClass) =
        Class.forName(desc.getName, false, loader)
    }
    return ois.readObject.asInstanceOf[T]
  }

  def createTempDir(root: String = System.getProperty("java.io.tmpdir")): File =
  {
    var attempts = 0
    val maxAttempts = 10
    var dir: File = null
    while (dir == null) {
      attempts += 1
      if (attempts > maxAttempts) {
        throw new IOException("Failed to create a temp directory " +
          "after " + maxAttempts + " attempts!")
      }
      try {
        dir = new File(root, "mesos-" + UUID.randomUUID.toString)
        if (dir.exists() || !dir.mkdirs()) {
          dir = null
        }
      } catch { case e: IOException => ; }
    }
    return dir
  }

  def copyStream(src:InputStream,out:OutputStream,closeStream:Boolean=false) = {
    val arrayBuffer = new Array[Byte](8192)
    var n = 0
    while(n != -1) {
      n = src.read(arrayBuffer)
      if(n!= -1){
        out.write(arrayBuffer,0,n)
      }
    }
    if(closeStream){
      src.close()
      out.close()
    }

  }

}
