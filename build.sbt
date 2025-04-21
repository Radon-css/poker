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
    "io.circe" %% "circe-core" % "0.14.6",
    "io.circe" %% "circe-generic" % "0.14.6",
    "io.circe" %% "circe-parser" % "0.14.6",
    "de.heikoseeberger" %% "akka-http-circe" % "1.40.0"
  )


lazy val root = project
  .in(file("."))
  .settings(
    name := "poker",
    dependencies
  )
  .dependsOn(
    utilService,
    controllerService,
    modelService,
    fileIOService
  )
  .aggregate(
    utilService,
    controllerService,
    modelService,
    fileIOService
  )

lazy val utilService = project
  .in(file("utilService"))
  .settings(
    name := "utilService",
    dependencies
  )

lazy val controllerService = project
  .in(file("controllerService"))
  .settings(
    name := "controllerService",
    dependencies
  )

lazy val modelService = project
  .in(file("modelService"))
  .settings(
    name := "modelService",
    dependencies
  )

lazy val fileIOService = project
  .in(file("fileIOService"))
  .settings(
    name := "fileIOService",
    dependencies
  )
