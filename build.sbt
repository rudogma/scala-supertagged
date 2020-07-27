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
    Console.defaultSettings

lazy val supertagged = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(defaultSettings)
  .settings(NonTests.defaultSettings)
  .nativeSettings(
    Project.moduleNativeSettings
  )
  
lazy val tests = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(defaultSettings)
  .settings(Tests.defaultSettings)
  .settings(
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
  .jsSettings(
    Test / parallelExecution := false
  )
  .dependsOn(supertagged)

lazy val root = project.in(file("."))
  .settings(defaultSettings)
  .settings(
    name := "supertagged",
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
  .aggregate(supertagged.jvm, supertagged.js, tests.jvm, tests.js)
