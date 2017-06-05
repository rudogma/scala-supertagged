import sbt._

organization := "org.rudogma"
name := "supertagged"

description := "Better (multi-nested-)tagged types for Scala, Intellij Idea autocomplete features working pretty fine."
developers += Developer(
  "rudogma",
  "Mikhail Savinov",
  "mikhail@rudogma.org",
  url("https://github.com/Rudogma")
)
licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))


val SCALA_VERSION = "2.12.2"


scalaVersion := SCALA_VERSION
crossScalaVersions := Seq("2.11.11", "2.12.2")
releaseCrossBuild := true


(scalaVersion in ThisBuild) := SCALA_VERSION


scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import"
)



resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "spire" % "0.14.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "com.chuusai" %% "shapeless" % "2.3.2" % "test"
)


publishArtifact in (Compile, packageDoc) := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

publishMavenStyle := true
publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)
pomIncludeRepository := (_ => false)

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
scmInfo := Some(
  ScmInfo(

    url("https://github.com/Rudogma/scala-supertagged"),
    "scm:git:git@github.com:Rudogma/scala-supertagged.git",
    Some("scm:git:ssh://github.com:Rudogma/scala-supertagged.git")
  )
)
homepage := Some(url("https://github.com/Rudogma/scala-supertagged"))

