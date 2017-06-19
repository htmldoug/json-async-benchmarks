organization in ThisBuild := "com.rallyhealth"

lazy val bench = project
  .enablePlugins(JmhPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % "2.9.0.pr4",
      "de.undercouch" % "actson" % "1.2.0"
    )
  )
