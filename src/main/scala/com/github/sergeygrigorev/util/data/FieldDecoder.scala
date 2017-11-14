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

package com.github.sergeygrigorev.util.data

import com.google.gson.JsonObject

trait FieldDecoder[A] {
  def decode(json: JsonObject, field: String): A
}

object FieldDecoder {
  def apply[A](implicit t: FieldDecoder[A]): FieldDecoder[A] = t

  def construct[A](f: (JsonObject, String) => A): FieldDecoder[A] = new FieldDecoder[A] {
    override def decode(json: JsonObject, field: String): A = f(json, field)
  }
}
