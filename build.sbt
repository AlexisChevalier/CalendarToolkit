name := "CalendarToolkit"

version := "0.1"

scalaVersion := "2.12.4"


libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.7.0",
  "joda-time" % "joda-time" % "2.9.9",
  "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.8",
  "com.sksamuel.scrimage" %% "scrimage-io-extra" % "2.1.8",
  "com.sksamuel.scrimage" %% "scrimage-filters" % "2.1.8"
)