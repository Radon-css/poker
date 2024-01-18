val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "poker",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    testOptions in Test += Tests.Filter(s =>
      s.startsWith("de.htwg.poker.model") ||
        s.startsWith("de.htwg.poker.util") ||
        s.startsWith("de.htwg.poker.controller")
    ),
    coverageExcludedPackages := "de\\.htwg\\.poker\\.aview\\..*"
  )

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test

libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32"
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
