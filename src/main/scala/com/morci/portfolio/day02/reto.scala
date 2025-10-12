package com.morci.portfolio.day02

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{when, _}

object reto extends App {

  val spark = SparkSession.builder()
    .appName("Día 2 - Reto de transacciones bancarias")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val path = "src/main/resources/transactions.json"
  val df = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .json(path)
    .select("account_number", "transaction_amount", "transaction_category", "transaction_date", "transaction_description", "transaction_type")
  df.show()

  val dfResult = df
    .filter($"transaction_amount" > 0)
    .withColumn("transaction_date_format", to_date($"transaction_date", "M/d/yyyy"))
    .withColumn("year", year($"transaction_date_format"))
    .withColumn("month", month($"transaction_date_format"))
    .withColumn("tipo_movimiento",
      when(col("transaction_type").isin("deposit", "transfer"), "entrada")
        .when(col("transaction_type").isin("withdrawal", "payment"), "salida")
        .otherwise("Otro")
    )
    .withColumn("transaction_category", lower(trim($"transaction_category")))

  //analisis
  dfResult.groupBy("tipo_movimiento").count().show()
  //5 categorias màs frecuentes
  dfResult.groupBy("transaction_category").count().orderBy(desc("count")).show(5)
  //gasto total por mes y año
  dfResult.groupBy("year","month").agg(sum("transaction_amount"))
    .orderBy("year", "month")
    .show()
  //transacciones duplicadas:ccount_number + transaction_amount + transaction_date
  dfResult.groupBy("account_number","transaction_amount","transaction_date")
    .count()
    .filter($"count">1)
    .show(false)
  spark.stop()
}
