import sbt.url

sonatypeProfileName := "com.github.sergeygrigorev"

publishMavenStyle := true

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/SergeyGrigorev/gson-object-scala-syntax"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/SergeyGrigorev/gson-object-scala-syntax"),
    "scm:git@github.com:SergeyGrigorev/gson-object-scala-syntax.git"
  )
)

developers:= List(
  Developer(
    id = "SergeyGrigorev",
    name = "Sergey Grigorev",
    email = "spgrigorev@gmail.com",
    url = url("https://github.com/SergeyGrigorev")
  )
)