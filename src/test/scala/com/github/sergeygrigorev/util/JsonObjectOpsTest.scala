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
import com.google.gson._
import org.scalatest.FlatSpec

/**
 * Examples and unit tests for [[com.github.sergeygrigorev.util.syntax]].
 */
class JsonObjectOpsTest extends FlatSpec {

  "a json decoder" should "decode all jvm primitives" in {
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

  it should "decode some other simple types" in {
    val jsonObject = parse("""{string: "string", number: 10, object: {} }""")
    assert(jsonObject.getAs[String]("string") == "string")
    assert(jsonObject.getAs[BigDecimal]("number") == BigDecimal(10))
    assert(jsonObject.getAs[BigInt]("number") == BigInt(10))
  }

  it should "decode base gson types" in {
    val jsonObject = parse("""{number: 10, object: {}, array: [1, 2] }""")
    assert(jsonObject.getAs[JsonObject]("object") == jsonObject.get("object"))
    assert(jsonObject.getAs[JsonElement]("object") == jsonObject.get("object"))
    assert(jsonObject.getAs[JsonArray]("array") == jsonObject.getAsJsonArray("array"))
    assert(jsonObject.getAs[JsonPrimitive]("number") == jsonObject.getAsJsonPrimitive("number"))
  }

  it should "decode optional values" in {
    val jsonObject = parse("{a: null}")
    assert(jsonObject.find[Int]("b").isEmpty)
    assert(jsonObject.find[JsonObject]("a").isEmpty)
  }

  it should "decode list of primitives" in {
    val jsonObject = parse("{a: [1, 2, 3] }")
    assert(jsonObject.getAs[List[Int]]("a") == List(1, 2, 3))
  }

  it should "decode map of primitives" in {
    val jsonObject = parse("{a: { b: 1, c: 2 } }")
    assert(jsonObject.getAs[Map[String, Int]]("a") == Map("b" -> 1, "c" -> 2))
  }

  it should "decode custom type with manually created format" in {
    val jsonObject = parse("{a: { byte: 1, int: 2 } }")
    import JsonObjectOpsTest.customTypeParser
    assert(jsonObject.getAs[JsonObjectOpsTest.CustomType]("a") == JsonObjectOpsTest.CustomType(1, 2))
  }

  it should "decode custom type" in {
    case class CustomType2(long: Long, double: Double)
    val jsonObject = parse("{a: { long: 1, double: 2 } }")
    assert(jsonObject.getAs[CustomType2]("a") == CustomType2(1, 2))
  }

  it should "decode custom complicated type" in {
    case class CustomType2(long: Long, double: Double, list: Option[List[Int]])
    val jsonObject = parse("{a: { long: 1, double: 2, list: [3, 4] } }")
    assert(jsonObject.getAs[CustomType2]("a") == CustomType2(1, 2, Some(List(3, 4))))
  }

  it should "decode custom type with optional fields" in {
    case class CustomType2(long: Option[Long], double: Option[Double])
    val jsonObject = parse("{a: { long: 1, double: 2 } }")
    assert(jsonObject.getAs[CustomType2]("a") == CustomType2(Some(1), Some(2)))

    val jsonObject2 = parse("{a: { } }")
    assert(jsonObject2.getAs[CustomType2]("a") == CustomType2(None, None))
  }

  it should "decode custom option type" in {
    case class CustomType2(long: Long, double: Double)
    val jsonObject = parse("{a: { long: 1, double: 2 } }")
    assert(jsonObject.find[CustomType2]("a").contains(CustomType2(1, 2)))
  }

  it should "decode a coproduct" in {
    sealed trait CustomTrait
    case class CustomType1(long: Long) extends CustomTrait
    case class CustomType2(int: Int) extends CustomTrait
    val jsonObject = parse("{a: { type: \"CustomType2\", int: 2} }")
    assert(jsonObject.getAs[CustomTrait]("a") == CustomType2(2))
  }

  it should "decode an optional coproduct" in {
    sealed trait CustomTrait
    case class CustomType1(long: Long) extends CustomTrait
    case class CustomType2(int: Int) extends CustomTrait
    val jsonObject = parse("{b: { type: \"CustomType2\", int: 2} }")
    assert(jsonObject.find[CustomTrait]("a").isEmpty)
  }

  it should "correctly throw expected exceptions on empty elements" in {
    val jsonObject = parse("""{}""")
    // field is not found
    assertThrows[IllegalArgumentException](jsonObject.getAs[String]("nonexistent_field"))
    // nonexistent list couldn't be empty
    assertThrows[IllegalArgumentException](jsonObject.getAs[List[String]]("nonexistent_field"))
    // nonexistent map couldn't be empty
    assertThrows[IllegalArgumentException](jsonObject.getAs[Map[String, String]]("nonexistent_field"))
  }

  it should "correctly throw expected exceptions on elements having incorrect types" in {
    val jsonObject = parse("""{string: "string", b: {} }""")
    assertThrows[IllegalArgumentException](jsonObject.getAs[Map[String, String]]("string"))
    assertThrows[IllegalArgumentException](jsonObject.getAs[List[String]]("string"))
    assertThrows[IllegalArgumentException](jsonObject.getAs[JsonObject]("string"))
    assertThrows[IllegalArgumentException](jsonObject.getAs[JsonArray]("string"))
    assertThrows[IllegalArgumentException](jsonObject.getAs[JsonPrimitive]("b"))
  }

  it should "correctly throw expected exceptions on elements having incorrect types (Product)" in {
    case class Inner(long: Long)
    case class Outer(inner: Inner)
    val jsonObject = parse("""{b: { inner: 10 } }""")
    assertThrows[IllegalArgumentException](jsonObject.getAs[Outer]("b"))
  }

  it should "correctly throw expected exceptions on elements having incorrect types (Coproduct)" in {
    sealed trait CustomTrait
    case class CustomType1(long: Long) extends CustomTrait
    case class CustomType2(int: Int) extends CustomTrait
    val jsonObject = parse("""{b: 10 }""")
    assertThrows[IllegalArgumentException](jsonObject.getAs[CustomTrait]("b"))
  }

  it should "correctly throw expected in HList code generation instances" in {
    case class CustomType1(a: String)
    val jsonObject = parse("""{field: { string: "" } } """)
    assertThrows[IllegalArgumentException](jsonObject.getAs[CustomType1]("field"))
  }

  it should "correctly throw expected in Coproduct if object has no type or unknown" in {
    sealed trait CustomTrait
    case class CustomType1(long: Long) extends CustomTrait
    case class CustomType2(int: Int) extends CustomTrait
    val jsonObject = parse("{field: { int: 2} }")
    assertThrows[IllegalArgumentException](jsonObject.getAs[CustomTrait]("field"))

    val jsonObject2 = parse("{field: { type: \"CustomType3\", int: 2} }")
    val unknownType = intercept[IllegalArgumentException](jsonObject2.getAs[CustomTrait]("field"))
    assert(unknownType.getMessage.contains("unknown type"))
  }

  def parse(json: String): JsonObject = {
    new JsonParser().parse(json).getAsJsonObject
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

  case class CustomType(b: Byte, i: Int)

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