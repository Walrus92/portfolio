package com.morci.portfolio.day05

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object Day05_Windows extends App {

  // 1️⃣ Crear sesión Spark
  val spark = SparkSession.builder()
    .appName("Día 5 - Funciones ventana y agregaciones avanzadas")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("WARN")
  import spark.implicits._

  // 2️⃣ Leer dataset de ventas
  val salesDF = spark.read
    .option("multiline", "true")
    .json("src/main/resources/sales.json")

  println("=== Dataset original ===")
  salesDF.show(false)
  salesDF.printSchema()

  // 3️⃣ Definir ventana por vendedor, ordenada por fecha
  val wBySalesperson = Window.partitionBy("salesperson").orderBy("date")

  // 4️⃣ Calcular métricas por vendedor
  val enrichedDF = salesDF
    .withColumn("sale_num", row_number().over(wBySalesperson))
    .withColumn("running_total", sum("amount").over(wBySalesperson))
    .withColumn("avg_running", round(avg("amount").over(wBySalesperson), 2))
    .withColumn("diff_vs_prev", col("amount") - lag("amount", 1).over(wBySalesperson))

  println("=== Métricas por vendedor ===")
  enrichedDF.show(false)

  // 5️⃣ Calcular total por región (agrupación clásica)
  val regionAggDF = salesDF.groupBy("region")
    .agg(
      sum("amount").alias("region_total"),
      avg("amount").alias("region_avg"),
      count("*").alias("region_count")
    )

  println("=== Totales por región ===")
  regionAggDF.show(false)

  // 6️⃣ Unir resultados de métricas con el agregado regional
  val joinedDF = enrichedDF.join(regionAggDF, Seq("region"), "left")

  println("=== Enriquecido con totales regionales ===")
  joinedDF.show(false)

  // 7️⃣ Ranking global de vendedores por ventas totales
  val totalSalesBySeller = salesDF.groupBy("salesperson")
    .agg(sum("amount").alias("total_sales"))

  val wGlobal = Window.orderBy(desc("total_sales"))
  val rankedDF = totalSalesBySeller
    .withColumn("global_rank", rank().over(wGlobal))

  println("=== Ranking global por vendedor ===")
  rankedDF.show(false)

  // 8️⃣ Top 2 vendedores por región
  val wRegion = Window.partitionBy("region").orderBy(desc("amount"))
  val topRegionDF = salesDF
    .withColumn("rank_region", rank().over(wRegion))
    .filter($"rank_region" <= 2)

  println("=== Top 2 vendedores por región ===")
  topRegionDF.show(false)

  // 9️⃣ Guardar salida final en CSV
  joinedDF.write
    .mode("overwrite")
    .option("header", "true")
    .csv("src/main/resources/day05_output")

  println("✅ Resultados guardados en src/main/resources/day05_output")
}
