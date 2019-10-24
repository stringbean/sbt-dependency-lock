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

import java.time.Instant

import sbt._

import scala.collection.{immutable, mutable}

object DependencyUtils {
  def resolve(updateReport: UpdateReport, configs: Seq[ConfigRef]): DependencyLockFile = {
    val configurations = updateReport.configurations.filter(config => configs.contains(config.configuration))

    val checksumCache = mutable.Map.empty[File, String]

    val configModules = configurations map { conf =>
      conf.configuration.name -> conf.modules.map(toResolvedDependency(_, checksumCache)).sorted
    }

    DependencyLockFile(1, Instant.now(), configModules.toMap)
  }

  private def toResolvedDependency(
      module: ModuleReport,
      checksumCache: mutable.Map[File, String]): ResolvedDependency = {

    val artifacts: immutable.Seq[ResolvedArtifact] = module.artifacts map {
      case (artifact, file) =>
        val hash = checksumCache.getOrElseUpdate(file, hashFile(file))
        ResolvedArtifact(artifact.name, hash)
    }

    ResolvedDependency(module.module.organization, module.module.name, module.module.revision, artifacts)
  }

  private def hashFile(file: File): String = s"sha1:${Hash.toHex(Hash(file))}"
}
