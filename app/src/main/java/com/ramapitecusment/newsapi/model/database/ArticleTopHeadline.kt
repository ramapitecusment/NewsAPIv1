package com.ramapitecusment.newsapi.model.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "top_headlines_table")
data class ArticleTopHeadline(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: String,
    val title: String,
    val url: String,
    val urlToImage: String,
    val country: String
) : Parcelable
