package com.ramapitecusment.newsapi.services.newsdetails

import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi

class NewsDetailsService(
    private val newsApi: NewsApi,
    private val articleDao: ArticleDao
) {
}