package com.stocknews
package article

import cats._

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.blaze.server._

import scala.concurrent.ExecutionContext

trait Server[F[_]] {
  def serve: F[Unit]
}

object Server {
  def dsl[F[_]: effect.ConcurrentEffect: effect.Timer](
      config: ApiConfig,
      executionContext: ExecutionContext,
    )(
      httpApp: HttpApp[F]
    ): F[Server[F]] =
    effect.ConcurrentEffect[F].delay {
      new Server[F] {
        override val serve: F[Unit] =
          BlazeServerBuilder(executionContext)
            .bindHttp(config.port, config.host)
            .withHttpApp(httpApp)
            .serve
            .compile
            .drain
      }
    }
}
