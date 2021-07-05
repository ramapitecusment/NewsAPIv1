package com.ramapitecusment.newsapi.services.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ramapitecusment.newsapi.common.AppConsts.Companion.ARTICLES_TABLE
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = ARTICLES_TABLE)
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
