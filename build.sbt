import bloop.config.Config.Java
val scala3Version = "3.6.4"

ThisBuild / version := "1.0.1"
ThisBuild / scalaVersion := scala3Version
ThisBuild / scalacOptions += "-Xmax-inlines:64"
Compile / mainClass := Some("de.htwg.poker.CoreServer")
Compile / resourceDirectory := baseDirectory.value / "src" / "main" / "resources"


ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.5" % "test",
  //"org.scalafx" %% "scalafx" % "21.0.0-R32",

  // Akka - using consistent versions
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  // Add explicit dependency to prevent older version being pulled in
  "com.typesafe.akka" %% "akka-serialization-jackson" % "2.8.5",

  // Circe
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",

  //play
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "com.typesafe.play" %% "play" % "2.9.0",

    "org.slf4j" % "slf4j-api" % "2.0.7",
  "ch.qos.logback" % "logback-classic" % "1.4.7",

  //slick
  "com.typesafe.slick" %% "slick" % "3.6.0" cross CrossVersion.for3Use2_13,
  "org.postgresql" % "postgresql" % "42.7.3",

  //mongo
  "org.mongodb.scala" %% "mongo-scala-driver" % "5.4.0" cross CrossVersion.for3Use2_13,
  
)

/*libraryDependencies ++= {
    // Determine OS version of JavaFX binaries
    lazy val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux")   => "linux"
      case n if n.startsWith("Mac")     => "mac"
      case n if n.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }
    Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
      .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}*/

lazy val root = project
  .in(file("."))
  .settings(
    name := "poker"
  )
  .dependsOn(
    coreService,
    dbService,
    evalService
  )
  .aggregate(
    coreService,
    dbService,
    evalService
  )


lazy val evalService = project
  .in(file("evalService"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "evalService",
     Compile / resourceDirectory := baseDirectory.value / "src" / "main" / "resources"
  )

lazy val coreService = project
  .in(file("coreService"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "coreService"
  )



lazy val dbService = project
  .in(file("dbService"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "dbService"
  )