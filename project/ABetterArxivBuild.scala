import sbt._
import sbt.Keys._

object ABetterArxivBuild extends Build {

  lazy val aBetterArxiv = Project(
    id = "a-better-arxiv",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "A better arxiv",
      organization := "com.sentimentalarse",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.1"
      // add other settings here
    )
  )
}
