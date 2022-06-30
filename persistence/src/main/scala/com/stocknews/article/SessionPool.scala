package com.stocknews
package article

import cats.effect._
import cats.syntax.option._

import skunk._

object SessionPool {
  def dsl[F[_]: Concurrent: ContextShift: natchez.Trace](config: DbConfig): SessionPool[F] =
    Session.pooled(
      host = config.host,
      port = config.port,
      user = config.user,
      password = config.password.some,
      database = config.name,
      max = 10,
      debug = false
    )
}
