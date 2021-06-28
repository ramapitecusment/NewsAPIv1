package com.ramapitecusment.newsapi.services.network

import com.google.gson.annotations.SerializedName
import com.ramapitecusment.newsapi.services.database.Article

data class ArticleNetwork(
    @SerializedName("author")
    val author: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("publishedAt")
    val publishedAt: String?,
    @SerializedName("source")
    val source: Source?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("urlToImage")
    val urlToImage: String?
)

data class Source(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)

data class Response(
    @SerializedName("articles")
    val articles: List<ArticleNetwork>?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("totalResults")
    val totalResults: Int?
)

fun ArticleNetwork.toArticle(searchTag: String = "NaN", country: String = "NaN"): Article =
    Article(
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        source = source?.name,
        title = title,
        url = url,
        urlToImage = urlToImage,
        searchTag = searchTag,
        country = country
    )

fun List<ArticleNetwork>.toArticle(searchTag: String, country: String): List<Article> {
    return this.map {
        it.toArticle(searchTag, country)
    }
}