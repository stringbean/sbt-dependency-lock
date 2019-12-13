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

final case class ResolvedDependency(
    org: String,
    name: String,
    version: String,
    artifacts: Seq[ResolvedArtifact],
    configurations: SortedSet[String])
    extends Ordered[ResolvedDependency] {

  override def compare(that: ResolvedDependency): Int = {
    (org, name, version) compare (that.org, that.name, that.version)
  }

  def withConfiguration(conf: String): ResolvedDependency = {
    copy(configurations = configurations + conf)
  }
}
