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

package software.purpledragon.sbt.lock.util

import java.text.{ChoiceFormat, MessageFormat}
import java.util.ResourceBundle

import scala.math.ScalaNumber

object MessageUtil {
  val messages: ResourceBundle = ResourceBundle.getBundle("messages")

  def format(template: String, args: Any*): String = {
    MessageFormat.format(template, args.map(unwrapArg): _*)
  }

  def formatMessage(key: String, args: Any*): String = {
    format(messages.getString(key), args.map(unwrapArg): _*)
  }

  def formatPlural(baseKey: String, count: Int, args: Any*): String = {
    val formatStrings = Array(
      messages.getString(s"$baseKey.none"),
      messages.getString(s"$baseKey.singular"),
      messages.getString(s"$baseKey.multiple")
    )

    val choice = new ChoiceFormat(Array(0, 1, 2), formatStrings)
    format(choice.format(count), count +: args: _*)
  }

  private def unwrapArg(arg: Any): AnyRef = arg match {
    case x: ScalaNumber => x.underlying
    case x => x.asInstanceOf[AnyRef]
  }
}
