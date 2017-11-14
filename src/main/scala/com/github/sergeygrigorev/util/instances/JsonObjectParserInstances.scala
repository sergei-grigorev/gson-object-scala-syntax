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
import com.google.gson.JsonObject
import scala.collection.JavaConverters._

/**
 * More specific decoders for [[FieldDecoder]].
 */
trait JsonObjectParserInstances {
  import FieldDecoder.construct

  type StringMap[A] = Map[String, A]

  implicit val jsonObjectParser: FieldDecoder[JsonObject] =
    construct[JsonObject] { (json: JsonObject, field: String) =>
      json.get(field) match {
        case j: JsonObject => j
        case _ => null
      }
    }

  implicit def jsonElementToParser[T: ElementDecoder]: FieldDecoder[T] =
    construct[T] { (json: JsonObject, field: String) =>
      if (json.has(field)) ElementDecoder[T].decode(json.get(field))
      else throw new IllegalArgumentException(s"element $field is null and couldn't be decoded ($json)")
    }

  implicit def optionObjectParser[T: FieldDecoder]: FieldDecoder[Option[T]] =
    construct[Option[T]] { (json: JsonObject, field: String) =>
      if (json.has(field)) Some(FieldDecoder[T].decode(json, field))
      else None
    }

  implicit def jsonListParser[T: ElementDecoder]: FieldDecoder[List[T]] =
    construct[List[T]] { (json: JsonObject, field: String) =>
      if (json.has(field)) {
        val view = json.getAsJsonArray(field).asScala.view
        view.map(ElementDecoder[T].decode(_)).toList
      } else Nil
    }

  implicit def hashMapParser[T: ElementDecoder]: FieldDecoder[StringMap[T]] =
    construct[StringMap[T]] { (json: JsonObject, field: String) =>
      val m = FieldDecoder[JsonObject].decode(json, field).entrySet().asScala
      m.map(e => (e.getKey, ElementDecoder[T].decode(e.getValue))).toMap
    }
}
