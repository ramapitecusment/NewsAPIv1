package com.ramapitecusment.newsapi.services.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    var id: Int = 0,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    val searchTag: String?,
    val country: String?,
    var isReadLater: Int = 0
) : Parcelable

fun Article.toReadLaterArticle(): ReadLater = ReadLater(
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

fun List<Article>.toReadLaterArticle(): List<ReadLater> {
    return this.map {
        it.toReadLaterArticle()
    }
}

fun Article.toArticleEntity(): ArticleEntity = ArticleEntity(
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
    searchTag = searchTag
)

fun List<Article>.toArticleEntity(): List<ArticleEntity> {
    return this.map {
        it.toArticleEntity()
    }
}

fun Article.toTopHeadlines(): ArticleTopHeadline = ArticleTopHeadline(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage,
    isReadLater = isReadLater,
    country = country
)

fun List<Article>.toTopHeadlines(): List<ArticleTopHeadline> {
    return this.map {
        it.toTopHeadlines()
    }
}