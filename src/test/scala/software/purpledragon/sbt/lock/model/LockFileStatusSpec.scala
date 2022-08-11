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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import software.purpledragon.sbt.lock.model.lockfile.v1
import software.purpledragon.sbt.lock.model.lockfile.v1.{ResolvedArtifact, ResolvedDependency}

import scala.collection.SortedSet

class LockFileStatusSpec extends AnyFlatSpec with Matchers {
  "LockFileMatches.toShortReport" should "output correct message" in {
    LockFileMatches.toShortReport shouldBe "Dependency lock check passed"
  }

  "LockFileMatches.toLongReport" should "output correct message" in {
    LockFileMatches.toLongReport shouldBe "Dependency lock check passed"
  }

  "LockFileDiffers.toShortReport" should "render 1 config added" in {
    val expected =
      """Dependency lock check failed:
        |  1 config added and no configs removed""".stripMargin

    LockFileMatches.withConfigurationsChanged(Seq("test"), Nil).toShortReport shouldBe expected
  }

  it should "render 2 configs added" in {
    val expected =
      """Dependency lock check failed:
        |  2 configs added and no configs removed""".stripMargin

    LockFileMatches.withConfigurationsChanged(Seq("test", "test2"), Nil).toShortReport shouldBe expected
  }

  it should "render 1 config removed" in {
    val expected =
      """Dependency lock check failed:
        |  no configs added and 1 config removed""".stripMargin

    LockFileMatches.withConfigurationsChanged(Nil, Seq("test")).toShortReport shouldBe expected
  }

  it should "render 1 config added and 2 configs removed" in {
    val expected =
      """Dependency lock check failed:
        |  1 config added and 2 configs removed""".stripMargin

    LockFileMatches.withConfigurationsChanged(Seq("test1"), Seq("test2", "test3")).toShortReport shouldBe expected
  }

  it should "render 1 dependency added" in {
    val expected =
      """Dependency lock check failed:
        |  1 dependency added, no dependencies removed and no dependencies changed""".stripMargin

    LockFileMatches.withDependencyChanges(Seq(testDependency()), Nil, Nil).toShortReport shouldBe expected
  }

  it should "render 2 dependencies removed" in {
    val expected =
      """Dependency lock check failed:
        |  no dependencies added, 2 dependencies removed and no dependencies changed""".stripMargin

    LockFileMatches
      .withDependencyChanges(Nil, Seq(testDependency(), testDependency(name = "artifact-2")), Nil)
      .toShortReport shouldBe expected
  }

  it should "render 1 dependency changed" in {
    val expected =
      """Dependency lock check failed:
        |  no dependencies added, no dependencies removed and 1 dependency changed""".stripMargin

    LockFileMatches.withDependencyChanges(Nil, Nil, Seq(testChangedDependency())).toShortReport shouldBe expected
  }

  it should "render 2 dependency artifacts changed" in {
    val expected =
      """Dependency lock check failed:
        |  2 dependency artifacts changed""".stripMargin

    LockFileMatches
      .withDependencyChanges(
        Nil,
        Nil,
        Seq(
          testChangedDependencyArtifacts(
            "dependency-1",
            "1.1",
            oldArtifacts = Seq(ResolvedArtifact("artifact-2.jar", "sha1:07c10d545325e3a6e72e06381afe469fd40eb701")),
            newArtifacts = Seq(ResolvedArtifact("artifact-1.jar", "sha1:2b8b815229aa8a61e483fb4ba0588b8b6c491890"))
          ),
          testChangedDependencyArtifacts(
            "dependency-2",
            "1.1.2",
            oldArtifacts = Seq(
              ResolvedArtifact("artifact-a.jar", "sha1:07c10d545325e3a6e72e06381afe469fd40eb701"),
              ResolvedArtifact("artifact-b.jar", "sha1:cfa4f316351a91bfd95cb0644c6a2c95f52db1fc")
            )
          )
        )
      )
      .toShortReport shouldBe expected
  }

  it should "render configs and dependencies changed" in {
    val expected =
      """Dependency lock check failed:
        |  1 config added and 2 configs removed
        |  1 dependency added, no dependencies removed and no dependencies changed""".stripMargin

    LockFileMatches
      .withConfigurationsChanged(Seq("test1"), Seq("test2", "test3"))
      .withDependencyChanges(Seq(testDependency()), Nil, Nil)
      .toShortReport shouldBe expected
  }

