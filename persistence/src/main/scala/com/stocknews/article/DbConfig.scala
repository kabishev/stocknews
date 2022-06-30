package com.stocknews.article

final case class DbConfig(
    name: String,
    host: String,
    port: Int,
    user: String,
    password: String,
  )
