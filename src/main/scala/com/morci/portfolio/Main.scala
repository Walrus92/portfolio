package com.morci.portfolio

import org.apache.spark.sql.SparkSession

object Main extends App {
  val spark = SparkSession.builder()
    .appName("Test Scala 2.12 con Spark")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val df = Seq(("Miguel", 31), ("Lucía", 29)).toDF("nombre", "edad")
  df.show()

  spark.stop()
}
