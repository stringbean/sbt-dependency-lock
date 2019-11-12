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

sealed trait LockFileStatus {
  def withConfigurationsChanged(addedConfigs: Seq[String], removedConfigs: Seq[String]): LockFileStatus
  def withDependencyChanges(
      added: Seq[ResolvedDependency],
      removed: Seq[ResolvedDependency],
      changed: Seq[ChangedDependency]): LockFileStatus
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
}
