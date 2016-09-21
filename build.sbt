import sbt._

name := "A better ML Arxiv"

version := "1.0"

scalaVersion := "2.11.1"

autoScalaLibrary := false

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "org.xerial" % "sqlite-jdbc" % "3.8.10.1",
  "com.typesafe" % "config" % "1.3.0"
)

