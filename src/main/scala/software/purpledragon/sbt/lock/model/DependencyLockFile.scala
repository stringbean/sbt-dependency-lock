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

import java.time.Instant
import java.util.Objects

final case class DependencyLockFile(
    lockVersion: Int,
    timestamp: Instant,
    configurations: Seq[String],
    dependencies: Seq[ResolvedDependency]) {

  override def hashCode(): Int = Objects.hash(int2Integer(lockVersion), configurations, dependencies)

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: DependencyLockFile =>
        lockVersion == other.lockVersion && configurations == other.configurations && dependencies == other.dependencies
      case _ =>
        false
    }
  }

  def findChanges(other: DependencyLockFile): LockFileStatus = {
    LockFileChecks.foldLeft(LockFileMatches: LockFileStatus) { (acc, check) =>
      check(acc, other)
    }
  }

  private type LockFileCheck = (LockFileStatus, DependencyLockFile) => LockFileStatus
  private def LockFileChecks = Seq(checkLockVersion, checkConfigurations, checkDependencies)

  private val checkLockVersion: LockFileCheck = { (acc, other) =>
    if (lockVersion != other.lockVersion) {
      sys.error("Incorrect lockfile version")
    }

    acc
  }

  private val checkConfigurations: LockFileCheck = { (acc, other) =>
    if (configurations == other.configurations) {
      acc
    } else {
      acc.withConfigurationsChanged(configurations, other.configurations)
    }
  }

  private val checkDependencies: LockFileCheck = { (acc, other) =>
    val ourDeps = dependenciesByRef(dependencies)
    val otherDeps = dependenciesByRef(other.dependencies)

    val added: Set[DependencyRef] = otherDeps.keySet -- ourDeps.keySet
    val removed: Set[DependencyRef] = ourDeps.keySet -- otherDeps.keySet

    val changed = ourDeps.keySet.intersect(otherDeps.keySet).foldLeft(Seq.empty[ChangedDependency]) {
      (changes, depref) =>
        val ourDep = ourDeps(depref)
        val otherDep = otherDeps(depref)

        if (ourDep != otherDep) {
          changes :+ ChangedDependency(
            depref.org,
            depref.name,
            ourDep.version,
            otherDep.version,
            ourDep.artifacts,
            otherDep.artifacts,
            ourDep.configurations,
            otherDep.configurations)
        } else {
          changes
        }
    }

    if (added.nonEmpty || removed.nonEmpty || changed.nonEmpty) {
      acc.withDependencyChanges(
        otherDeps.filterKeys(added.contains).values.toSeq,
        ourDeps.filterKeys(removed.contains).values.toSeq,
        changed)
    } else {
      acc
    }
  }

  private def dependenciesByRef(deps: Seq[ResolvedDependency]): Map[DependencyRef, ResolvedDependency] = {
    deps.map(dep => DependencyRef(dep.org, dep.name, None) -> dep).toMap
  }
}
