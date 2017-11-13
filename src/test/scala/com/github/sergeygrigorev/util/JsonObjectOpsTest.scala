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

package com.github.sergeygrigorev.util

import com.github.sergeygrigorev.util.instances.gson._
import com.github.sergeygrigorev.util.syntax.gson._
import com.google.gson.{ Gson, JsonObject, JsonParser }
import org.scalatest.FlatSpec

/**
 * Examples and unit tests for [[com.github.sergeygrigorev.util.syntax]].
 */
class JsonObjectOpsTest extends FlatSpec {

  "a json decoder" should "decode all primitives" in {
    val pattern = JsonObjectOpsTest.Primitives(
      boolean = true,
      byte = Byte.MaxValue,
      char = Char.MaxValue,
      short = Short.MaxValue,
      int = Int.MaxValue,
      long = Long.MaxValue,
      float = Float.MaxValue,
      double = Double.MaxValue)

    val json = new Gson().toJson(pattern)
    val jsonObject = new JsonParser().parse(json).getAsJsonObject

    assert(jsonObject.getAs[Boolean]("boolean") == pattern.boolean)
    assert(jsonObject.getAs[Byte]("byte") == pattern.byte)
    assert(jsonObject.getAs[Char]("char") == pattern.char)
    assert(jsonObject.getAs[Short]("short") == pattern.short)
    assert(jsonObject.getAs[Int]("int") == pattern.int)
    assert(jsonObject.getAs[Long]("long") == pattern.long)
    assert(jsonObject.getAs[Float]("float") == pattern.float)
    assert(jsonObject.getAs[Double]("double") == pattern.double)
  }

  it should "decode optional values" in {
    val jsonObject = new JsonParser().parse("{a: null}").getAsJsonObject
    assert(jsonObject.find[Int]("b").isEmpty)
  }

  it should "decode list of primitives" in {
    val jsonObject = new JsonParser().parse("{a: [1, 2, 3] }").getAsJsonObject
    assert(jsonObject.getAs[List[Int]]("a") == List(1, 2, 3))
  }

  it should "decode map of primitives" in {
    val jsonObject = new JsonParser().parse("{a: { b: 1, c: 2 } }").getAsJsonObject
    assert(jsonObject.getAs[Map[String, Int]]("a") == Map("b" -> 1, "c" -> 2))
  }

  it should "decode custom type with manually created format" in {
    val jsonObject = new JsonParser().parse("{a: { byte: 1, int: 2 } }").getAsJsonObject
    assert(jsonObject.getAs[JsonObjectOpsTest.CustomType]("a") == JsonObjectOpsTest.CustomType(1, 2))
  }

  it should "decode custom type" in {
    case class CustomType2(long: Long, double: Double)
    val jsonObject = new JsonParser().parse("{a: { long: 1, double: 2 } }").getAsJsonObject
    assert(jsonObject.getAs[CustomType2]("a") == CustomType2(1, 2))
  }

  it should "decode custom type with optional fields" in {
    case class CustomType2(long: Option[Long], double: Option[Double])
    val jsonObject = new JsonParser().parse("{a: { long: 1, double: 2 } }").getAsJsonObject
    assert(jsonObject.getAs[CustomType2]("a") == CustomType2(Some(1), Some(2)))

    val jsonObject2 = new JsonParser().parse("{a: { } }").getAsJsonObject
    assert(jsonObject2.getAs[CustomType2]("a") == CustomType2(None, None))
  }

  it should "decode custom option type" in {
    case class CustomType2(long: Long, double: Double)
    val jsonObject = new JsonParser().parse("{a: { long: 1, double: 2 } }").getAsJsonObject
    assert(jsonObject.find[CustomType2]("a").contains(CustomType2(1, 2)))
  }

  it should "decode a coproduct" in {
    sealed trait CustomTrait
    case class CustomType1(long: Long) extends CustomTrait
    case class CustomType2(int: Int) extends CustomTrait
    val jsonObject = new JsonParser().parse("{a: { type: \"CustomType2\", int: 2} }").getAsJsonObject
    assert(jsonObject.getAs[CustomTrait]("a") == CustomType2(2))
  }

  it should "decode an optional coproduct" in {
    sealed trait CustomTrait
    case class CustomType1(long: Long) extends CustomTrait
    case class CustomType2(int: Int) extends CustomTrait
    val jsonObject = new JsonParser().parse("{b: { type: \"CustomType2\", int: 2} }").getAsJsonObject
    assert(jsonObject.find[CustomTrait]("a").isEmpty)
  }
}

object JsonObjectOpsTest {
  case class Primitives(
    boolean: Boolean,
    byte: Byte,
    char: Char,
    short: Short,
    int: Int,
    long: Long,
    float: Float,
    double: Double)

  case class CustomType(byte: Byte, int: Int)

  /* manually created decoder */
  import com.github.sergeygrigorev.util.data.ElementDecoder
  import com.github.sergeygrigorev.util.data.ElementDecoder._
  implicit val customTypeParser: ElementDecoder[CustomType] = primitive[CustomType] {
    case root: JsonObject =>
      val byte = root.getAs[Byte]("byte")
      val int = root.getAs[Int]("int")
      CustomType(byte, int)
    case _ => throw new IllegalArgumentException("is not an element")
  }
}