  "LockFileDiffers.toLongReport" should "render 1 config added" in {
    val expected =
      """Dependency lock check failed:
        |  1 config added: test""".stripMargin

    LockFileMatches.withConfigurationsChanged(Seq("test"), Nil).toLongReport shouldBe expected
  }

  it should "render 1 config removed" in {
    val expected =
      """Dependency lock check failed:
        |  1 config removed: test""".stripMargin

    LockFileMatches.withConfigurationsChanged(Nil, Seq("test")).toLongReport shouldBe expected
  }

  it should "render 1 config added and 2 configs removed" in {
    val expected =
      """Dependency lock check failed:
        |  1 config added: test1
        |  2 configs removed: test2,test3""".stripMargin

    LockFileMatches.withConfigurationsChanged(Seq("test1"), Seq("test2", "test3")).toLongReport shouldBe expected
  }

  it should "render 1 dependency added" in {
    val expected =
      """Dependency lock check failed:
        |  1 dependency added:
        |    com.example:artifact  (compile,test)  1.0""".stripMargin

    LockFileMatches.withDependencyChanges(Seq(testDependency()), Nil, Nil).toLongReport shouldBe expected
  }

  it should "render 2 dependencies removed" in {
    val expected =
      """Dependency lock check failed:
        |  2 dependencies removed:
        |    com.example:artifact    (compile,test)  1.0
        |    com.example:artifact-2  (compile,test)  1.0""".stripMargin

    LockFileMatches
      .withDependencyChanges(Nil, Seq(testDependency(), testDependency(name = "artifact-2")), Nil)
      .toLongReport shouldBe expected
  }

  it should "render 1 dependency changed version" in {
    val expected =
      """Dependency lock check failed:
        |  1 dependency changed:
        |    com.example:artifact  (compile,test)    1.0  -> 2.0""".stripMargin

    LockFileMatches.withDependencyChanges(Nil, Nil, Seq(testChangedDependency())).toLongReport shouldBe expected
  }

  it should "render 1 dependency changed configs" in {
    val expected =
      """Dependency lock check failed:
        |  1 dependency changed:
        |    com.example:artifact  (compile,test)  -> (compile)  1.0""".stripMargin

    LockFileMatches
      .withDependencyChanges(
        Nil,
        Nil,
        Seq(testChangedDependency(newVersion = "1.0", newConfigurations = SortedSet("compile"))))
      .toLongReport shouldBe expected
  }

  it should "render 1 dependency changed version and configs" in {
    val expected =
      """Dependency lock check failed:
        |  1 dependency changed:
        |    com.example:artifact  (compile,test)  -> (compile)  1.0  -> 2.0""".stripMargin

    LockFileMatches
      .withDependencyChanges(Nil, Nil, Seq(testChangedDependency(newConfigurations = SortedSet("compile"))))
      .toLongReport shouldBe expected
  }

  it should "render 2 dependency artifacts changed" in {
    val expected =
      """Dependency lock check failed:
        |  2 dependency artifacts changed""".stripMargin

    LockFileMatches
      .withDependencyChanges(
        Nil,
        Nil,
        Seq(
          testChangedDependencyArtifacts(
            "dependency-1",
            "1.1",
            oldArtifacts = Seq(ResolvedArtifact("artifact-2.jar", "sha1:07c10d545325e3a6e72e06381afe469fd40eb701")),
            newArtifacts = Seq(ResolvedArtifact("artifact-1.jar", "sha1:2b8b815229aa8a61e483fb4ba0588b8b6c491890"))
          ),
          testChangedDependencyArtifacts(
            "dependency-2",
            "1.1.2",
            oldArtifacts = Seq(
              ResolvedArtifact("artifact-a.jar", "sha1:07c10d545325e3a6e72e06381afe469fd40eb701"),
              ResolvedArtifact("artifact-b.jar", "sha1:cfa4f316351a91bfd95cb0644c6a2c95f52db1fc")
            )
          )
        )
      )
      .toLongReport shouldBe expected
  }

