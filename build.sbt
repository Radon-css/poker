val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "poker",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.9")
