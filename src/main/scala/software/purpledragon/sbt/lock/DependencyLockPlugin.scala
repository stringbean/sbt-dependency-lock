package software.purpledragon.sbt.lock

import sbt.Keys._
import sbt._

object DependencyLockPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    val dependencyLockFile = settingKey[File]("lock file to generate")
    val dependencyLockWrite = taskKey[File]("write dependencies to lock file")
    val dependencyLockRead = taskKey[Option[DependencyLockFile]]("read dependencies from lock file")

    val dependencyLockCheck = taskKey[Boolean]("check if dependency lock is up to date")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    dependencyLockFile := baseDirectory.value / "build.sbt.lock",
    dependencyLockWrite := {
      val dest = dependencyLockFile.value
      val updateReport = update.value

      val lockFile = DependencyUtils.resolve(updateReport, thisProject.value.configurations.map(_.toConfigRef))
      DependencyLockIO.writeLockFile(lockFile, dest)
      dest
    },
    dependencyLockRead := {
      val src = dependencyLockFile.value
      val deps = DependencyLockIO.readLockFile(src)
      println(s"deps: $deps")

      deps
    },
    dependencyLockCheck := {
      val updateReport = update.value

      val currentFile = dependencyLockRead.value.getOrElse(sys.error("no lock file"))
      val updatedFile = DependencyUtils.resolve(updateReport, thisProject.value.configurations.map(_.toConfigRef))

      if (currentFile.dependencies ==updatedFile.dependencies) {
        println("all's good")
        true
      } else {
        println("not good")
        false
      }
    }
  )
}
