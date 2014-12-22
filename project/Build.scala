import sbt._
import sbtappengine.Plugin._

object Gae4sBuild extends Build {
  import sbt.Keys._
  import sbtbuildinfo.Plugin._
  import Versions._

  lazy val defaultSetting = Defaults.defaultSettings ++ buildInfoSettings ++
    Seq(
      sourceGenerators in Compile <+= buildInfo,
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
      scalaVersion := "2.11.4",
      crossScalaVersions := Seq("2.10.4", "2.11.4"),
      scalacOptions ++= Seq("-feature", "-deprecation"),
      organization := "com.xhachi",
      version := "0.6-SNAPSHOT",
      publishTo <<= version { (v: String) =>
        val base = "/Users/takashi/Documents/xhachi/repository"
        if (v.trim.endsWith("SNAPSHOT"))
          Some(Resolver.file("snapshot", file(base + "/maven2-snapshot")))
        else
          Some(Resolver.file("release" , file(base + "/maven2")))
      },
      parallelExecution in Test := false
    )

  lazy val core = Project(
    id = "gae4s-core",
    base = file("core"),
    settings = defaultSetting ++ Seq(
      name := "gae4s-core",
      buildInfoPackage := "com.xhachi.gae4s.buildinfo",
      libraryDependencies ++= defaultDependency
    )
  ).dependsOn(scalatest % "test")

  lazy val scalatest = Project(
    id = "gae4s-scalatest",
    base = file("scalatest"),
    settings = defaultSetting ++ Seq(
      name := "gae4s-scalatest",
      libraryDependencies ++= defaultDependency ++ Seq(
        "com.google.appengine" % "appengine-api-stubs" % appengineVersion,
        "com.google.appengine" % "appengine-testing" % appengineVersion,
        "org.scalatest" %% "scalatest" % scalatestVersion
      )
    )
  )

  lazy val store_ext = Project(
    id = "gae4s-store-extension",
    base = file("store-extension"),
    settings = defaultSetting ++ Seq(
      name := "gae4s-store-extension",
      libraryDependencies ++= defaultDependency
    )
  ).dependsOn(core).dependsOn(scalatest % "test")

  lazy val sample = Project(
    id = "gae4s-sample",
    base = file("sample"),
    settings = defaultSetting ++ appengineSettings ++ Seq(
      name := "gae4s-sample",
      libraryDependencies ++= defaultDependency ++ Seq(
        "javax.servlet" % "servlet-api" % "2.5" % "provided",
        "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.v20100331" % "container"
      )
    )
  ).dependsOn(core)

  val defaultDependency = {
    Seq(
      "org.json4s" %% "json4s-native" % "3.2.11",
      "org.json4s" %% "json4s-ext" % "3.2.11",
      "com.google.appengine" % "appengine-api-1.0-sdk" % appengineVersion,
      "com.google.appengine.tools" % "appengine-gcs-client" % "0.4.3",
      "com.google.appengine" % "appengine-api-stubs" % appengineVersion % "test",
      "com.google.appengine" % "appengine-testing" % appengineVersion % "test",
      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    )
  }
}

object Versions {
  val appengineVersion = "1.9.17"
  val scalatestVersion = "2.2.2"
}