package com.morci.portfolio.day01

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object InspectPlan extends App {

  val spark = SparkSession.builder()
    .appName("Day01 - Spark Plan Inspection")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val df = Seq(
    ("Miguel", 31),
    ("Lucía", 29),
    ("Pepe", 42),
    ("Ana", 33)
  ).toDF("nombre", "edad")

  val filtered = df.filter($"edad" > 30).select($"nombre")

  // Acción que dispara el job
  filtered.show()

  // Plan lógico
  println("\n== Logical Plan ==")
  filtered.explain(extended = false)

  // Plan físico completo
  println("\n== Physical Plan (extended) ==")
  filtered.explain(extended = true)

  spark.stop()
}
