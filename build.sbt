name := """tobyks"""

version := "1.0"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"

resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"
libraryDependencies += "io.scalac" %% "slack-scala-bot-core" % "0.2.1"
libraryDependencies += "me.lessis" %% "hubcat" % "0.2.0-SNAPSHOT"

val akkaVersion = "2.3.12"
libraryDependencies ++=
  Seq(
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
    "com.github.scullxbones" %% "akka-persistence-mongo-rxmongo" % "0.4.1",
    "org.reactivemongo" %% "reactivemongo" % "0.11.4"
  )

enablePlugins(JavaAppPackaging)

herokuProcessTypes in Compile := Map(
 "tobyks" -> "target/universal/stage/bin/tobyks"
)
