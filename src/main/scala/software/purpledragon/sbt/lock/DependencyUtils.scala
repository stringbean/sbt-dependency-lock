package software.purpledragon.sbt.lock

import java.time.Instant

import sbt._

import scala.collection.{immutable, mutable}

object DependencyUtils {
  def resolve(updateReport: UpdateReport, configs: Seq[ConfigRef]): DependencyLockFile = {
    val configurations = updateReport.configurations.filter(config => configs.contains(config.configuration))

    val checksumCache = mutable.Map[File, String]()

    val configModules = configurations map { conf =>
      conf.configuration.name -> conf.modules.map(toResolvedDependency(_, checksumCache))
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
