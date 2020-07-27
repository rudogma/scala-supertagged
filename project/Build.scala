import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Versions {
  val Scala211 = "2.11.11"
  val Scala212 = "2.12.12"
  val Scala213 = "2.13.3"

  val VERSION = "2.0-RC1"

  val ScalaCross = Seq(Scala213, Scala212, Scala211)

}

object Project {
  val defaultSettings = Seq(

    organization in ThisBuild := "org.rudogma",
    name := "supertagged",
    version in ThisBuild := Versions.VERSION,
    isSnapshot in ThisBuild := true,

    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),

    homepage := Some(url("https://github.com/Rudogma/scala-supertagged")),

    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 11 | 12)) => List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch))
        case _                  => List()
      }
    },

    libraryDependencies ++= List (
      scalaOrganization.value % "scala-reflect" % scalaVersion.value % Provided
    )
  )


  lazy val moduleNativeSettings = Def.settings(
    scalaVersion := Versions.Scala211,
    crossScalaVersions := Seq(Versions.Scala211),
    // Disable Scaladoc generation because of:
    // [error] dropping dependency on node with no phase object: mixin
    Compile / doc / sources := Seq.empty,
    Compile / test / sources := Seq.empty,
    libraryDependencies := Seq.empty
//    doctestGenTests := Seq.empty
  )
}

object Compiler {

  val defaultSettings = Seq(
    scalacOptions in ThisBuild ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused-import",
      "-language:higherKinds",
      "-language:experimental.macros"
    ),
    scalacOptions in ThisBuild ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => List("-Ymacro-annotations")
        case _                  => List()
      }
    },
      // ++ (if (CrossVersion.partialVersion(scalaVersion.value).exists(_._2 != 13)) Seq("-Yno-adapted-args") else Seq()),

    scalaVersion in ThisBuild := Versions.Scala212,
    Compile / crossScalaVersions := Versions.ScalaCross
  )
}
object Publish {
  val defaultSettings = Seq(

    description := "Unboxed (multi-nested-)tagged + unboxed newtypes. Better and much friendlier alternative to AnyVals",
    developers += Developer(
      "rudogma",
      "Mikhail Savinov",
      "mikhail@rudogma.org",
      url("https://github.com/Rudogma")
    ),

    isSnapshot := false,
    publishTo in ThisBuild := Some(if (isSnapshot.value){
      Opts.resolver.sonatypeSnapshots
    }else{
      Opts.resolver.sonatypeStaging
    }),

    publishMavenStyle := true,

    publishArtifact in Test := false,

    pomIncludeRepository := { _ => false },

//    releasePublishArtifactsAction := PgpKeys.publishSigned.value,

    licenses += ("MIT", url("https://opensource.org/licenses/MIT")),

    scmInfo := Some(
      ScmInfo(
        url("https://github.com/Rudogma/scala-supertagged"),
        "scm:git:git@github.com:Rudogma/scala-supertagged.git",
        Some("scm:git:ssh://github.com:Rudogma/scala-supertagged.git")
      )
    )
  )
}

object Tests {
  val defaultSettings = Seq(
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "spire" % "0.17.0-RC1" % "test",
      "org.scalatest" %%% "scalatest" % "3.1.2" % "test",
      "org.scalacheck" %%% "scalacheck" % "1.14.3" % "test",
      "com.chuusai" %%% "shapeless" % "2.3.3" % "test"
    ),
    Test / crossScalaVersions := Seq(Versions.Scala212, Versions.Scala213),
    excludeFilter in (Test, unmanagedSources) := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor == 13 =>
          HiddenFileFilter || "*Bench*"
        case _ =>
          HiddenFileFilter
      }
    }



  )
}


object Console {
  val defaultSettings = Seq(
    scalacOptions ~= (_ filterNot (Set("-Xfatal-warnings", "-Ywarn-unused-import").contains)),
  )
}
