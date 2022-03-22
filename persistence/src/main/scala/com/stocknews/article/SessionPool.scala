package com.stocknews
package article

import cats.effect._
import cats.syntax.option._

import skunk._

object SessionPool {
  def dsl[F[_]: Concurrent: ContextShift: natchez.Trace]: SessionPool[F] =
    Session.pooled(
      host = "localhost",
      port = 5432,
      user = "user",
      password = "password".some,
      database = "stocknews",
      max = 10,
      debug = false
    )
}
