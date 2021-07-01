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

package software.purpledragon.sbt.lock

import org.scalatest.OptionValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import software.purpledragon.sbt.lock.model.lockfile.v1.{DependencyLockFile, ResolvedArtifact, ResolvedDependency}

import java.time.Instant
import scala.collection.SortedSet
import scala.io.Source

class DependencyLockIOSpec extends AnyFlatSpec with Matchers {
  "parseLockFile" should "return None if not valid JSON" in {
    DependencyLockIO.parseLockFile("jibberish")
  }

  it should "return None if version is unknown" in {
    parseFromResource("unknown-version.json") shouldBe None
  }

  "parseLockFile (v1)" should "parse empty dependencies" in {
    val parsed = parseFromResource("v1/empty-dependencies.json")

    parsed.value.lockVersion shouldBe 1
    parsed.value.timestamp shouldBe Instant.parse("2021-05-11T12:00:00.000Z")
    parsed.value.configurations shouldBe Seq("compile", "test")
    parsed.value.dependencies shouldBe empty
  }

  it should "parse lockfile with dependencies" in {
    val parsed = parseFromResource("v1/with-dependencies.json")

    parsed.value.lockVersion shouldBe 1
    parsed.value.timestamp shouldBe Instant.parse("2021-05-11T12:00:00.000Z")
    parsed.value.configurations shouldBe Seq("compile", "test")
    parsed.value.dependencies shouldBe Seq(
      ResolvedDependency(
        "org.scala-lang",
        "scala-library",
        "2.12.10",
        SortedSet(
          ResolvedArtifact(
            "scala-library.jar",
            "sha1:3509860bc2e5b3da001ed45aca94ffbe5694dbda"
          )
        ),
        SortedSet("compile", "test")
      ),
      ResolvedDependency(
        "org.scala-lang",
        "scala-reflect",
        "2.12.10",
        SortedSet(
          ResolvedArtifact(
            "scala-reflect.jar",
            "sha1:14cb7beb516cd8e07716133668c427792122c926"
          )
        ),
        SortedSet("test")
      )
    )
  }

  private def parseFromResource(name: String): Option[DependencyLockFile] = {
    val contents = Source.fromResource(s"lockfile/$name").getLines().mkString("\n")
    DependencyLockIO.parseLockFile(contents)
  }
}
