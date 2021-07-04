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

package software.purpledragon.sbt.lock.io

import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Json}
import sbt.io.IO
import software.purpledragon.sbt.lock.model.lockfile.v1.Decoders._
import software.purpledragon.sbt.lock.model.lockfile.v1.DependencyLockFile

import java.io.File
import scala.util.{Failure, Success, Try}

object DependencyLockIO {
  def writeLockFile(lockFile: DependencyLockFile, dest: File): Unit = {
    IO.write(dest, lockFile.asJson.spaces2)
  }

  def readLockFile(src: File): Try[DependencyLockFile] = {
    if (src.exists()) {
      parseLockFile(IO.read(src))
    } else {
      Failure(new MissingLockfileException())
    }
  }

  def parseLockFile(contents: String): Try[DependencyLockFile] = {
    parse(contents) match {
      case Right(json) =>
        json.hcursor.get[Int]("lockVersion") match {
          case Right(1) =>
            decodeJson[DependencyLockFile](json)

          case Right(version) =>
            Failure(new InvalidLockfileVersionException(version))

          case _ =>
            // missing lockVersion field - invalid lockfile
            Failure(new InvalidFormatException())
        }

      case _ =>
        // invalid json
        Failure(new InvalidFormatException())
    }
  }

  private def decodeJson[T](json: Json)(implicit d: Decoder[T]): Try[T] = {
    json.as[T] match {
      case Right(parsed) => Success(parsed)
      case Left(_) => Failure(new InvalidFormatException())
    }
  }
}
