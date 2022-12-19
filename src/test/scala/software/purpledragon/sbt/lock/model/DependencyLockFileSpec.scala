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

import java.time.Instant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.SortedSet

class DependencyLockFileSpec extends AnyFlatSpec with Matchers {
  private val EmptyLockFile = DependencyLockFile(1, Instant.now(), Nil, Nil)
  private val TestLockFile = DependencyLockFile(
    1,
    Instant.now(),
    Seq("test-1", "test-2"),
    Seq(
      ResolvedDependency(
        "com.example",
        "package-1",
        "1.0.0",
        SortedSet(ResolvedArtifact("package-1.jar", "hash-1")),
        SortedSet("test-1")),
      ResolvedDependency("com.example", "package-2", "1.2.0", SortedSet.empty, SortedSet("test-2")),
    ),
  )

  "findChanges" should "return LockFileMatches for identical lockfiles" in {
    val left = TestLockFile
    val right = left.copy()

    left.findChanges(right) shouldBe LockFileMatches
  }

  it should "return LockFileMatches if timestamp differs" in {
    val left = DependencyLockFile(1, Instant.now(), Nil, Nil)
    val right = left.copy(timestamp = Instant.now())

    left.findChanges(right) shouldBe LockFileMatches
  }

  it should "error if lock version differs" in {
    val left = EmptyLockFile
    val right = left.copy(lockVersion = 2)

    a[RuntimeException] shouldBe thrownBy {
      left.findChanges(right)
    }
  }

  it should "return LockFileDiffers if configuration added" in {
    val left = TestLockFile
    val right = left.copy(configurations = left.configurations :+ "new-config")

    left.findChanges(right) shouldBe LockFileDiffers(Seq("new-config"), Nil, Nil, Nil, Nil)
  }

  it should "return LockFileDiffers if configuration removed" in {
    val left = TestLockFile
    val right = left.copy(configurations = Seq("test-1"))

    left.findChanges(right) shouldBe LockFileDiffers(Nil, Seq("test-2"), Nil, Nil, Nil)
  }

  it should "return LockFileDiffers if dependency added" in {
    val newDependency = ResolvedDependency(
      "com.example",
      "package-3",
      "3.0",
      SortedSet(ResolvedArtifact("package-3.jar", "hash-3")),
      SortedSet("test-1"))

    val left = TestLockFile
    val right = left.copy(dependencies = left.dependencies :+ newDependency)

    left.findChanges(right) shouldBe LockFileDiffers(Nil, Nil, Seq(newDependency), Nil, Nil)
  }

  it should "return LockFileDiffers if dependency removed" in {
    val left = TestLockFile
    val right = left.copy(dependencies = left.dependencies.tail)

    left.findChanges(right) shouldBe LockFileDiffers(Nil, Nil, Nil, Seq(left.dependencies.head), Nil)
  }

  it should "return LockFileDiffers if dependency changed" in {
    val left = TestLockFile
    val right = left.copy(
      dependencies = left.dependencies.tail :+ ResolvedDependency(
        "com.example",
        "package-1",
        "2.0.0",
        SortedSet(ResolvedArtifact("package-1.jar", "hash-1a")),
        SortedSet("test-1", "test-2")),
    )

    left.findChanges(right) shouldBe LockFileDiffers(
      Nil,
      Nil,
      Nil,
      Nil,
      Seq(
        ChangedDependency(
          "com.example",
          "package-1",
          "1.0.0",
          "2.0.0",
          SortedSet(ResolvedArtifact("package-1.jar", "hash-1")),
          SortedSet(ResolvedArtifact("package-1.jar", "hash-1a")),
          SortedSet("test-1"),
          SortedSet("test-1", "test-2"),
        )),
    )
  }
}
