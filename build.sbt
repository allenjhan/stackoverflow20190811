name := "stackoverflow20190811"

version := "0.1"

scalaVersion := "2.13.0"

lazy val akkaVersion = "2.5.24"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
)

