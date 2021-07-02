package com.ramapitecusment.newsapi.services.readLater

import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

class ReadLaterService(private val newsApi: NewsApi, private val articleDao: ArticleDao) {

    fun insert(article: Article): Completable = articleDao.insertArticle(article)

    fun getArticlesByReadLater(): Flowable<List<Article>> = articleDao.getArticlesByReadLater()

    fun update(article: Article): Completable = articleDao.update(article)

    fun updateArticles(articles: List<Article>): Completable = articleDao.updateArticles(articles)

    fun delete(article: Article): Completable = articleDao.delete(article)

}