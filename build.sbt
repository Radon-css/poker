val scala3Version = "3.3.1"

lazy val dependencies = Seq(
  version := "1.0.1",
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",
  libraryDependencies ++= {
    // Determine OS version of JavaFX binaries
    lazy val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux")   => "linux"
      case n if n.startsWith("Mac")     => "mac"
      case n if n.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }
    Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
      .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
  }
)

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "com.typesafe.akka" %% "akka-http" % "10.5.3",

  // Circe
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "poker",
    dependencies
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
    name := "tuiService",
    dependencies
  )

lazy val evalService = project
  .in(file("evalService"))
  .settings(
    name := "evalService",
    dependencies
  )

lazy val coreService = project
  .in(file("coreService"))
  .settings(
    name := "coreService",
    dependencies
  )

lazy val guiService = project
  .in(file("guiService"))
  .settings(
    name := "guiService",
    dependencies
  )

lazy val fileIOService = project
  .in(file("fileIOService"))
  .settings(
    name := "fileIOService",
    dependencies
  )