  it should "sort dependencies" in {
    val expected =
      """Dependency lock check failed:
        |  3 dependencies added:
        |    com.example:added-2  (compile,test)  1.0
        |    com.example:added-3  (compile,test)  1.0
        |    net.example:added-1  (compile,test)  1.0
        |  3 dependencies removed:
        |    com.example:removed-1  (compile,test)  1.0
        |    com.example:removed-2  (compile,test)  1.0
        |    com.example:removed-3  (compile,test)  1.0
        |  3 dependencies changed:
        |    com.example:changed-2  (compile,test)    1.0  -> 1.3
        |    com.example:changed-3  (compile,test)    1.0  -> 1.2
        |    net.example:changed-1  (compile,test)    1.0  -> 1.0.1""".stripMargin

    val actual = LockFileMatches
      .withDependencyChanges(
        Seq(
          testDependency(name = "added-3"),
          testDependency(name = "added-2"),
          testDependency(org = "net.example", name = "added-1")
        ),
        Seq(
          testDependency(name = "removed-1"),
          testDependency(name = "removed-3"),
          testDependency(name = "removed-2")
        ),
        Seq(
          testChangedDependency(name = "changed-3", newVersion = "1.2"),
          testChangedDependency(name = "changed-2", newVersion = "1.3"),
          testChangedDependency(org = "net.example", name = "changed-1", newVersion = "1.0.1")
        )
      )
      .toLongReport

    actual shouldBe expected
  }

  it should "render lots of changes" in {
    val expected =
      """Dependency lock check failed:
        |  1 config added: test1
        |  2 configs removed: test2,test3
        |  2 dependencies added:
        |    com.example:artifact1  (compile)  1.0
        |    com.example:artifact2  (test)     1.2
        |  1 dependency removed:
        |    com.example:artifact3  (runtime)  3.1.1
        |  3 dependencies changed:
        |    org.example:both     (compile)       -> (compile,test)  1.0  -> 2.0
        |    org.example:configs  (compile,test)  -> (compile)       1.0
        |    org.example:version  (compile)                          1.0  -> 2.0""".stripMargin

    val actual = LockFileMatches
      .withConfigurationsChanged(Seq("test1"), Seq("test2", "test3"))
      .withDependencyChanges(
        Seq(
          testDependency(name = "artifact1", configs = SortedSet("compile")),
          testDependency(name = "artifact2", version = "1.2", configs = SortedSet("test"))),
        Seq(testDependency(name = "artifact3", version = "3.1.1", configs = SortedSet("runtime"))),
        Seq(
          testChangedDependency(
            org = "org.example",
            name = "version",
            oldConfigurations = SortedSet("compile"),
            newConfigurations = SortedSet("compile")),
          testChangedDependency(
            org = "org.example",
            name = "configs",
            newVersion = "1.0",
            newConfigurations = SortedSet("compile")),
          testChangedDependency(org = "org.example", name = "both", oldConfigurations = SortedSet("compile"))
        )
      )
      .toLongReport

    actual shouldBe expected
  }

  private def testDependency(
      org: String = "com.example",
      name: String = "artifact",
      version: String = "1.0",
      configs: SortedSet[String] = SortedSet("compile", "test")): ResolvedDependency = {
    v1.ResolvedDependency(org, name, version, SortedSet.empty, configs)
  }

  private def testChangedDependency(
      org: String = "com.example",
      name: String = "artifact",
      oldVersion: String = "1.0",
      newVersion: String = "2.0",
      oldConfigurations: SortedSet[String] = SortedSet("compile", "test"),
      newConfigurations: SortedSet[String] = SortedSet("compile", "test")): ChangedDependency = {

    ChangedDependency(
      org,
      name,
      oldVersion,
      newVersion,
      SortedSet.empty,
      SortedSet.empty,
      oldConfigurations,
      newConfigurations)
  }

  private def testChangedDependencyArtifacts(
      name: String,
      version: String,
      org: String = "com.example",
      oldArtifacts: Seq[ResolvedArtifact] = Nil,
      newArtifacts: Seq[ResolvedArtifact] = Nil): ChangedDependency = {

    ChangedDependency(
      org,
      name,
      version,
      version,
      oldArtifacts.to[SortedSet],
      newArtifacts.to[SortedSet],
      SortedSet("compile"),
      SortedSet("compile")
    )
  }
}
