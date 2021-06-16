package com.ramapitecusment.newsapi.services.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "read_later_table")
data class ReadLater(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: String,
    val title: String,
    val url: String,
    val urlToImage: String
) : Parcelable

fun ReadLater.toArticle(): Article = Article(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage
)

fun List<ReadLater>.toArticle(): List<Article> {
    return this.map {
        it.toArticle()
    }
}
