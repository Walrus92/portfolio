package com.morci.portfolio.day04

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object Day04_StructuredTypes extends App {

  val spark = SparkSession.builder()
    .appName("Día 4 - Columnas complejas y estructuras anidadas")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  // 1 Leer el JSON con estructuras anidadas
  val df = spark.read
    .option("multiline", "true")
    .json("src/main/resources/user_events.json")

  println("=== Estructura del DataFrame original ===")
  df.printSchema()

  // 2 Acceso a campos dentro de structs
  val flattenDF = df.select(
    $"user_id",
    $"profile.name".alias("name"),
    $"profile.country".alias("country"),
    $"metadata.device".alias("device"),
    $"metadata.ip".alias("ip"),
    $"profile.preferences".alias("preferences"),
    $"events"
  )

  println("=== DataFrame con campos planos ===")
  flattenDF.show(false)

  // 3️⃣ Derivar columnas nuevas
  val enrichedDF = flattenDF
    .withColumn("country_lower", lower(trim($"country")))
    .withColumn("num_preferences", size($"preferences"))
    .withColumn("is_mobile", $"device" === "mobile")

  println("=== DataFrame enriquecido con columnas derivadas ===")
  enrichedDF.show(false)

  // 4️⃣ Explode de los eventos (1 fila por evento)
  val explodedDF = enrichedDF
    .withColumn("event", explode($"events"))
    .withColumn("event_type", $"event.type")
    .withColumn("event_timestamp", $"event.timestamp")
    .drop("event")

  println("=== DataFrame con eventos explotados ===")
  explodedDF.show(false)

  // 5️⃣ Condicional con when/otherwise
  val categorizedDF = explodedDF.withColumn(
    "user_category",
    when($"num_preferences" >= 3, "multi-interest")
      .when($"num_preferences" === 2, "medium-interest")
      .otherwise("low-interest")
  )

  println("=== DataFrame con categorización ===")
  categorizedDF.show(false)

  // 6️ Guardar en CSV
  val dfToWrite = categorizedDF.withColumn(
      "preferences",
      concat_ws(", ", col("preferences"))
    )
    .withColumn("events", to_json(col("events")))


  dfToWrite.write
    .mode("overwrite")
    .option("header", "true")
    .csv("src/main/resources/day04_output")

  println(" Datos escritos en src/main/resources/day04_output")
}
