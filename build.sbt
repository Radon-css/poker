val scala3Version = "3.6.4"
val scalatestVersion = "3.2.14"
val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"
val circeVersion = "0.14.1"
val jacksonVersion = "2.15.3"



ThisBuild / version := "1.0.1"
ThisBuild / scalaVersion := scala3Version

ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % scalatestVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0" cross CrossVersion.for3Use2_13,
  "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.2",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.4",
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test cross CrossVersion.for3Use2_13,
  "org.wiremock" % "wiremock" % "3.12.1" % Test,
  "com.typesafe.slick" %% "slick" % "3.6.0" cross CrossVersion.for3Use2_13,
  "org.postgresql" % "postgresql" % "42.7.3",
  "com.h2database" % "h2" % "2.3.232",
  "org.mongodb.scala" %% "mongo-scala-driver" % "5.4.0" cross CrossVersion.for3Use2_13,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.13.5" % Test,
  "io.gatling" % "gatling-test-framework" % "3.13.5" % Test,
  "org.apache.kafka" % "kafka-clients" % "4.0.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "3.7.0" cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2" cross CrossVersion.for3Use2_13,
  "com.github.danielwegener" % "logback-kafka-appender" % "0.2.0-RC2"
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