name         := "sbt-dependency-lock"
organization := "software.purpledragon"

enablePlugins(SbtPlugin, ParadoxSitePlugin, GhpagesPlugin)

val circeVersion = "0.14.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "software.purpledragon" %% "text-utils" % "1.3.1",
  "org.scalatest"         %% "scalatest"  % "3.2.14" % Test,
)

organizationName := "Michael Stringer"
startYear        := Some(2019)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

scriptedLaunchOpts           := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

ThisBuild / scapegoatVersion := "2.1.6"

developers := List(
  Developer("stringbean", "Michael Stringer", "@the_stringbean", url("https://github.com/stringbean")),
)

homepage       := Some(url("https://github.com/stringbean/sbt-dependency-lock"))
scmInfo        := Some(
  ScmInfo(
    url("https://github.com/stringbean/sbt-dependency-lock"),
    "https://github.com/stringbean/sbt-dependency-lock.git"))
git.remoteRepo := "git@github.com:stringbean/sbt-dependency-lock.git"
publishTo      := sonatypePublishToBundle.value

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
  releaseStepCommand("sonatypeBundleRelease"),
  releaseStepTask(ghpagesPushSite),
  setNextVersion,
  commitNextVersion,
  pushChanges,
)

previewLaunchBrowser := false
