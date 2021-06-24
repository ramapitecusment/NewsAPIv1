package com.ramapitecusment.newsapi.services.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "news_table")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    var isReadLater: Int = 0,
    val searchTag: String?
) : Parcelable

fun ArticleEntity.toArticle(): Article = Article(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage,
    isReadLater = isReadLater,
    country = "NaN",
    searchTag = searchTag
)

fun List<ArticleEntity>.toArticle(): List<Article> {
    return this.map {
        it.toArticle()
    }
}

fun ArticleEntity.toReadLaterArticle(): ReadLater = ReadLater(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage,
    isReadLater = isReadLater
)

fun List<ArticleEntity>.toReadLaterArticle(): List<ReadLater> {
    return this.map {
        it.toReadLaterArticle()
    }
}

