name := """ambry-client-stream"""

version := "1.0"

scalaVersion := "2.11.8"

val scalaTestVersion = "3.0.1"

val akkaVersion = "2.4.14"

val akka_http_Version = "10.0.0"

val macwireV = "2.2.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion% "test",
  "com.typesafe.akka" %% "akka-http-core" % akka_http_Version,
  "com.typesafe.akka" %% "akka-http" % akka_http_Version,
  "com.typesafe.akka" %% "akka-http-testkit" % akka_http_Version,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.softwaremill.macwire" %% "macros" % macwireV % "provided",
  "com.softwaremill.macwire" %% "util" % macwireV,
  "org.mockito" % "mockito-all" % "1.10.19",
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.12",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "ch.qos.logback" % "logback-core" % "1.1.7"

)
