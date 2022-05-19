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
import sbt.internal.util.ManagedLogger
import software.purpledragon.sbt.lock.DependencyLockUpdateMode._
import software.purpledragon.sbt.lock.model.{DependencyLockFile, LockFileMatches}
import software.purpledragon.sbt.lock.util.MessageUtil

object DependencyLockPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    val dependencyLockFile = settingKey[File]("lockfile to generate")
    val dependencyLockWrite = taskKey[File]("write dependencies to lockfile")
    val dependencyLockRead = taskKey[Option[DependencyLockFile]]("read dependencies from lockfile")

    val dependencyLockCheck = taskKey[Unit]("check if dependency lock is up to date")

    val DependencyLockUpdateMode: software.purpledragon.sbt.lock.DependencyLockUpdateMode.type =
      software.purpledragon.sbt.lock.DependencyLockUpdateMode
    val dependencyLockAutoCheck = settingKey[DependencyLockUpdateMode]("automatically check lockfile after update")
  }

  import autoImport._

  // task names to skip auto-check if we're inside of
  private val PluginTasks = Seq("dependencyLockWrite", "dependencyLockCheck", "dependencyLockRead")

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    dependencyLockFile := baseDirectory.value / "build.sbt.lock",
    dependencyLockAutoCheck := DependencyLockUpdateMode.WarnOnError,
    dependencyLockWrite := {
      val dest = dependencyLockFile.value
      val updateReport = update.value

      val lockFile = DependencyUtils.resolve(updateReport, thisProject.value.configurations.map(_.toConfigRef))

      val updateStatus = DependencyLockIO
        .readLockFile(dest)
        .map(_.findChanges(lockFile))

      if (!updateStatus.contains(LockFileMatches)) {
        DependencyLockIO.writeLockFile(lockFile, dest)
      }
      dest
    },
    dependencyLockRead := {
      val src = dependencyLockFile.value
      val deps = DependencyLockIO.readLockFile(src)
      deps
    },
    dependencyLockCheck := {
      val logger: ManagedLogger = streams.value.log
      val updateReport: UpdateReport = update.value

      val currentFile = dependencyLockRead.value.getOrElse(sys.error(MessageUtil.formatMessage("lock.status.missing")))
      val updatedFile = DependencyUtils.resolve(updateReport, thisProject.value.configurations.map(_.toConfigRef))

      val changes = currentFile.findChanges(updatedFile)

      if (changes == LockFileMatches) {
        logger.info(changes.toShortReport)
      } else {
        logger.warn(changes.toShortReport)
        sys.error(changes.toLongReport)
      }
    },
    update := Def.taskDyn {
      val report = update.value
      val logger = streams.value.log

      // check to see if the current command/task is one of our internal ones
      val skipCheck = state.value.currentCommand.map(_.commandLine).exists(PluginTasks.contains)
      val checkMode = dependencyLockAutoCheck.value

      if (checkMode != DependencyLockUpdateMode.CheckDisabled && !skipCheck) {
        logger.debug("Automatically checking lockfile")

        dependencyLockRead.value match {
          case Some(currentFile) =>
            val updatedFile = DependencyUtils.resolve(report, thisProject.value.configurations.map(_.toConfigRef))

            val changes = currentFile.findChanges(updatedFile)

            (changes, checkMode) match {
              case (LockFileMatches, _) =>
              // check passed
              case (_, WarnOnError) =>
                logger.warn(MessageUtil.formatMessage("update.status.warning"))
              case (_, FailOnError) =>
                logger.error(MessageUtil.formatMessage("update.status.error"))
                sys.error(changes.toLongReport)

              case _ =>
              // scenario shouldn't happen - failed check, but we're not checking...
            }

          case None =>
            logger.warn("no lockfile found - please run dependencyLockWrite")
        }
      }

      // return the original report
      Def.task {
        report
      }
    }.value
  )
}
