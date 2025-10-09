ThisBuild / scalaVersion := "2.12.18"
ThisBuild / organization := "com.morci"
ThisBuild / version := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "portfolio",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.1",
      "org.apache.spark" %% "spark-sql"  % "3.5.1",
      "org.scalatest"    %% "scalatest"  % "3.2.19" % Test
    )
  )
