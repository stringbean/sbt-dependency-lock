scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.9",
  // lockfile has 3.0.8
  "org.scalatest"     %% "scalatest"     % "3.0.7" % Test
)

dependencyLockAutoCheck := DependencyLockUpdateMode.AutoUpdate

val checkLog      = taskKey[Unit]("checks the contents of the log")
val checkLockfile = taskKey[Unit]("checks the contents of the lockfile")

checkLog := {
  val lastLog = BuiltinCommands.lastLogFile(state.value).get
  val last    = IO.read(lastLog)

  if (!last.contains("Dependency lockfile is outdated")) {
    sys.error("check did not contain warning")
  }
}

checkLockfile := {
  val lockfile = dependencyLockRead.value.getOrElse(sys.error("Failed to read updated lockfile"))

  val scalatest = lockfile.dependencies
    .find(dep => dep.org == "org.scalatest" && dep.name == "scalatest_2.12")
    .getOrElse(sys.error("Failed to find scalatest dependency"))

  if (scalatest.version != "3.0.7") {
    sys.error("Updated lockfile had incorrect scalatest version")
  }
}
