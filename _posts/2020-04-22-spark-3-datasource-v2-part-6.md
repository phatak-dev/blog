---
layout: post
title: "Data Source V2 API in Spark 3.0 - Part 6 : MySQL Source"
date : 2020-04-22
categories: scala spark spark-three datasource-v2-spark-three
---
Spark 3.0 is a major release of Apache Spark framework. It's been in preview from last December and going to have  a stable release very soon. As part of major release, Spark has a habit of shaking up API's to bring it to latest standards. There will be breaking changes also in these API's. One of such API is Data source V2 API.

Data Source V2 API, a new data source API for spark, was introduced in spark 2.3. Then it's been updated in spark 2.4. I have written detailed posts on same [here](/categories/datasource-v2-series).

This API is going to be completely changed in Spark 3.0. Spark rarely change an API this frequently in between releases. But as data source are heart of the framework, they are improved constantly. Also in spark 2.4, these API's were marked **evolving**. This means they are meant to be changed in future.

The usage of the data sources have not changed in 3.0. So if you are a user of the third party data sources you don't need to worry. These changes are geared mainly towards the developer of these sources. Also all the sources written V1 API going to work even in 3.0. So if your source is not updated, no need to panic. It's going to work without latest optimisations.

These new changes in V2 API brings more control to data source developer and better integration with spark optimiser. Moving to this API makes third party sources more performant. So in these series of posts I will be discussing the new Data source V2 API in 3.0.

This is sixth post in the series where we discuss about implementing data source which can write to MySQL.You can read all the post in the series [here](/categories/datasource-v2-spark-three).


## DefaultSource Implementation

As any source, default source will be entry point for our MySQL source. The below code implements **TableProvider**.

{% highlight scala %}

class DefaultSource extends TableProvider{
    override def inferSchema(caseInsensitiveStringMap: 
                             CaseInsensitiveStringMap): StructType =
    getTable(null,Array.empty[Transform],
               caseInsensitiveStringMap.asCaseSensitiveMap()).schema()

  override def getTable(structType: StructType, 
      transforms: Array[Transform], map: util.Map[String, String]): Table ={
      new MysqlTable
    }


{% endhighlight %}


## MysqlTable

MysqlTable extends **SupportsWrite** to indicate it supports writing in below code.

{% highlight scala %}

class MysqlTable extends SupportsWrite{

  private val tableSchema = new StructType().add("user", StringType)


  override def name(): String = this.getClass.toString

  override def schema(): StructType = tableSchema

  override def capabilities(): util.Set[TableCapability] = Set(TableCapability.BATCH_WRITE,
    TableCapability.TRUNCATE).asJava

  override def newWriteBuilder(logicalWriteInfo: LogicalWriteInfo): WriteBuilder 
           = new MysqlWriterBuilder
}

{% endhighlight %}

Here we are hard coding the table schema. Also we are exposing the write capabilities as **BATCH_WRITE** and **TRUNCATE**.

## MysqlWriterBuilder

MysqlWriterBuilder builds a batch writer for our source.

{% highlight scala %}

class MysqlWriterBuilder extends WriteBuilder{
  override def buildForBatch(): BatchWrite = new MysqlBatchWriter()
}

{% endhighlight %}

## MysqlBatchWriter

MysqlBatchWriter creates writer factories for our source.

{% highlight scala %}

class MysqlBatchWriter extends BatchWrite{
  override def createBatchWriterFactory(physicalWriteInfo: PhysicalWriteInfo): DataWriterFactory = new
  MysqlDataWriterFactory

  override def commit(writerCommitMessages: Array[WriterCommitMessage]): Unit = {}

  override def abort(writerCommitMessages: Array[WriterCommitMessage]): Unit = {}
}

{% endhighlight %}

## MysqlDataWriterFactory

MysqlDataWriterFactory generates writers for the source. Here we don't need to specify the list as the number of partitions is already determined by the input data.

{% highlight scala %}

class MysqlDataWriterFactory extends DataWriterFactory {
  override def createWriter(partitionId: Int, taskId:Long): DataWriter[InternalRow] = new MysqlWriter()
}

{% endhighlight %}

##MysqlWriter

MysqlWriter is the one which actually writes the data to MySQL using JDBC API.

{% highlight scala %}

class MysqlWriter extends DataWriter[InternalRow] {
  val url = "jdbc:mysql://localhost/test"
  val user = "root"
  val password = "abc123"
  val table ="userwrite"

  val connection = DriverManager.getConnection(url,user,password)
  val statement = "insert into userwrite (user) values (?)"
  val preparedStatement = connection.prepareStatement(statement)


  override def write(record: InternalRow): Unit = {
    val value = record.getString(0)
    preparedStatement.setString(1,value)
    preparedStatement.executeUpdate()
  }

  override def commit(): WriterCommitMessage = WriteSucceeded

  override def abort(): Unit = {}

  override def close(): Unit = {}
}

{% endhighlight %}

In this code, credentials are hard coded. Update them according to your source.

## Using the Mysql Source

The below code shows how to use the source from the user code.

{% highlight scala %}

simpleMysqlDf.write
      .format(
        "com.madhukaraphatak.spark.sources.datasourcev2.simplemysqlwriter")
      .mode(SaveMode.Append)
      .save()

{% endhighlight%}

## Code

You can access complete code on [github](https://github.com/phatak-dev/spark-3.0-examples/blob/master/src/main/scala/com/madhukaraphatak/spark/sources/datasourcev2/SimpleMysqlWriterDataSource.scala).

## Conclusion

Datasource V2 API brings new functionalities to spark data sources. In this post we saw how to build a source which can write to relational databases.
