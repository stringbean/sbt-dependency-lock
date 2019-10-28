/*
 * Copyright 2019 Michael Stringer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.purpledragon.sbt.lock

import sbt.Keys._
import sbt._
import software.purpledragon.sbt.lock.model.DependencyLockFile

object DependencyLockPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    val dependencyLockFile = settingKey[File]("lock file to generate")
    val dependencyLockWrite = taskKey[File]("write dependencies to lock file")
    val dependencyLockRead = taskKey[Option[DependencyLockFile]]("read dependencies from lock file")

    val dependencyLockCheck = taskKey[Unit]("check if dependency lock is up to date")
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
      deps
    },
    dependencyLockCheck := {
      val logger = streams.value.log
      val updateReport = update.value

      val currentFile = dependencyLockRead.value.getOrElse(sys.error("no lock file"))
      val updatedFile = DependencyUtils.resolve(updateReport, thisProject.value.configurations.map(_.toConfigRef))

      if (currentFile == updatedFile) {
        logger.info("Dependency lock check passed")
      } else {
        // TODO output info?
        sys.error("Dependency lock check failed")
      }
    }
  )
}
