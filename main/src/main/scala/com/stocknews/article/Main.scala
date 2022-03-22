package com.stocknews
package article

import cats.effect._

import scala.concurrent.ExecutionContext

import natchez.Trace.Implicits.noop

object Main extends App {
  val executionContext: ExecutionContext = ExecutionContext.global

  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)

  implicit val timer: Timer[IO] = IO.timer(executionContext)

  Program
    .dsl[cats.effect.IO](executionContext)
    .unsafeRunSync()
}
