package com.ramapitecusment.newsapi.services.news

import com.ramapitecusment.newsapi.common.API_KEY_VALUE
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.network.Response
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

class NewsService(private val newsApi: NewsApi, private val articleDao: ArticleDao) {

    fun getEverythingRemote(searchTag: String, page: Int): Single<retrofit2.Response<Response>> =
        newsApi.getEverythingRemote(searchTag, API_KEY_VALUE, PAGE_SIZE_VALUE, page)

    fun getTopHeadlinesRemote(country: String, page: Int): Single<retrofit2.Response<Response>> =
        newsApi.getTopHeadlinesRemote(country, API_KEY_VALUE, PAGE_SIZE_VALUE, page)

    fun insertAll(articles: List<Article>): Completable = articleDao.insert(articles)

    fun insert(article: Article): Completable = articleDao.insertArticle(article)

    fun getArticlesByReadLater(): Flowable<List<Article>> = articleDao.getArticlesByReadLater()

    fun getArticlesBySearchTag(searchTag: String): Flowable<List<Article>> =
        articleDao.getArticlesBySearchTag(searchTag)

    fun getArticlesByCountry(country: String): Flowable<List<Article>> =
        articleDao.getArticlesByCountry(country)

    fun deleteAll(): Completable = articleDao.delete()

    fun delete(article: Article): Completable = articleDao.delete(article)

    fun update(article: Article): Completable = articleDao.update(article)

}