package com.stocknews
package article
package response

import java.time.format.DateTimeFormatter

import io.circe._
import io.circe.generic.semiauto._

import org.http4s._
import org.http4s.circe._

import com.{ stocknews => domain }

final case class Article(
    id: String,
    title: String,
    text: String,
    date: String,
  )

object Article {
  def apply[ArticleId](
      pattern: DateTimeFormatter
    )(
      existing: domain.Article.Existing[ArticleId]
    ): Article =
    Article(
      id = existing.id.toString,
      title = existing.data.title,
      text = existing.data.text,
      date = existing.data.date.format(pattern),
    )

  implicit val encoder: Encoder[Article] = deriveEncoder

  implicit def entityEncoder[F[_]]: EntityEncoder[F, Article] = jsonEncoderOf
}
