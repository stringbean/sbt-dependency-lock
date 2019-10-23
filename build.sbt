name := "sbt-dependency-lock"
organization := "software.purpledragon"

version := "0.1-SNAPSHOT"

enablePlugins(SbtPlugin)

val circeVersion = "0.11.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)