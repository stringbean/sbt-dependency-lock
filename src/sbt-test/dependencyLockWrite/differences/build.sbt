import scala.collection.SortedSet

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.commons"  %  "commons-lang3"  % "3.9",
  // lockfile has 3.0.8
  "org.scalatest"       %% "scalatest"      % "3.0.7"   % Test,
)

val checkLockfile = taskKey[Unit]("checks the contents of the lockfile")

checkLockfile := {
  val lockfile = dependencyLockRead.value.getOrElse(sys.error("Failed to read updated lockfile"))

  def checkDependency(org: String, name: String, version: String, configs: SortedSet[String]): Unit = {
    val dependency = lockfile.dependencies
      .find(dep => dep.org == org && dep.name == name)
      .getOrElse(sys.error(s"Failed to find dependency: $org % $name"))

    if (dependency.version != version) {
      sys.error(s"Incorrect version (${dependency.version}, expected $version) for dependency $org % $name")
    }

    if (dependency.configurations != configs) {
      sys.error(s"Incorrect configs (${dependency.configurations}, expected $configs) for dependency $org % $name")
    }
  }

  checkDependency("org.apache.commons", "commons-lang3", "3.9", SortedSet("compile", "runtime", "test"))
  checkDependency("org.scalatest", "scalatest_2.12", "3.0.7", SortedSet( "test"))
}