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

import sbt.Hash

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}
import java.security.{DigestInputStream, MessageDigest}
import scala.util.{Failure, Success, Try}

object HashingUtils {

  def sha1(file: File): String = s"sha1:${Hash.toHex(Hash(file))}"

  def sha256(file: File): String =
    Try {
      sha256(new BufferedInputStream(new FileInputStream(file))) // sha256 closes stream
    } match {
      case Failure(_) => "" // seems to pretty much be the behaviour in the SHA-1 case
      case Success(hash) =>
        s"sha256:${Hash.toHex(hash)}"
    }

  def sha256(stream: InputStream): Array[Byte] = {
    val digest = MessageDigest.getInstance("SHA-256")
    try {
      val dis = new DigestInputStream(stream, digest)
      val buffer = new Array[Byte](8192)
      while (dis.read(buffer) >= 0) {}
      dis.close()
      digest.digest
    } finally {
      stream.close()
    }
  }

}
