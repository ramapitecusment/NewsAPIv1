package com.ramapitecusment.newsapi.services.network

import com.ramapitecusment.newsapi.common.AppConsts.Companion.DATE_TIME_PATTERN
import com.ramapitecusment.newsapi.common.AppConsts.Companion.EMPTY_STRING
import com.ramapitecusment.newsapi.services.database.Article
import java.text.SimpleDateFormat
import java.util.*

data class ArticleNetwork(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)

data class Source(
    val id: String?,
    val name: String?
)

data class Response(
    val articles: List<ArticleNetwork>?,
    val status: String?,
    val totalResults: Int?
)

fun ArticleNetwork.toArticle(
    searchTag: String = EMPTY_STRING,
    country: String = EMPTY_STRING
): Article = Article(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt?.let { date ->
        if (date.isNotEmpty()) SimpleDateFormat(
            DATE_TIME_PATTERN,
            Locale.ROOT
        ).parse(date)
        else null
    }.toString(),
    source = source?.name,
    title = title,
    url = url,
    urlToImage = urlToImage,
    searchTag = searchTag,
    country = country,
    isReadLater = 0
)

fun List<ArticleNetwork>.toArticle(
    searchTag: String = EMPTY_STRING,
    country: String = EMPTY_STRING
): List<Article> {
    return this.map {
        it.toArticle(searchTag, country)
    }
}