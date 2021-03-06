import sbt._

object Dependencies {
  case object com {
    case object github {
      val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.17.1"
    }
  }

  case object org {
    case object augustjune {
      val `context-applied` = "org.augustjune" %% "context-applied" % "0.1.4"
    }

    case object scalacheck {
      val scalacheck =
        "org.scalacheck" %% "scalacheck" % "1.15.4" % "test"
    }

    case object scalatest {
      val scalatest =
        "org.scalatest" %% "scalatest" % "3.2.12" % "test"
    }

    case object slf4j {
      val `slf4j-simple` =
        "org.slf4j" % "slf4j-simple" % "1.7.36"
    }

    case object http4s {
      val http4sVersion = "0.21.31"
      val `http4s-blaze-server` = "org.http4s" %% "http4s-blaze-server" % http4sVersion
      val `http4s-circe` = "org.http4s" %% "http4s-circe" % http4sVersion
      val `http4s-dsl` = "org.http4s" %% "http4s-dsl" % http4sVersion
    }

    case object tpolecat {
      val `skunk-core` =
        "org.tpolecat" %% "skunk-core" % "0.0.28"
    }

    case object typelevel {
      lazy val `cats-core` = "org.typelevel" %% "cats-core" % "2.6.1"
      lazy val `cats-effect` =
        "org.typelevel" %% "cats-effect" % "2.5.4" withSources () withJavadoc ()
      lazy val `kind-projector` = "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
    }
  }

  case object iopta {
    case object circe {
      val circeVersion = "0.14.1"
      val `circe-generic` = "io.circe" %% "circe-generic" % circeVersion
    }
  }
}
