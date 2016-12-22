import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences.{DoubleIndentClassDeclaration, AlignSingleLineCaseStatements}

name := """ambry-client-akkahttp"""

version := "1.0.3-SNAPSHOT"

organization := "io.outofaxis"

scalaVersion := "2.11.8"

val scalaTestVersion = "3.0.1"

val akkaVersion = "2.4.14"

val akka_http_Version = "10.0.0"



libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-core" % akka_http_Version,
  "com.typesafe.akka" %% "akka-http" % akka_http_Version,
  "com.typesafe.akka" %% "akka-http-testkit" % akka_http_Version,
  "org.mockito" % "mockito-all" % "1.10.19",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.12",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "ch.qos.logback" % "logback-core" % "1.1.7",
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",
  "org.mockito" % "mockito-all" % "1.10.19",
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

)

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"


scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Ydead-code",
  "-Ywarn-numeric-widen",
  "-Xfatal-warnings",
  "-encoding",
  "UTF-8"
)


scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  //  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

//Configures sbt to work without a network connection where possible
offline := true

checksums in update := Nil

sources in doc in Compile := Nil

testOptions in Test := Nil

fork in run := true

logBuffered in Test := false

updateOptions := updateOptions.value.withCachedResolution(true)

parallelExecution in Test := false

publishTo := {
  val nexus = "http://pixelart.ge:8081/nexus/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "content/repositories/releases")
}

credentials += Credentials("Sonatype Nexus Repository Manager", "pixelart.ge", "admin", "F9bz4Nx3rwul")

