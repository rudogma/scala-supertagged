import sbt._

organization := "org.rudogma"
name := "supertagged"


val SCALA_VERSION = "2.12.2"

scalaVersion in ThisBuild := SCALA_VERSION
crossScalaVersions in ThisBuild := Seq("2.12.2", "2.11.11")
releaseCrossBuild := true

scalacOptions in ThisBuild ++= Seq(
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


publishTo in ThisBuild := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)
publishMavenStyle := true
pomIncludeRepository := (_ => false)
releasePublishArtifactsAction := PgpKeys.publishSigned.value

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  organization := "org.rudogma",
  name := "supertagged",

  description := "Better (multi-nested-)tagged types for Scala, Intellij Idea autocomplete features working pretty fine.",
  developers += Developer(
    "rudogma",
    "Mikhail Savinov",
    "mikhail@rudogma.org",
    url("https://github.com/Rudogma")
  ),

  publishMavenStyle := true,
  pomIncludeRepository := (_ => false),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,

  licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/Rudogma/scala-supertagged"),
      "scm:git:git@github.com:Rudogma/scala-supertagged.git",
      Some("scm:git:ssh://github.com:Rudogma/scala-supertagged.git")
    )
  ),
  homepage := Some(url("https://github.com/Rudogma/scala-supertagged")),

  libraryDependencies ++= Seq(
    "org.typelevel" %%% "spire" % "0.14.1" % "test",
    "org.scalatest" %%% "scalatest" % "3.0.1" % "test",
    "org.scalacheck" %%% "scalacheck" % "1.13.4" % "test",
    "com.chuusai" %%% "shapeless" % "2.3.2" % "test"
  )
)

lazy val cross =
  crossProject.in(file("."))
    .settings(commonSettings)

lazy val jvm = cross.jvm
lazy val js = cross.js

lazy val root =
  project.in(file("."))
    .aggregate(jvm, js)
    .settings(
      publish := {},
      publishLocal := {},
      publishArtifact := false
    )