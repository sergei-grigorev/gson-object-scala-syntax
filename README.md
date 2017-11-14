# gson-object-scala-syntax

Pure and safe type-inference syntax for work 
with [https://github.com/google/gson](com.google.gson.JsonObject).

## Simple examples of usage.
```
import com.github.sergeygrigorev.helpers.instances.gson._
import com.github.sergeygrigorev.helpers.syntax.gson._

// use scala primitive
val jsonObject = new JsonParser().parse("{a: null}").getAsJsonObject
assert(jsonObject.find[Int]("b").isEmpty)

// use scala list with primitive
val jsonObject = new JsonParser().parse("{a: [1, 2, 3] }").getAsJsonObject
assert(jsonObject.getAs[List[Int]]("a") == List(1, 2, 3))

// manually define format
case class CustomType(byte: Byte, int: Int)
import com.github.sergeygrigorev.helpers.data.ElementParser
import com.github.sergeygrigorev.helpers.data.ElementParser._
implicit val customTypeParser: ElementParser[CustomType] = primitive[CustomType] {
  case root: JsonObject =>
    val byte = root.getAs[Byte]("byte")
    val int = root.getAs[Int]("int")
    CustomType(byte, int)
  case _ => throw new IllegalArgumentException("is not an element")
}

// impicitly load manually defined format
val jsonObject = new JsonParser().parse("{a: { byte: 1, int: 2 } }").getAsJsonObject
assert(jsonObject.getAs[JsonObjectOpsTest.CustomType]("a") == JsonObjectOpsTest.CustomType(1, 2))

// automatically inlined by shapeless HList
case class CustomType2(long: Long, double: Double)
val jsonObject = new JsonParser().parse("{a: { long: 1, double: 2 } }").getAsJsonObject
assert(jsonObject.getAs[CustomType2]("a") == CustomType2(1, 2))
```

You can use any of Scala case classes and they will be
automatically derived by shapeless library.