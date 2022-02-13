import sbt._

object Dependencies {
  case object org {
    case object augustjune {
      val `context-applied` = "org.augustjune" %% "context-applied" % "0.1.4"
    }
    case object scalatest {
      lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"
    }

    case object typelevel {
      lazy val `cats-core` = "org.typelevel" %% "cats-core" % "2.6.1"
      lazy val `cats-effect` = "org.typelevel" %% "cats-effect" % "2.5.4" withSources() withJavadoc()
    }

    case object http4s {
      val http4sVersion = "0.21.31"
      val `http4s-blaze-server` = "org.http4s" %% "http4s-blaze-server" % http4sVersion
      val `http4s-circe` = "org.http4s" %% "http4s-circe" % http4sVersion
      val `http4s-dsl` = "org.http4s" %% "http4s-dsl" % http4sVersion
    }
  }

  case object iopta {
    case object circe {
      val circeVersion = "0.14.1"
      val `circe-generic` = "io.circe" %% "circe-generic" % circeVersion
    }
  }
}
