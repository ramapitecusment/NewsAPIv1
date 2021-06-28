package com.ramapitecusment.newsapi.services.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Articles(
    val total: Int? = 0,
    val page: Int? = 0,
    val articles: List<Article>?
): Parcelable {

    @Parcelize
    @Entity(tableName = "articles")
    data class Article(
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
        val searchTag: String?,
        val country: String?,
        var isReadLater: Int = 0
    ) : Parcelable

    @Parcelize
    @Entity(tableName = "article_remote_keys")
    data class ArticleRemoteKeys(
        @PrimaryKey val articleId: Long,
        val prevKey: Int?,
        val nextKey: Int?
    ) : Parcelable
}


