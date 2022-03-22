package com.stocknews
package article
package crud

import cats._
import cats.implicits._

trait Boundary[F[_], ArticleId] {
  def createOne(article: Article.Data): F[Article.Existing[ArticleId]]
  def createMany(todos: Vector[Article.Data]): F[Vector[Article.Existing[ArticleId]]]

  def readOneById(id: ArticleId): F[Option[Article.Existing[ArticleId]]]
  def readManyById(ids: Vector[ArticleId]): F[Vector[Article.Existing[ArticleId]]]
  def readAll: F[Vector[Article.Existing[ArticleId]]]

  def updateOne(article: Article.Existing[ArticleId]): F[Article.Existing[ArticleId]]
  def updateMany(
      articles: Vector[Article.Existing[ArticleId]]
    ): F[Vector[Article.Existing[ArticleId]]]

  // def deleteOne(article: Article.Existing[ArticleId]): F[Unit]
  // def deleteMany(Articles: Vector[Article.Existing[ArticleId]]): F[Unit]
  // def deleteAll: F[Unit]
}

object Boundary {
  def dsl[F[_]: Applicative, ArticleId](
      gateway: EntityGateway[F, ArticleId]
    ): Boundary[F, ArticleId] =
    new Boundary[F, ArticleId] {
      override def createOne(article: Article.Data): F[Article.Existing[ArticleId]] =
        createMany(Vector(article)).map(_.head)

      override def createMany(
          articles: Vector[Article.Data]
        ): F[Vector[Article.Existing[ArticleId]]] =
        writeMany(articles)

      private def writeMany[T <: Article[ArticleId]](
          articles: Vector[T]
        ): F[Vector[Article.Existing[ArticleId]]] =
        gateway.writeMany(articles)

      override def readOneById(id: ArticleId): F[Option[Article.Existing[ArticleId]]] =
        readManyById(Vector(id)).map(_.headOption)

      override def readManyById(ids: Vector[ArticleId]): F[Vector[Article.Existing[ArticleId]]] =
        gateway.readManyById(ids)

      override val readAll: F[Vector[Article.Existing[ArticleId]]] =
        gateway.readAll

      override def updateOne(article: Article.Existing[ArticleId]): F[Article.Existing[ArticleId]] =
        updateMany(Vector(article)).map(_.head)

      override def updateMany(
          articles: Vector[Article.Existing[ArticleId]]
        ): F[Vector[Article.Existing[ArticleId]]] =
        writeMany(articles)

      // override def deleteOne(article: Article.Existing[ArticleId]): F[Unit] =
      //   deleteMany(Vector(article))

      // override def deleteMany(articles: Vector[Article.Existing[ArticleId]]): F[Unit] =
      //   gateway.deleteMany(articles)

      // override val deleteAll: F[Unit] =
      //   gateway.deleteAll
    }
}
