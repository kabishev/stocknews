package com.stocknews
package article
package crud

import cats._

import org.scalatest._
import propspec._
import matchers._

import org.scalacheck._
import Arbitrary.arbitrary
import Prop.forAll
import org.scalatest.Assertions._

import java.time.LocalDateTime

final class BoundarySuite extends AnyPropSpec with should.Matchers {
  import BoundarySuite._

  private type F[A] = Id[A]

  val dataGen: Gen[Article.Data] = for {
    title <- arbitrary[String]
    text <- arbitrary[String]
    date <- arbitrary[LocalDateTime]
  } yield Article.Data(title, text, date)

  implicit val arbitraryData: Arbitrary[Article.Data] = Arbitrary(dataGen)

  property("text should be trimmed") {
    val entityGateway: EntityGateway[F, Unit] =
      new FakeEntityGateway[F, Unit] {
        override def writeMany(
            articles: Vector[Article[Unit]]
          ): F[Vector[Article.Existing[Unit]]] =
          articles.map {
            case data: Article.Data => Article.Existing((), data)
            case existing: Article.Existing[Unit] => existing
          }
      }

    val boundary: Boundary[F, Unit] =
      Boundary.dsl(entityGateway)

    forAll { (data: Article.Data) =>
      boundary.createOne(data).text == data.text.trim
    }
  }
}

object BoundarySuite {
  private class FakeEntityGateway[F[_], ArticleId] extends EntityGateway[F, ArticleId] {
    override def writeMany(
        articles: Vector[Article[ArticleId]]
      ): F[Vector[Article.Existing[ArticleId]]] = ???
    override def readManyById(ids: Vector[ArticleId]): F[Vector[Article.Existing[ArticleId]]] = ???
    override def readAll: F[Vector[Article.Existing[ArticleId]]] = ???
  }
}
