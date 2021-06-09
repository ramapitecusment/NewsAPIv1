package com.ramapitecusment.newsapi.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class Article(
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
    val searchTag: String,
    val isTopHeadlines: Int
)