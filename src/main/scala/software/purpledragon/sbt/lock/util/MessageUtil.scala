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

import java.text.ChoiceFormat
import java.util.ResourceBundle

object MessageUtil {
  val messages: ResourceBundle = ResourceBundle.getBundle("messages")

  def format(key: String, args: AnyRef*): String = {
    messages.getString(key).format(args: _*)
  }

  def formatPlural(baseKey: String, count: Int): String = {
    val formatStrings = Array(
      messages.getString(s"$baseKey.none"),
      messages.getString(s"$baseKey.singular"),
      messages.getString(s"$baseKey.multiple")
    )

    val format = new ChoiceFormat(Array(0, 1, 2), formatStrings)
    format.format(count).format(count)
  }
}
