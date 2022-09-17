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

import sbt.*
import sbt.Keys.*
import sbt.internal.util.ManagedLogger
import sbt.librarymanagement.{ConfigurationFilter, DependencyFilter, ModuleFilter}
import software.purpledragon.sbt.lock.DependencyLockUpdateMode.*
import software.purpledragon.sbt.lock.model.{DependencyLockFile, LockFileMatches}
import software.purpledragon.sbt.lock.util.MessageUtil

object DependencyLockPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    val dependencyLockFile = settingKey[File]("lockfile to generate")
    val dependencyLockWrite = taskKey[File]("write dependencies to lockfile")
    val dependencyLockRead = taskKey[Option[DependencyLockFile]]("read dependencies from lockfile")
    val dependencyLockModuleFilter = settingKey[ModuleFilter]("exclusion filter for dependencies")
    val dependencyLockConfigurationFilter = settingKey[ConfigurationFilter]("exclusion filter for configurations")

    val dependencyLockCheck = taskKey[Unit]("check if dependency lock is up to date")

    val DependencyLockUpdateMode: software.purpledragon.sbt.lock.DependencyLockUpdateMode.type =
      software.purpledragon.sbt.lock.DependencyLockUpdateMode
    val dependencyLockAutoCheck = settingKey[DependencyLockUpdateMode]("automatically check lockfile after update")
  }

  import autoImport.*

  // task names to skip auto-check if we're inside of
  private val PluginTasks = Seq("dependencyLockWrite", "dependencyLockCheck", "dependencyLockRead")

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    dependencyLockFile := baseDirectory.value / "build.sbt.lock",
    dependencyLockWrite := {
      val dest = dependencyLockFile.value
      val updateReport = update.value
      val exclusionFilter = dependencyLockModuleFilter.value
      val configFilter = dependencyLockConfigurationFilter.value
      val configurations = thisProject.value.configurations.filterNot(c => configFilter(c)).map(_.toConfigRef)

      val lockFile =
        DependencyUtils.resolve(updateReport, exclusionFilter, thisProject.value.configurations.map(_.toConfigRef))

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
      val exclusionFilter = dependencyLockModuleFilter.value
      val configFilter = dependencyLockConfigurationFilter.value
      val configurations = thisProject.value.configurations.filterNot(c => configFilter(c)).map(_.toConfigRef)

      val currentFile = dependencyLockRead.value.getOrElse(sys.error(MessageUtil.formatMessage("lock.status.missing")))
      val updatedFile =
        DependencyUtils.resolve(updateReport, exclusionFilter, thisProject.value.configurations.map(_.toConfigRef))

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
      val exclusionFilter = dependencyLockModuleFilter.value
      val configFilter = dependencyLockConfigurationFilter.value
      val configurations = thisProject.value.configurations.filterNot(c => configFilter(c)).map(_.toConfigRef)

      if (checkMode != DependencyLockUpdateMode.CheckDisabled && !skipCheck) {
        logger.debug("Automatically checking lockfile")

        dependencyLockRead.value match {
          case Some(currentFile) =>
            val updatedFile =
              DependencyUtils.resolve(report, exclusionFilter, thisProject.value.configurations.map(_.toConfigRef))

            val changes = currentFile.findChanges(updatedFile)

            (changes, checkMode) match {
              case (LockFileMatches, _) =>
              // check passed
              case (_, WarnOnError) =>
                logger.warn(MessageUtil.formatMessage("update.status.warning"))
              case (_, FailOnError) =>
                logger.error(MessageUtil.formatMessage("update.status.error"))
                sys.error(changes.toLongReport)
              case (_, AutoUpdate) =>
                logger.warn(MessageUtil.formatMessage("update.status.auto"))

                // rewrite the lockfile
                val dest = dependencyLockFile.value
                DependencyLockIO.writeLockFile(updatedFile, dest)

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
    }.value,
  )

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    dependencyLockAutoCheck := DependencyLockUpdateMode.WarnOnError,
    dependencyLockModuleFilter := DependencyFilter.fnToModuleFilter(_ => false),
    dependencyLockConfigurationFilter := DependencyFilter.fnToConfigurationFilter(_ => false),
  )
}
