package com.ramapitecusment.newsapi.model.database

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
