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

package com.github.sergeygrigorev.util.syntax

import com.github.sergeygrigorev.util.data.FieldDecoder
import com.google.gson.JsonObject

import scala.language.implicitConversions

trait JsonSyntax {
  implicit def gsonSyntaxOps[A](j: JsonObject): JsonSyntaxOps = new JsonSyntaxOps(j)
}

final class JsonSyntaxOps(val o: JsonObject) extends AnyVal {
  def getAs[T](field: String)(implicit f: FieldDecoder[T]): T = {
    f.decode(o, field)
  }

  def find[T](field: String)(implicit f: FieldDecoder[Option[T]]): Option[T] = {
    getAs[Option[T]](field)
  }
}
