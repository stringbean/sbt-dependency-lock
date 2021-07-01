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

import scala.collection.SortedSet
import scala.math.Ordered.orderingToOrdered

final case class ChangedDependency(
    org: String,
    name: String,
    oldVersion: String,
    newVersion: String,
    oldConfigurations: SortedSet[String],
    newConfigurations: SortedSet[String],
    addedArtifacts: SortedSet[ResolvedArtifact],
    removedArtifacts: SortedSet[ResolvedArtifact],
    changedArtifacts: SortedSet[ChangedArtifact]) {

  def versionChanged: Boolean = oldVersion != newVersion
  def configurationsChanged: Boolean = oldConfigurations != newConfigurations
  def artifactsChanged: Boolean = addedArtifacts.nonEmpty || removedArtifacts.nonEmpty || changedArtifacts.nonEmpty
}

object ChangedDependency {
  def apply(
      org: String,
      name: String,
      oldVersion: String,
      newVersion: String,
      oldConfigurations: SortedSet[String],
      newConfigurations: SortedSet[String],
      oldArtifacts: SortedSet[ResolvedArtifact],
      newArtifacts: SortedSet[ResolvedArtifact]): ChangedDependency = {

    val oldArtifactsByName = oldArtifacts.map(a => a.name -> a).toMap
    val newArtifactsByName = newArtifacts.map(a => a.name -> a).toMap

    val addedArtifacts = (newArtifactsByName.keySet -- oldArtifactsByName.keySet).map(newArtifactsByName.apply)
    val removedArtifacts = (oldArtifactsByName.keySet -- newArtifactsByName.keySet).map(oldArtifactsByName.apply)

    val changedArtifacts =
      oldArtifactsByName.keySet.intersect(newArtifactsByName.keySet).foldLeft(Seq.empty[ChangedArtifact]) {
        (changes, name) =>
          val oldHash = oldArtifactsByName(name).hash
          val newHash = newArtifactsByName(name).hash

          if (oldHash != newHash) {
            changes :+ ChangedArtifact(name, oldHash, newHash)
          } else {
            changes
          }
      }

    ChangedDependency(
      org,
      name,
      oldVersion,
      newVersion,
      oldConfigurations,
      newConfigurations,
      addedArtifacts.to[SortedSet],
      removedArtifacts.to[SortedSet],
      changedArtifacts.to[SortedSet]
    )
  }
}

final case class ChangedArtifact(name: String, oldHash: String, newHash: String) extends Ordered[ChangedArtifact] {
  override def compare(that: ChangedArtifact): Int = {
    (name, oldHash, newHash) compare (that.name, that.oldHash, that.newHash)
  }
}
