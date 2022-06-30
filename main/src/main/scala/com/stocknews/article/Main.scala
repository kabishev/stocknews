package com.stocknews
package article

import scala.concurrent.ExecutionContext

import cats.effect._
import natchez.Trace.Implicits.noop

object Main extends App {
  val executionContext: ExecutionContext = ExecutionContext.global

  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val timer: Timer[IO] = IO.timer(executionContext)
  implicit val configService: ApplicationConfig[IO] = LiveApplicationConfigInterpreter[IO]

    Program
      .dsl[cats.effect.IO](executionContext)
      .unsafeRunSync()
}
