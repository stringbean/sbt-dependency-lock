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

import java.io.File

import io.circe.Decoder.Result
import io.circe.parser._
import io.circe.syntax._
import sbt.io.IO
import software.purpledragon.sbt.lock.Decoders._

object DependencyLockIO {
  def writeLockFile(lockFile: DependencyLockFile, dest: File): Unit = {
    IO.write(dest, lockFile.asJson.spaces2)
  }

  def readLockFile(src: File): Option[DependencyLockFile] = {

    if (src.exists()) {
      parse(IO.read(src)) match {
        case Right(json) =>
          json.as[DependencyLockFile].toOption
        case _ =>
          None
      }

      //     Some(parse(IO.read(src)))
    } else {
      None
    }
  }
}
