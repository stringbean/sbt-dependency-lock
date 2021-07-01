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

import java.io.File

import sbt.Hash
import sbt.librarymanagement.Artifact

import scala.collection.mutable
import scala.math.Ordered.orderingToOrdered

final case class ResolvedArtifact(name: String, hash: String) extends Ordered[ResolvedArtifact] {
  override def compare(that: ResolvedArtifact): Int = {
    (name, hash) compare (that.name, that.hash)
  }
}

object ResolvedArtifact {
  def apply(art: (Artifact, File), checksumCache: mutable.Map[File, String]): ResolvedArtifact = {
    val (artifact, file) = art
    val hash = checksumCache.getOrElseUpdate(file, hashFile(file))

    val classifier = artifact.classifier.map(c => s"-$c").getOrElse("")
    val qualifier = artifact.`type` match {
      case "jar" | "bundle" => ""
      case q => s"-$q"
    }

    ResolvedArtifact(s"${artifact.name}$classifier$qualifier.${artifact.extension}", hash)
  }

  private def hashFile(file: File): String = s"sha1:${Hash.toHex(Hash(file))}"
}
