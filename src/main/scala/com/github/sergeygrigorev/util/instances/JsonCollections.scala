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

import com.github.sergeygrigorev.util.data.ElementDecoder
import com.google.gson.{ JsonArray, JsonObject }

import scala.collection.JavaConverters._

/**
 * Scala collections.
 */
trait JsonCollections {
  import ElementDecoder.primitive

  type StringMap[A] = Map[String, A]

  implicit def jsonListDecoder[T: ElementDecoder]: ElementDecoder[List[T]] =
    primitive[List[T]] {
      case j: JsonArray => j.asScala.view.map(ElementDecoder[T].decode(_)).toList
      case field => throw new IllegalArgumentException(s"element is not a collection ($field)")
    }

  implicit def hashMapDecoder[T: ElementDecoder]: ElementDecoder[StringMap[T]] =
    primitive[StringMap[T]] {
      case j: JsonObject => j.entrySet().asScala.view.map(e => (e.getKey, ElementDecoder[T].decode(e.getValue))).toMap
      case field => throw new IllegalArgumentException(s"element couldn't be decodes as a map ($field)")
    }
}
