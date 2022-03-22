import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.stocknews"
ThisBuild / organizationName := "stocknews"

val Cctt: String = "compile->compile;test->test"

lazy val stocknews =
  project in file(".") aggregate (util, domain, core, `delivery-http4s`, persistence, main)

lazy val util = project
  .in(file("util"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      org.typelevel.`cats-core`
    )
  )

lazy val domain = project
  .in(file("domain"))
  .settings(commonSettings)

lazy val core = project
  .in(file("core"))
  .dependsOn(domain % Cctt)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      org.typelevel.`cats-core`
    )
  )

lazy val `delivery-http4s` = project
  .in(file("delivery-http4s"))
  .dependsOn(core % Cctt)
  .dependsOn(util % Cctt)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      iopta.circe.`circe-generic`,
      org.http4s.`http4s-blaze-server`,
      org.http4s.`http4s-circe`,
      org.http4s.`http4s-dsl`,
      org.typelevel.`cats-effect`,
    )
  )

lazy val persistence = project
  .in(file("persistence"))
  .dependsOn(domain % Cctt)
  .dependsOn(core % Cctt)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      org.typelevel.`cats-effect`,
      org.tpolecat.`skunk-core`,
    )
  )

lazy val main = project
  .in(file("main"))
  .dependsOn(domain % Cctt)
  .dependsOn(`delivery-http4s` % Cctt)
  .dependsOn(persistence % Cctt)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      org.typelevel.`cats-core`,
      org.typelevel.`cats-effect`,
      org.slf4j.`slf4j-simple`
    )
  )

lazy val commonSettings = Seq(
  // addCompilerPlugin(org.augustjune.`context-applied`),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings",
  ),
  Test / console / scalacOptions :=
    (Compile / console / scalacOptions).value,
)
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
