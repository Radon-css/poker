val scala3Version = "3.6.4"

ThisBuild / version := "1.0.1"
ThisBuild / scalaVersion := scala3Version
ThisBuild / scalacOptions += "-Xmax-inlines:64"
Compile / mainClass := Some("de.htwg.poker.CoreServer")
Compile / resourceDirectory := baseDirectory.value / "src" / "main" / "resources"

val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"




ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0" cross CrossVersion.for3Use2_13,

  // Akka dependencies - all with cross version
  "com.typesafe.akka" %% "akka-actor" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-protobuf-v3" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion cross CrossVersion.for3Use2_13,
  
  // Other dependencies that need cross version
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2" cross CrossVersion.for3Use2_13,
  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2" cross CrossVersion.for3Use2_13,
  "com.typesafe" %% "ssl-config-core" % "0.6.1" cross CrossVersion.for3Use2_13,

  // Rest of your dependencies...
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",

  "com.typesafe.play" %% "play-json" % "2.10.0-RC5" cross CrossVersion.for3Use2_13,
  "com.typesafe.play" %% "play" % "2.9.0" cross CrossVersion.for3Use2_13,

  "org.slf4j" % "slf4j-api" % "2.0.7",
  "ch.qos.logback" % "logback-classic" % "1.4.7",

  "com.typesafe.slick" %% "slick" % "3.6.0" cross CrossVersion.for3Use2_13,
  "org.postgresql" % "postgresql" % "42.7.3",

  "org.mongodb.scala" %% "mongo-scala-driver" % "5.4.0" cross CrossVersion.for3Use2_13,

  "org.apache.kafka" % "kafka-clients" % "4.0.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "3.7.0" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2" cross CrossVersion.for3Use2_13,
  "org.scala-lang.modules" %% "scala-xml" % "2.1.0" cross CrossVersion.for3Use2_13
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
    tuiService,
    coreService,
    guiService,
    dbService,
    evalService
  )
  .aggregate(
    tuiService,
    coreService,
    guiService,
    dbService,
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
    name := "evalService",
     Compile / resourceDirectory := baseDirectory.value / "src" / "main" / "resources"
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

lazy val dbService = project
  .in(file("dbService"))
  .settings(
    name := "dbService"
  )