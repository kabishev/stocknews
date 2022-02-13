package com.stocknews
package article
package crud

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.chaining._

import cats._
import cats.data._
import cats.syntax.all._

import io.circe._
import io.circe.syntax._

import org.http4s.{ HttpRoutes, Response }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

object Controller {
  def dsl[F[_]: effect.Sync, ArticleId](
      pattern: DateTimeFormatter,
      boundary: Boundary[F, ArticleId],
    )(implicit
      parse: Parse[String, ArticleId]
    ): F[Controller[F]] =
    effect.Sync[F].delay {
      new Controller[F] with Http4sDsl[F] {
        override def routes: HttpRoutes[F] = Router {
          "articles" -> HttpRoutes.of {
            case r @ POST -> Root =>
              r.as[Request.Article.Create].flatMap(create)

            case r @ PUT -> Root / id =>
              r.as[Request.Article.Update].flatMap(update(id))

            case GET -> Root => showAll
            case GET -> Root / id => searchById(id)

            case DELETE -> Root => deleteAll
            case DELETE -> Root / id => delete(id)
          }
        }

        private def create(payload: Request.Article.Create): F[Response[F]] =
          withDatePromt(payload.date) { date =>
            boundary
              .createOne(Article.Data(payload.title, payload.text, date))
              .map(response.Article(pattern))
              .map(_.asJson)
              .flatMap(Created(_))
          }

        private def withDatePromt(
            deadline: String
          )(
            onSuccess: LocalDateTime => F[Response[F]]
          ): F[Response[F]] =
          toLocalDateTime(deadline).fold(BadRequest(_), onSuccess)

        private def toLocalDateTime(input: String): Either[String, LocalDateTime] = {
          val formatter = DateTimeFormatter.ofPattern(DeadlinePromptPattern)
          val trimmedInput: String = input.trim

          Either
            .catchNonFatal(LocalDateTime.parse(trimmedInput, formatter))
            .leftMap { _ =>
              s"$trimmedInput does not match the required format $DeadlinePromptPattern."
            }
        }

        private val DeadlinePromptPattern: String = "yyyy-M-d H:m"

        private def update(id: String)(payload: Request.Article.Update): F[Response[F]] =
          payload.fold(updateTitle(id), updateText(id), updateDate(id), updateAllFields(id))

        private def updateTitle(id: String)(title: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { article =>
              boundary
                .updateOne(article.withUpdateTitle(title))
                .map(response.Article(pattern))
                .map(_.asJson)
                .flatMap(Ok(_))
            }
          }

        private def updateText(id: String)(text: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { article =>
              boundary
                .updateOne(article.withUpdateText(text))
                .map(response.Article(pattern))
                .map(_.asJson)
                .flatMap(Ok(_))
            }
          }

        private def updateDate(id: String)(date: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withDatePromt(date) { date =>
              withReadOne(id) { article =>
                boundary
                  .updateOne(article.withUpdateDate(date))
                  .map(response.Article(pattern))
                  .map(_.asJson)
                  .flatMap(Ok(_))
              }
            }
          }

        private def updateAllFields(
            id: String
          )(
            title: String,
            text: String,
            date: String,
          ): F[Response[F]] =
          withIdPrompt(id) { id =>
            withDatePromt(date) { date =>
              withReadOne(id) { article =>
                boundary
                  .updateOne(
                    article
                      .withUpdateTitle(title)
                      .withUpdateText(text)
                      .withUpdateDate(date)
                  )
                  .map(response.Article(pattern))
                  .map(_.asJson)
                  .flatMap(Ok(_))
              }
            }
          }
        private def withIdPrompt(
            id: String
          )(
            onValidId: ArticleId => F[Response[F]]
          ): F[Response[F]] =
          toId(id).fold(BadRequest(_), onValidId)

        private def toId(userInput: String): Either[String, ArticleId] =
          parse(userInput).leftMap(_.getMessage)

        private def withReadOne(
            id: ArticleId
          )(
            onFound: Article.Existing[ArticleId] => F[Response[F]]
          ): F[Response[F]] =
          boundary
            .readOneById(id)
            .flatMap(_.fold(displayNoTodosFoundMessage)(onFound))

        private val displayNoTodosFoundMessage: F[Response[F]] =
          NotFound("No todos found!")

        private def showAll: F[Response[F]] =
          boundary
            .readAll
            .flatMap { articles =>
              articles
                .sortBy(_.date)
                .map(response.Article(pattern))
                .asJson
                .pipe(Ok(_))
            }

        private def searchById(id: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { article =>
              article
                .pipe(response.Article(pattern))
                .pipe(_.asJson)
                .pipe(Ok(_))
            }
          }

        private def deleteAll: F[Response[F]] =
          boundary.deleteAll >> NoContent()

        private def delete(id: String): F[Response[F]] =
          withIdPrompt(id) { id =>
            withReadOne(id) { article =>
              boundary.deleteOne(article) >> NoContent()
            }
          }
      }
    }
}
