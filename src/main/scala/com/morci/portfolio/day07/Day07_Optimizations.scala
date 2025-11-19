package com.morci.portfolio.day07

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object Day07_Optimizations extends App {
  val spark = SparkSession.builder()
    .appName("Day 06 - Optimizations")
    .master("local[*]")
    .getOrCreate()
  spark.conf.set("spark.sql.adaptive.enabled", "true")

  spark.sparkContext.setLogLevel("ERROR")

  val productsDf = spark.read
    .option("header", "true")
    .option("multiline", "true")
    .option("inferSchema", "true")
    .json("src/main/resources/products.json")

  val categoriesDf = spark.read
    .option("header", "true")
    .option("multiline", "true")
    .option("inferSchema", "true")
    .json("src/main/resources/categories.json")

  val joinedDf = productsDf
    .filter(col("category").isNotNull && col("price") > 0)
    .join(categoriesDf.filter(col("category") === lit("Clothing")), Seq("Category"), "inner")
    .groupBy("category", "price")
    .agg(
      first("category"),
      max("price")
    )
    .drop("category", "price")
  joinedDf.show(false)
  joinedDf.explain(true)
  //  productsDf.printSchema()
  //  categoriesDf.printSchema()
  spark.stop()
}