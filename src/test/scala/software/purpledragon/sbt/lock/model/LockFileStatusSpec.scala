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

import org.scalatest.{FlatSpec, Matchers}

class LockFileStatusSpec extends FlatSpec with Matchers {
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

  "LockFileDiffers.toLongReport" should "" in pending

  private def testDependency(
      org: String = "com.example",
      name: String = "artifact",
      version: String = "1.0"): ResolvedDependency = {
    ResolvedDependency(org, name, version, Nil, Set.empty)
  }

  private def testChangedDependency(
      org: String = "com.example",
      name: String = "artifact",
      oldVersion: String = "1.0",
      newVersion: String = "1."): ChangedDependency = {

    ChangedDependency(org, name, oldVersion, newVersion, Nil, Nil, Set.empty, Set.empty)
  }
}
