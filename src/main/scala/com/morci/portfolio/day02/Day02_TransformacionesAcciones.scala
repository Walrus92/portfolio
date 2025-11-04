package com.morci.portfolio.day02

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Day02_TransformacionesAcciones extends App {

  val spark = SparkSession.builder()
    .appName("Día 2 - Transformaciones y Acciones")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Dataset artificial
  val usuarios = Seq(
    ("Lucía", 28),
    ("Miguel", 34),
    ("Laura", 42),
    ("Pedro", 19)
  ).toDF("nombre", "edad")

  // TRANSFORMACIONES
  val mayores = usuarios.filter($"edad" > 30)
  val conCategoria = mayores.withColumn("categoria", when($"edad" > 40, "senior").otherwise("adulto"))

  // ACCIONES
  conCategoria.show()
  println(s"Número de mayores de 30: ${conCategoria.count()}")

  spark.stop()
}
