val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dependency-checker",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.7.0",
      "com.lihaoyi" %% "upickle" % "1.6.0",
      "com.norbitltd" %% "spoiwo" % "2.2.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.17.2",
      "com.moandjiezana.toml" % "toml4j" % "0.7.2",
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-effect" % "3.4.1",
      "org.legogroup" %% "woof-core" % "0.4.7",
      "org.typelevel" %% "discipline-scalatest" % "2.1.5" % Test
    )
  )

enablePlugins(JavaAppPackaging)
