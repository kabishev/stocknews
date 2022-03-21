package com.stocknews
package article
package crud

import java.util.UUID

import skunk._
import skunk.codec.all._
import skunk.implicits._

import com.stocknews.Article
import java.time.LocalDateTime

object Statement {
  final implicit private class ArticleDataCompanionOps(private val data: Article.Data.type) {
    val codec: Codec[Article.Data] = (text ~ text ~ timestamp).gimap[Article.Data]
  }

  final implicit private class ArticleExistingCompanionOps(
      private val existing: Article.Existing.type
    ) {
    val codec: Codec[Article.Existing[UUID]] =
      (uuid ~ Article.Data.codec).gimap[Article.Existing[UUID]]
  }

  object select {
    val all: Query[Void, Article.Existing[UUID]] =
      sql"SELECT title, text, date FROM article".query(Article.Existing.codec)

    def byIds(size: Int): Query[List[UUID], Article.Existing[UUID]] =
      sql"""
        SELECT *
        FROM article
        WHERE id IN (${uuid.list(size)})
      """.query(Article.Existing.codec)
  }

  object insert {
    def many(articles: List[Article.Data]): Query[articles.type, Article.Existing[UUID]] =
      sql"""
        INSERT INTO article (title, text, date) 
        VALUES ${Article.Data.codec.values.list(articles)}
        RETURNING *
      """.query(Article.Existing.codec)
  }
}
