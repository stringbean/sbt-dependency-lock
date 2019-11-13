name := "sbt-dependency-lock"
organization := "software.purpledragon"

enablePlugins(SbtPlugin)

val circeVersion = "0.11.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

organizationName := "Michael Stringer"
startYear := Some(2019)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scapegoatVersion in ThisBuild := "1.3.11"

developers := List(
  Developer("stringbean", "Michael Stringer", "@the_stringbean", url("https://github.com/stringbean"))
)

homepage := Some(url("https://github.com/stringbean/sbt-dependency-lock"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/stringbean/sbt-dependency-lock"),
    "https://github.com/stringbean/sbt-dependency-lock.git"))

bintrayPackageLabels := Seq("sbt", "sbt-plugin", "lockfile")

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  releaseStepInputTask(scripted),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)