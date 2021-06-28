package com.ramapitecusment.newsapi.services.network

import com.google.gson.annotations.SerializedName
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.Articles
import java.text.SimpleDateFormat
import java.util.*

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

class ArticleMapper {
    fun transform(
        response: Response,
        searchTag: String = "",
        country: String = ""
    ): Articles {
        return with(response) {
            Articles(
                total = totalResults,
                page = totalResults?.div(PAGE_SIZE_VALUE),
                articles = articles?.map {
                    Articles.Article(
                        id = 0,
                        author = it.author,
                        content = it.content,
                        description = it.description,
                        publishedAt = it.publishedAt?.let { date ->
                            if (date.isNotEmpty()) SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss",
                                Locale.ROOT
                            ).parse(date)
                            else null
                        }.toString(),
                        source = it.source?.id,
                        title = it.title,
                        url = it.url,
                        urlToImage = it.urlToImage,
                        searchTag = searchTag,
                        country = country,
                        isReadLater = 0
                    )
                }
            )
        }
    }
}



fun ArticleNetwork.toArticle(searchTag: String = "", country: String = ""): Articles.Article =
    Articles.Article(
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

fun List<ArticleNetwork>.toArticle(
    searchTag: String = "",
    country: String = ""
): List<Articles.Article> {
    return this.map {
        it.toArticle(searchTag, country)
    }
}