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
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

/**
 * Scala collections.
 */
trait JsonCollections {
  import ElementDecoder.primitive

  implicit def sequenceDecoder[E: ElementDecoder, S[_]](implicit builder: CanBuildFrom[Nothing, E, S[E]]): ElementDecoder[S[E]] =
    primitive[S[E]] {
      case j: JsonArray =>
        val res = builder()
        val decoder = ElementDecoder[E]
        for (v <- j.asScala)
          res.+=(decoder.decode(v))
        res.result()
      case field => throw new IllegalArgumentException(s"element is not a collection ($field)")
    }

  implicit def mapDecoder[T: ElementDecoder, M[_, _]](implicit builder: CanBuildFrom[Nothing, (String, T), M[String, T]]): ElementDecoder[M[String, T]] =
    primitive[M[String, T]] {
      case j: JsonObject =>
        val res = builder()
        for (e <- j.entrySet().asScala)
          res += ((e.getKey, ElementDecoder[T].decode(e.getValue)))
        res.result()
      case field => throw new IllegalArgumentException(s"element couldn't be decodes as a map ($field)")
    }
}
