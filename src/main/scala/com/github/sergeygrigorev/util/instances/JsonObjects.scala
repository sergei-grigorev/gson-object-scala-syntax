/*
 * Copyright 2017 Sergey Grigorev
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

package com.github.sergeygrigorev.util.instances

import com.github.sergeygrigorev.util.data.{ ElementDecoder, FieldDecoder }
import com.google.gson.{ JsonArray, JsonElement, JsonObject, JsonPrimitive }

import scala.language.higherKinds

/**
 * More specific decoders for [[FieldDecoder]].
 */
trait JsonObjects {
  import ElementDecoder.primitive

  implicit val jsonObjectDecoder: ElementDecoder[JsonObject] =
    primitive[JsonObject] {
      case j: JsonObject => j
      case json => throw new IllegalArgumentException(s"couldn't be decoded as JsonObject ($json)")
    }

  implicit val jsonElementDecoder: ElementDecoder[JsonElement] =
    primitive[JsonElement] { j: JsonElement => j }

  implicit val jsonArrayDecoder: ElementDecoder[JsonArray] =
    primitive[JsonArray] {
      case j: JsonArray => j
      case json => throw new IllegalArgumentException(s"couldn't be decoded as JsonArray ($json)")
    }

  implicit val jsonPrimitiveDecoder: ElementDecoder[JsonPrimitive] =
    primitive[JsonPrimitive] {
      case j: JsonPrimitive => j
      case json => throw new IllegalArgumentException(s"couldn't be decoded as JsonPrimitive ($json)")
    }
}
