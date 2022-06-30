package com.stocknews
package article

import java.time.format.DateTimeFormatter

import scala.concurrent._

import cats.effect._
import cats.syntax.all._

object Program {
  def dsl[F[_]: ConcurrentEffect: ContextShift: Timer: natchez.Trace: ApplicationConfig](
      executionContext: ExecutionContext
    ): F[Unit] =
    ApplicationConfig[F].config.flatMap { config =>
      SessionPool.dsl(config.db).use { resource =>
        for {
          _ <- ConcurrentEffect[F].delay {
            println(Console.GREEN + config + Console.RESET)
          }
          controller <- crud.DependencyGraph.dsl(Pattern, resource)
          server <- Server.dsl(config.api, executionContext) {
            HttpApp.dsl(controller)
          }
          _ <- server.serve
        } yield ()
      }
    }

  private val Pattern =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm")
}
