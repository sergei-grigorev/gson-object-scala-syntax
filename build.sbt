lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.sergeygrigorev",
      scalaVersion := "2.12.3",
      version := "0.3.0-SNAPSHOT"
    )),
    name := "gson-object-scala-syntax",
    description := "Scala syntax for google gson object",
    startYear := Some(2017),

    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,
    useGpg := true,

    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),


    libraryDependencies ++= Seq(
      "com.google.code.gson" % "gson" % "2.8.2" % "provided",
      "com.chuusai" %% "shapeless" % "2.3.2"
    ),

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.3" % "test"
    )
  )
