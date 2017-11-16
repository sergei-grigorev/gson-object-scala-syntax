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
import com.github.sergeygrigorev.util.data.FieldDecoder
import com.github.sergeygrigorev.util.data.FieldDecoder.construct
import com.google.gson.JsonObject

import scala.language.higherKinds

/**
 * [[ElementDecoder]] to [[FieldDecoder]]
 */
trait JsonElementToJsonDecoder {
  /* ElementDecoder to FieldDecoder */
  implicit def jsonElementToFieldDecoder[T: ElementDecoder]: FieldDecoder[T] =
    construct[T] { (json: JsonObject, field: String) =>
      if (json.has(field) && !json.get(field).isJsonNull) ElementDecoder[T].decode(json.get(field))
      else throw new IllegalArgumentException(s"element $field is null and couldn't be decoded ($json)")
    }

  /* ElementDecoder to FieldDecoder (optional field) */
  implicit def optionObjectDecoder[T: FieldDecoder]: FieldDecoder[Option[T]] =
    construct[Option[T]] { (json: JsonObject, field: String) =>
      if (json.has(field) && !json.get(field).isJsonNull) Some(FieldDecoder[T].decode(json, field))
      else None
    }

  /* Higher Kind type F[T] */
  implicit def tuple1ToFieldDecoder[F[_], T](implicit ev: ElementDecoder[F[T]]): FieldDecoder[F[T]] =
    jsonElementToFieldDecoder(ev)

  /* Higher Kind type F[T1, T2] */
  implicit def tuple2ToFieldDecoder[F[_, _], T1, T2](implicit ev: ElementDecoder[F[T1, T2]]): FieldDecoder[F[T1, T2]] =
    jsonElementToFieldDecoder(ev)
}
