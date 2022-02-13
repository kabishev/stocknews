package com.stocknews
package article
package crud

import cats._
import cats.data._
import cats.syntax.all._

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object Request {
  object Article {
    final case class Create(
        title: String,
        text: String,
        date: String,
      ) extends Update

    object Create {
      implicit val decoder: Decoder[Create] = deriveDecoder
      implicit def entityDecoder[F[_]: effect.Sync]: EntityDecoder[F, Create] = jsonOf
    }

    sealed abstract class Update extends Product with Serializable {
      import Update._

      final def fold[B](
          ifTitle: String => B,
          ifText: String => B,
          ifDate: String => B,
          ifAllFields: (String, String, String) => B,
        ): B = this match {
        case Title(title) => ifTitle(title)
        case Text(text) => ifText(text)
        case Date(date) => ifDate(date)
        case AllFields(title, text, date) => ifAllFields(title, text, date)
      }
    }

    object Update {
      final case class Title(title: String) extends Update
      final case class Text(text: String) extends Update
      final case class Date(date: String) extends Update

      final type AllFields = Create
      final val AllFields = Create

      implicit val decoder: Decoder[Update] = NonEmptyChain[Decoder[Update]](
        deriveDecoder[AllFields].widen,
        deriveDecoder[Title].widen,
        deriveDecoder[Text].widen,
        deriveDecoder[Date].widen,
      ).reduceLeft(_ or _)

      implicit def entityDecoder[F[_]: effect.Sync]: EntityDecoder[F, Update] = jsonOf
    }
  }
}
