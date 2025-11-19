package com.morci.portfolio.day07

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Day07_BroadcastVsSortMerged extends App {
  val spark = SparkSession.builder()
    .appName("Day 06 - Optimizations")
    .master("local[*]")
    .getOrCreate()
  //spark.conf.set("spark.sql.adaptive.enabled", "true")

  import spark.implicits._

  spark.sparkContext.setLogLevel("ERROR")

  val bigDf = (1 to 200000).toDF("id")
    .withColumn("category", when(col("id") % 5 === 0, "A")
      .when(col("id") % 5 === 1, "B")
      .when(col("id") % 5 === 2, "C")
      .when(col("id") % 5 === 3, "D")
      .otherwise("E"))

  val smallDf = Seq(
    ("A", "Group1"),
    ("B", "Group2"),
    ("C", "Group3"),
    ("D", "Group4"),
    ("E", "Group5")
  ).toDF("category", "group")

  val joinDefault = bigDf.join(smallDf, "category")
  joinDefault.explain(true)



  spark.stop()
}