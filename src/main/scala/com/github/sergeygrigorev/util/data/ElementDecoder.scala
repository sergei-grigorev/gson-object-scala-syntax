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

import com.google.gson.JsonElement

trait ElementDecoder[A] {
  def decode(json: JsonElement): A
}

object ElementDecoder {
  def apply[A](implicit t: ElementDecoder[A]): ElementDecoder[A] = t

  def primitive[A](f: JsonElement => A): ElementDecoder[A] = new ElementDecoder[A] {
    override def decode(json: JsonElement): A = f(json)
  }
}
