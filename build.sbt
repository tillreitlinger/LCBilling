name := "LCBilling"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

val AkkaVersion = "2.6.10"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test

libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0"
