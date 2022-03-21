package com.stocknews
package article
package crud

import java.util.UUID

import cats._
import cats.implicits._

import com.stocknews.Article.Data

object PostgresEntityGateway {
  def dsl[F[_]: effect.Sync](
      resource: effect.Resource[F, skunk.Session[F]]
    ): F[EntityGateway[F, UUID]] = effect.Sync[F].delay {
    new EntityGateway[F, UUID] {
      override def writeMany(articles: Vector[Article[UUID]]): F[Vector[Article.Existing[UUID]]] =
        for {
          newItems <- insertMany(articles.collect {
            case data @ Article.Data(_, _, _) => data
          }.toList)
        } yield newItems

      private def insertMany(data: List[Article.Data]): F[Vector[Article.Existing[UUID]]] =
        resource.use { session =>
          session
            .prepare(Statement.insert.many(data))
            .use(_.stream(data, ChunkSizeInBytes).compile.toVector)
        }

      override def readManyById(ids: Vector[UUID]): F[Vector[Article.Existing[UUID]]] =
        resource.use { session =>
          session
            .prepare(Statement.select.byIds(ids.size))
            .use(_.stream(ids.to(List), ChunkSizeInBytes).compile.toVector)
        }

      override def readAll: F[Vector[Article.Existing[UUID]]] =
        resource.use { session =>
          session
            .execute(Statement.select.all)
            .map(_.to(Vector))
        }

      override def deleteMany(articles: Vector[Article.Existing[UUID]]): F[Unit] = ???
      override def deleteAll: F[Unit] = ???
    }
  }

  private val ChunkSizeInBytes: Int = 1024
}
