package com.ramapitecusment.newsapi.services.news

import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi
import io.reactivex.rxjava3.core.Completable

class NewsService(private val newsApi: NewsApi, private val articleDao: ArticleDao) {

    fun insert(article: Article): Completable = articleDao.insertArticle(article)

    fun update(article: Article): Completable = articleDao.update(article)

    fun delete(article: Article): Completable = articleDao.delete(article)

}