import Dependencies.Libraries

ThisBuild / scalafixDependencies += Libraries.organizeImports
ThisBuild / scalaVersion := "3.3.0"

Global / semanticdbEnabled := true
Global / semanticdbVersion := scalafixSemanticdb.revision

val commonSettings = List(
  libraryDependencies ++= Seq(
    Libraries.sttp,
    Libraries.sttpCats,
    Libraries.sttpCirce,
    Libraries.circe,
    Libraries.circeGeneric,
    Libraries.spoiwo,
    Libraries.log4j,
    Libraries.toml4j,
    Libraries.catsCore,
    Libraries.catsEffect,
    Libraries.scalaTime,
    Libraries.ciris,
    Libraries.decline,
    Libraries.declineCatsEffect,
    Libraries.http4sDsl,
    Libraries.http4sServer,
    Libraries.http4sCirce,
    Libraries.doobie,
    Libraries.scalaTags,
    Libraries.doobieHikari,
    Libraries.sqliteJDB,
    Libraries.woof,
    Libraries.scalaTestDiscipline % Test,
    Libraries.scalaTestCatsEffect % Test
  )
)

lazy val root = project
  .in(file("."))
  .settings(
    name    := "sentinel",
    version := "0.6.3",
    commonSettings
  )
  .aggregate(core, gitlab, scanning, upkeep)
  .dependsOn(core, gitlab, scanning, upkeep)

lazy val core = project
  .in(file("modules/core"))
  .settings(commonSettings: _*)

lazy val gitlab = project
  .in(file("modules/gitlab"))
  .settings(commonSettings: _*)

lazy val scanning = project
  .in(file("modules/scanning"))
  .settings(commonSettings: _*)
  .dependsOn(core, gitlab)

lazy val upkeep = project
  .in(file("modules/upkeep"))
  .settings(commonSettings: _*)
  .dependsOn(core, gitlab)

enablePlugins(JavaAppPackaging)

addCommandAlias("lint", ";scalafmtAll ;scalafixAll --rules OrganizeImports")
