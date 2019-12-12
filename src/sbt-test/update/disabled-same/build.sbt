scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.commons"  %  "commons-lang3"  % "3.9",
  "org.scalatest"       %% "scalatest"      % "3.0.8"   % Test,
)

dependencyLockAutoCheck := DependencyLockUpdateMode.CheckDisabled

val checkLog = taskKey[Unit]("checks the contents of the log")

checkLog := {
  val lastLog = BuiltinCommands.lastLogFile(state.value).get
  val last = IO.read(lastLog)

  if (last.contains("Dependency lockfile is outdated")) {
    sys.error("check contained warning")
  }
}