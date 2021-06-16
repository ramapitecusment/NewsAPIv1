package com.ramapitecusment.newsapi.services.readLater

import android.content.Context
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi

class ReadLaterService(
    private val api: NewsApi,
    private val context: Context,
    private val dao: ArticleDao
) {

}