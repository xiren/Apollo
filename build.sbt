name := """Apollo"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.apache.spark" %% "spark-core" % "1.6.1" ,
  "org.apache.spark" %% "spark-mllib" % "1.6.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
