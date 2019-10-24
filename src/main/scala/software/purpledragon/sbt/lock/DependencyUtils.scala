package software.purpledragon.sbt.lock

import java.time.Instant

import sbt._

object DependencyUtils {
  def resolve(updateReport: UpdateReport, configs: Seq[ConfigRef]): DependencyLockFile = {
    val configurations = updateReport.configurations.filter(config => configs.contains(config.configuration))

    val configModules = configurations map { conf =>
      conf.configuration.name -> (conf.modules map { mr =>
        val arts = mr.artifacts.map(toResolvedArtifact)
        ResolvedDependency(mr.module.organization, mr.module.name, mr.module.revision, arts)
      })
    }

    DependencyLockFile(1, Instant.now(), configModules.toMap)
  }

  private def toResolvedArtifact(artifact: (Artifact, File)): ResolvedArtifact = {
    val checksum = Hash.toHex(Hash(artifact._2))
    ResolvedArtifact(artifact._1.name, s"sha1:$checksum")
  }
}
