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
