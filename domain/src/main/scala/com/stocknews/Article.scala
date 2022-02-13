package com.stocknews

import java.time.LocalDateTime

sealed abstract class Article[+ArticleId] extends Product with Serializable {
  def title: String
  def text: String
  def date: LocalDateTime

  def withUpdateTitle(newTitle: String): Article[ArticleId]
  def withUpdateText(newText: String): Article[ArticleId]
  def withUpdateDate(newDate: LocalDateTime): Article[ArticleId]
}

object Article {
  final case class Data(
      title: String,
      text: String,
      date: LocalDateTime,
    ) extends Article[Nothing] {
    override def withUpdateTitle(newTitle: String): Data = copy(title = newTitle)

    override def withUpdateText(newText: String): Data = copy(text = newText)

    override def withUpdateDate(newDate: LocalDateTime): Data = copy(date = newDate)
  }

  final case class Existing[ArticleId](id: String, data: Data) extends Article[ArticleId] {
    override def withUpdateTitle(newTitle: String): Existing[ArticleId] = copy(data = data.withUpdateTitle(newTitle))

    override def withUpdateText(newText: String): Existing[ArticleId] = copy(data = data.withUpdateText(newText))

    override def withUpdateDate(newDate: LocalDateTime): Existing[ArticleId] = copy(data = data.withUpdateDate(newDate))

    override def title: String = data.title

    override def text: String = data.text

    override def date: LocalDateTime = data.date
  }
}
