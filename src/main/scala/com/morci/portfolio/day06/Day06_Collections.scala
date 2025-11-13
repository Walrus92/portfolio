package com.morci.portfolio.day06

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object Day06_Collections extends App {

  // Modelo de datos
  case class Transaction(id: Int, user: String, amount: Double, category: String)

  // Lista de transacciones (10 registros mínimo)
  val transactions = Seq(
    Transaction(1, "Lucía", 120.5, "shopping"),
    Transaction(2, "Miguel", 75.0, "food"),
    Transaction(3, "Lucía", 210.0, "travel"),
    Transaction(4, "Carla", 50.0, "food"),
    Transaction(5, "Miguel", 300.0, "electronics"),
    Transaction(6, "Lucía", 99.0, "food"),
    Transaction(7, "Carla", 450.0, "travel"),
    Transaction(8, "Miguel", 130.0, "shopping"),
    Transaction(9, "Lucía", 15.0, "misc"),
    Transaction(10, "Miguel", 500.0, "travel")
  )

  println("=== Transacciones mayores de 200€ ===")
  val altas = transactions.filter(_.amount > 200)
  altas.foreach(println)

  println("\n=== Gasto medio por categoría ===")
  val promedioPorCategoria = transactions
    .groupBy(_.category)
    .mapValues(t => t.map(_.amount).sum / t.size)

  promedioPorCategoria.foreach(println)

  println("\n=== Gasto total general (foldLeft) ===")
  val total = transactions.foldLeft(0.0)((acc, t) => acc + t.amount)
  println(total)

  println("\n=== Usuarios con alguna compra > 100€ ===")
  val usuarios = transactions
    .filter(_.amount > 100)
    .flatMap(t => Seq(t.user))
    .distinct

  println(usuarios)

  // Extra: función genérica
  def sumAmounts[T <: { def amount: Double }](seq: Seq[T]): Double =
    seq.foldLeft(0.0)((acc, t) => acc + t.amount)

  println("\n=== Total usando función genérica sumAmounts ===")
  println(sumAmounts(transactions))
}