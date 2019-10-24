package software.purpledragon.sbt.lock

case class ResolvedDependency(org: String, name: String, version: String, artifacts: Seq[ResolvedArtifact])

case class ResolvedArtifact(name: String, hash: String)