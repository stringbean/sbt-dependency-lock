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

package software.purpledragon.sbt.lock.model

import software.purpledragon.sbt.lock.util.MessageUtil

import scala.collection.mutable

sealed trait LockFileStatus {
  def withConfigurationsChanged(addedConfigs: Seq[String], removedConfigs: Seq[String]): LockFileStatus
  def withDependencyChanges(
      added: Seq[ResolvedDependency],
      removed: Seq[ResolvedDependency],
      changed: Seq[ChangedDependency]): LockFileStatus

  def toShortReport: String
  def toLongReport: String
}

case object LockFileMatches extends LockFileStatus {
  override def withConfigurationsChanged(addedConfigs: Seq[String], removedConfigs: Seq[String]): LockFileStatus = {
    LockFileDiffers(addedConfigs, removedConfigs, Nil, Nil, Nil)
  }

  override def withDependencyChanges(
      added: Seq[ResolvedDependency],
      removed: Seq[ResolvedDependency],
      changed: Seq[ChangedDependency]): LockFileStatus = {
    LockFileDiffers(Nil, Nil, added, removed, changed)
  }

  override val toShortReport: String = MessageUtil.formatMessage("lock.status.success")
  override val toLongReport: String = toShortReport
}

case class LockFileDiffers(
    addedConfigs: Seq[String],
    removedConfigs: Seq[String],
    addedDependencies: Seq[ResolvedDependency],
    removedDependencies: Seq[ResolvedDependency],
    changedDependencies: Seq[ChangedDependency])
    extends LockFileStatus {

  override def withConfigurationsChanged(addedConfigs: Seq[String], removedConfigs: Seq[String]): LockFileStatus = {
    copy(addedConfigs = addedConfigs, removedConfigs = removedConfigs)
  }

  override def withDependencyChanges(
      added: Seq[ResolvedDependency],
      removed: Seq[ResolvedDependency],
      changed: Seq[ChangedDependency]): LockFileStatus = {

    copy(addedDependencies = added, removedDependencies = removed, changedDependencies = changed)
  }

  override def toShortReport: String = {
    val errors = mutable.Buffer[String]()

    if (addedConfigs.nonEmpty || removedConfigs.nonEmpty) {
      errors += MessageUtil.formatMessage(
        "lock.status.configs.info",
        MessageUtil.formatPlural("lock.status.configs", addedConfigs.size),
        MessageUtil.formatPlural("lock.status.configs", removedConfigs.size)
      )
    }

    if (addedDependencies.nonEmpty || removedDependencies.nonEmpty || changedDependencies.nonEmpty) {
      errors += MessageUtil.formatMessage(
        "lock.status.dependencies.info",
        MessageUtil.formatPlural("lock.status.dependencies", addedDependencies.size),
        MessageUtil.formatPlural("lock.status.dependencies", removedDependencies.size),
        MessageUtil.formatPlural("lock.status.dependencies", changedDependencies.size)
      )
    }

    MessageUtil.formatMessage("lock.status.failed.short", errors.mkString("\n"))
  }

  override def toLongReport: String = {
    val errors = mutable.Buffer[String]()

    if (addedConfigs.nonEmpty) {
      errors += MessageUtil.formatPlural(
        "lock.status.full.configs.added",
        addedConfigs.size,
        addedConfigs.mkString(", "))
    }

    if (removedConfigs.nonEmpty) {
      errors += MessageUtil.formatPlural(
        "lock.status.full.configs.removed",
        removedConfigs.size,
        removedConfigs.mkString(","))
    }

    def dumpDependencies(dependencies: Seq[ResolvedDependency]): String = {
      dependencies
        .map({ dep =>
          MessageUtil.formatMessage(
            "lock.status.full.dependency",
            dep.org,
            dep.name,
            dep.version,
            dep.configurations.toSeq.sorted.mkString(","))
        })
        .mkString("\n")
    }

    if (addedDependencies.nonEmpty) {
      errors += MessageUtil.formatPlural(
        "lock.status.full.dependencies.added",
        addedDependencies.size,
        dumpDependencies(addedDependencies))
    }

    if (removedDependencies.nonEmpty) {
      errors += MessageUtil.formatPlural(
        "lock.status.full.dependencies.removed",
        removedDependencies.size,
        dumpDependencies(removedDependencies))
    }

    if (changedDependencies.nonEmpty) {
      val changeDetails = changedDependencies map { change =>
        val key = (change.versionChanged, change.configurationsChanged) match {
          case (true, true) => "lock.status.full.dependency.changed.all"
          case (true, false) => "lock.status.full.dependency.changed.version"
          case (false, true) => "lock.status.full.dependency.changed.configs"
          case _ => "error"
        }

        MessageUtil.formatMessage(
          key,
          change.org,
          change.name,
          change.oldVersion,
          change.newVersion,
          change.oldConfigurations.mkString(","),
          change.newConfigurations.mkString(","))
      }

      errors += MessageUtil.formatPlural(
        "lock.status.full.dependencies.changed",
        changedDependencies.size,
        changeDetails.mkString("\n"))
    }

    MessageUtil.formatMessage("lock.status.failed.long", errors.mkString("\n"))
  }
}
