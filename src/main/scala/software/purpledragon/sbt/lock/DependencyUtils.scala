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

import sbt._
import sbt.librarymanagement.ModuleFilter
import software.purpledragon.sbt.lock.model.{DependencyLockFile, DependencyRef, ResolvedArtifact, ResolvedDependency}

import java.time.Instant
import scala.collection.{SortedSet, immutable, mutable}

object DependencyUtils {
  def resolve(updateReport: UpdateReport, exclusion: ModuleFilter, configs: Seq[ConfigRef]): DependencyLockFile = {
    val configurations: immutable.Seq[ConfigurationReport] =
      updateReport.configurations.filter(config => configs.contains(config.configuration))

    val checksumCache = mutable.Map.empty[File, String]

    val resolvedDependencies =
      configurations.foldLeft(Map.empty[DependencyRef, ResolvedDependency]) { (acc, conf) =>
        val configName = conf.configuration.name

        val filteredModules = conf.modules.filterNot { moduleReport =>
          exclusion(moduleReport.module)
        }

        filteredModules.foldLeft(acc) { (acc2, module) =>
          resolveModuleForConfig(acc2, configName, module, checksumCache)
        }
      }

    DependencyLockFile(
      1,
      Instant.now(),
      configurations.map(_.configuration.name).sorted,
      resolvedDependencies.values.toSeq.sorted)
  }

  private def resolveModuleForConfig(
      acc: Map[DependencyRef, ResolvedDependency],
      configuration: String,
      module: ModuleReport,
      checksumCache: mutable.Map[File, String]): Map[DependencyRef, ResolvedDependency] = {

    val ref = DependencyRef(module.module)
    val dep = acc
      .getOrElse(ref, generateResolvedDependency(module, checksumCache))
      .withConfiguration(configuration)

    acc.updated(ref, dep)
  }

  private def generateResolvedDependency(
      module: ModuleReport,
      checksumCache: mutable.Map[File, String]): ResolvedDependency = {

    val artifacts = module.artifacts.map(ResolvedArtifact.apply(_, checksumCache))

    ResolvedDependency(
      module.module.organization,
      module.module.name,
      module.module.revision,
      artifacts.to[SortedSet],
      SortedSet.empty)
  }
}
