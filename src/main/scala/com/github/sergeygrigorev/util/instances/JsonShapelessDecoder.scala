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

import com.github.sergeygrigorev.util.data.ElementDecoder.primitive
import com.github.sergeygrigorev.util.data.{ ElementDecoder, FieldDecoder }
import com.github.sergeygrigorev.util.instances.gson._
import com.google.gson.{ JsonElement, JsonObject }
import shapeless.labelled.FieldType
import shapeless.{ :+:, ::, CNil, Coproduct, HList, HNil, LabelledGeneric, Lazy, Witness, labelled }

/**
 * Shapeless decoders.
 */
trait JsonShapelessDecoder {

  implicit def hnilDecoder: ElementDecoder[HNil] =
    primitive[HNil](_ => HNil)

  implicit def hlistDecoder[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hDecoder: Lazy[FieldDecoder[H]],
    tDecoder: ElementDecoder[T]): ElementDecoder[FieldType[K, H] :: T] = {

    val fieldName = witness.value.name
    primitive[FieldType[K, H] :: T] {
      case j: JsonObject =>
        labelled.field[K](hDecoder.value.decode(j, fieldName)) :: tDecoder.decode(j)
      case _ => throw new IllegalArgumentException(s"element $fieldName is not an object")
    }
  }

  implicit def genericHListDecoder[A, R <: HList](implicit
    gen: LabelledGeneric.Aux[A, R],
    dec: Lazy[ElementDecoder[R]]): ElementDecoder[A] =
    primitive[A] { json: JsonElement =>
      val hlist = dec.value.decode(json)
      gen.from(hlist)
    }

  type CoproductMapType = Map[String, ElementDecoder[_]]

  trait CoproductMap[F] {
    def map: CoproductMapType
  }

  implicit val cnilMap: CoproductMap[CNil] =
    new CoproductMap[CNil] {
      override val map: CoproductMapType = Map.empty
    }

  implicit def coproductMap[K <: Symbol, H, T <: Coproduct](
    implicit
    witness: Witness.Aux[K],
    hval: Lazy[ElementDecoder[H]],
    tail: CoproductMap[T]): CoproductMap[FieldType[K, H] :+: T] = {
    val name = witness.value.name
    new CoproductMap[FieldType[K, H] :+: T] {
      override val map: CoproductMapType = tail.map + (name -> hval.value)
    }
  }

  implicit def coproductGeneric[A, R <: Coproduct](
    implicit
    gen: LabelledGeneric.Aux[A, R],
    map: Lazy[CoproductMap[R]]): ElementDecoder[A] =
    primitive[A] {
      case j: JsonObject if j.has("type") =>
        val `type` = FieldDecoder[String].decode(j, "type")
        map.value.map.get(`type`) match {
          case Some(decoder) => decoder.decode(j).asInstanceOf[A]
          case None => throw new IllegalArgumentException(s"unknown type ${`type`} in $j")
        }
      case e => throw new IllegalArgumentException(s"element $e is not an object")
    }
}
