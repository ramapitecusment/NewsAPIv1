package com.ramapitecusment.newsapi.services.network

import com.google.gson.annotations.SerializedName
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.database.ArticleTopHeadline

data class Article(
    @SerializedName("author")
    val author: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("source")
    val source: Source,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val urlToImage: String
)

data class Source(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

data class Response(
    @SerializedName("articles")
    val articles: List<Article>,
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int
)

fun Article.toArticleEntity(searchTag: String): ArticleEntity = ArticleEntity(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source.name,
    title = title,
    url = url,
    urlToImage = urlToImage,
    searchTag = searchTag
)

fun List<Article>.toArticleEntity(searchTag: String): List<ArticleEntity> {
    return this.map {
        it.toArticleEntity(searchTag)
    }
}

fun Article.toArticleTopHeadline(country: String): ArticleTopHeadline = ArticleTopHeadline(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source.name,
    title = title,
    url = url,
    urlToImage = urlToImage,
    country = country
)

fun List<Article>.toArticleTopHeadline(country: String): List<ArticleEntity> {
    return this.map {
        it.toArticleEntity(country)
    }
}