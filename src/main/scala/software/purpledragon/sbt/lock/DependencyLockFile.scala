package software.purpledragon.sbt.lock

import java.time.Instant

case class DependencyLockFile(lockVersion: Int, timestamp: Instant, dependencies: Map[String, Seq[ResolvedDependency]])
