
lazy val commonSettings = Seq(
  organization := "com.waywardcode",
  version := "1.0",
  scalaVersion := "2.11.8"
)


lazy val seq_diagrams = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "sequence diagrams"
  )
