import ReleaseTransformations._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.sergeygrigorev"
    )),
    name := "gson-object-scala-syntax",
    description := "Scala syntax for google gson object",
    startYear := Some(2017),

    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,

    useGpg := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,

    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),

    crossSbtVersions := Vector("0.13.16", "1.0.2"),
    crossScalaVersions := Seq("2.11.12", "2.12.4"),
    releaseCrossBuild := true,

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,              // : ReleaseStep
      inquireVersions,                        // : ReleaseStep
      runClean,                               // : ReleaseStep
      runTest,                                // : ReleaseStep
      setReleaseVersion,                      // : ReleaseStep
      commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
      tagRelease,                             // : ReleaseStep
      publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
      setNextVersion,                         // : ReleaseStep
      commitNextVersion,                      // : ReleaseStep
      pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
    ),

    libraryDependencies ++= Seq(
      "com.google.code.gson" % "gson" % "2.8.2" % "provided",
      "com.chuusai" %% "shapeless" % "2.3.2"
    ),

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.3" % "test"
    )
  )
