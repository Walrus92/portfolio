package com.morci.portfolio.day03

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Day03_Joins extends App {

  val spark = SparkSession.builder()
    .appName("Día 2 - Transformaciones y Acciones")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  /*
  Lee ambos JSON en DataFrames

  Realiza un inner join para unir usuarios y compras

  Realiza un left_anti join para obtener usuarios que no han comprado

  Muestra el top 3 usuarios por gasto total (agrega por user_id)

  Normaliza el país a minúsculas sin espacios

  Haz un broadcast join manual si el dataset de usuarios es muy pequeño

  Guarda el resultado final en /day03/output/resultado.csv

  * */
  val users_path = "src/main/resources/users.json"
  val purchases_path = "src/main/resources/purchases.json"

  val users_df = spark.read
    .option("header", "true")
    .option("multiline", "true")
    .option("inferSchema", "true")
    .json(users_path)
  val purchases_df = spark.read
    .option("header", "true")
    .option("multiline", "true")
    .option("inferSchema", "true")
    .json(purchases_path)

  val joined_df = users_df.join(purchases_df, Seq("user_id"), "inner")
  joined_df.show()

  users_df.join(purchases_df, Seq("user_id"), "left_anti").show()

  val gastoTotalDF = joined_df.groupBy(col("user_id"))
    .agg(sum(col("amount")).alias("total_gastado"))
    .orderBy(desc("total_gastado"))
    .limit(3)

  println("Top 3 usuarios por gasto:")
  gastoTotalDF.show(false)

  //noramlizar pais a minus sin space
  val usersNormalizedDF = users_df.withColumn("country", trim(lower($"country")))
  usersNormalizedDF.show(4)

  println("broadcast users:")
  val broadcastedUsers = broadcast(usersNormalizedDF).join(purchases_df, Seq("user_id"), "inner")
  broadcastedUsers.show(false)

  //guardar en csv
  broadcastedUsers.write.mode("overwrite")
    .option("header","true")
    .csv("src/main/resources/day03_output")
  spark.stop()
}
