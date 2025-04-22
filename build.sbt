val scala3Version = "3.6.4"

  ThisBuild / version := "1.0.1"
  ThisBuild / scalaVersion := scala3Version

  ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  "org.scalafx" %% "scalafx" % "21.0.0-R32",

  // Akka
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "com.typesafe.akka" %% "akka-http" % "10.5.3",

  // Circe
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",

  //play
  "com.typesafe.play" %% "play-json" % "2.10.2",
  "com.typesafe.play" %% "play" % "2.9.0"
)


lazy val root = project
  .in(file("."))
  .settings(
    name := "poker"
  )
  .dependsOn(
    tuiService,
    coreService,
    guiService,
    fileIOService,
    evalService
  )
  .aggregate(
    tuiService,
    coreService,
    guiService,
    fileIOService,
    evalService
  )

lazy val tuiService = project
  .in(file("tuiService"))
  .settings(
    name := "tuiService"
  )

lazy val evalService = project
  .in(file("evalService"))
  .settings(
    name := "evalService"
  )

lazy val coreService = project
  .in(file("coreService"))
  .settings(
    name := "coreService"
  )

lazy val guiService = project
  .in(file("guiService"))
  .settings(
    name := "guiService"
  )

lazy val fileIOService = project
  .in(file("fileIOService"))
  .settings(
    name := "fileIOService"
  )
