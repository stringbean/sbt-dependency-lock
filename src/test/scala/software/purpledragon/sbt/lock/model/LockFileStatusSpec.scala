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
        |    org.example:version  (compile)                          1.0  -> 2.0
        |    org.example:configs  (compile,test)  -> (compile)       1.0
        |    org.example:both     (compile)       -> (compile,test)  1.0  -> 2.0""".stripMargin

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
    ResolvedDependency(org, name, version, SortedSet.empty, configs)
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
}
