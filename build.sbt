import sbt._

import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

releaseCrossBuild := true
publishMavenStyle := true
pomIncludeRepository := (_ => false)
releasePublishArtifactsAction := PgpKeys.publishSigned.value

parallelExecution in ThisBuild := false

lazy val defaultSettings =
  Project.defaultSettings ++
    Compiler.defaultSettings ++
    Publish.defaultSettings ++
    Tests.defaultSettings ++
    Console.defaultSettings

lazy val supertagged = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(defaultSettings: _*)
  .jsSettings(
    parallelExecution in Test := false
  )
  .nativeSettings(
    Project.moduleNativeSettings
  )

lazy val root = project.in(file("."))
  .settings(defaultSettings: _*)
  .settings(
    name := "supertagged",
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
  .aggregate(supertagged.jvm, supertagged.js)
