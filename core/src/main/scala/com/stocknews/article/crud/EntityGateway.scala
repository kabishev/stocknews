package com.stocknews
package article
package crud

trait EntityGateway[F[_], ArticleId] {
  def writeMany(articles: Vector[Article[ArticleId]]): F[Vector[Article.Existing[ArticleId]]]

  def readManyById(ids: Vector[ArticleId]): F[Vector[Article.Existing[ArticleId]]]
  def readAll: F[Vector[Article.Existing[ArticleId]]]
  // def deleteMany(articles: Vector[Article.Existing[ArticleId]]): F[Unit]
  // def deleteAll: F[Unit]
}
