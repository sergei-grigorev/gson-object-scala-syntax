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
import com.github.sergeygrigorev.util.data.ElementDecoder.primitive

/**
 * Scala primitives.
 */
trait JsonPrimitives {

  implicit val booleanPrimitiveParser: ElementDecoder[Boolean] =
    primitive[Boolean](json => json.getAsBoolean)

  implicit val intPrimitiveParser: ElementDecoder[Int] =
    primitive[Int](json => json.getAsInt)

  implicit val longPrimitiveParser: ElementDecoder[Long] =
    primitive[Long](json => json.getAsLong)

  implicit val doublePrimitiveParser: ElementDecoder[Double] =
    primitive[Double](json => json.getAsDouble)

  implicit val floatPrimitiveParser: ElementDecoder[Float] =
    primitive[Float](json => json.getAsFloat)

  implicit val bytePrimitiveParser: ElementDecoder[Byte] =
    primitive[Byte](json => json.getAsByte)

  implicit val charPrimitiveParser: ElementDecoder[Char] =
    primitive[Char](json => json.getAsCharacter)

  implicit val shortPrimitiveParser: ElementDecoder[Short] =
    primitive[Short](json => json.getAsShort)
}
