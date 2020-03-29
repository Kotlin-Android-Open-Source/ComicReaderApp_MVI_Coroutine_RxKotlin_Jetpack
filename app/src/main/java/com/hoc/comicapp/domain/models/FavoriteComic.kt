package com.hoc.comicapp.domain.models

import java.util.*

data class FavoriteComic(
  val url: String,
  val title: String,
  val thumbnail: String,
  val view: String,
  val createdAt: Date?,
)