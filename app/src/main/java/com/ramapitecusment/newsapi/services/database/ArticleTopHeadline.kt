package com.ramapitecusment.newsapi.services.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "top_headlines_table")
data class ArticleTopHeadline(
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
    val country: String?
) : Parcelable

fun ArticleTopHeadline.toArticle(): Article = Article(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage
)

fun List<ArticleTopHeadline>.toArticle(): List<Article> {
    return this.map {
        it.toArticle()
    }
}
