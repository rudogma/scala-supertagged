import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.toPlatformDepsGroupID
import sbt.Keys._
import sbt._


object Versions {
  val Supertagged = "1.5"
  val Scala = "2.13.1"
  val ScalaCross = Seq("2.13.1", "2.12.10", "2.11.11")

}

object Project {
  val defaultSettings = Seq(

    organization in ThisBuild := "org.rudogma",
    name := "supertagged",
    version in ThisBuild := Versions.Supertagged,

    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),

    homepage := Some(url("https://github.com/Rudogma/scala-supertagged"))
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
      "-Ywarn-unused-import"
    ) ++ (if (CrossVersion.partialVersion(scalaVersion.value).exists(_._2 != 13)) Seq("-Yno-adapted-args") else Seq()),

    scalaVersion in ThisBuild := Versions.Scala,
    crossScalaVersions := Versions.ScalaCross
  )
}
object Publish {
  val defaultSettings = Seq(

    description := "Scala: Typelevel unboxed compile time dimensional analysis over tagged types. Intellij Idea compatible 100%",
    developers += Developer(
      "rudogma",
      "Mikhail Savinov",
      "mikhail@rudogma.org",
      url("https://github.com/Rudogma")
    ),

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
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "spire" % "0.17.0-M1" % "test",
      "org.scalatest" %%% "scalatest" % "3.1.0" % "test",
      "org.scalacheck" %%% "scalacheck" % "1.14.2" % "test",
      "com.chuusai" %%% "shapeless" % "2.3.3" % "test"
    )
  )
}


object Console {
  val defaultSettings = Seq(
    scalacOptions ~= (_ filterNot (Set("-Xfatal-warnings", "-Ywarn-unused-import").contains)),

    initialCommands in console := """
     import supertagged._""".stripMargin
  )
}
