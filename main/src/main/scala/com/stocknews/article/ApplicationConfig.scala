package com.stocknews
package article

import scala.concurrent.ExecutionContext

import cats.MonadError

import pureconfig._
import pureconfig.generic.auto._

final case class Config(
    api: ApiConfig,
    db: DbConfig,
  )

trait ApplicationConfig[F[_]] {
  def config: F[Config]
}

object ApplicationConfig {
  def apply[F[_]](implicit F: ApplicationConfig[F]): F.type = F
}

case class LiveApplicationConfigInterpreter[F[_]: MonadError[*[_], Throwable]]() extends ApplicationConfig[F] {
  override val config: F[Config] = {

    /** We can wrap it in an either, if we do that then we don't
      * really need any other typeclasses, we can also say that
      * we expect the result to be mapped to a monadError. That
      * means that the F[_] used, should satisfy the MonadError
      * constraint.
      */
    val pp = ConfigSource
      .file("app.conf")
      .optional
      .withFallback(ConfigSource.default)
      .load[Config]
      .left
      .map(pureconfig.error.ConfigReaderException.apply)

    MonadError[F, Throwable].fromEither(pp)
  }
}
