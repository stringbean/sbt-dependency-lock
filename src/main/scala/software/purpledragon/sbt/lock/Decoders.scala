package software.purpledragon.sbt.lock

import io.circe._
import io.circe.generic.semiauto._

object Decoders {
  implicit val lockfileDecoder: Decoder[DependencyLockFile] = deriveDecoder[DependencyLockFile]
  implicit val lockfileEncoder: Encoder[DependencyLockFile] = deriveEncoder[DependencyLockFile]

  implicit val resolvedDependencyDecoder: Decoder[ResolvedDependency] = deriveDecoder[ResolvedDependency]
  implicit val resolvedDependencyEncoder: Encoder[ResolvedDependency] = deriveEncoder[ResolvedDependency]

  implicit val resolvedArtifactDecoder: Decoder[ResolvedArtifact] = deriveDecoder[ResolvedArtifact]
  implicit val resolvedArtifactEncoder: Encoder[ResolvedArtifact] = deriveEncoder[ResolvedArtifact]
}
