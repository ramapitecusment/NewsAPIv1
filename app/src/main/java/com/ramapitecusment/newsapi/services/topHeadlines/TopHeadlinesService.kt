package com.ramapitecusment.newsapi.services.topHeadlines

import com.ramapitecusment.newsapi.common.API_KEY_VALUE
import com.ramapitecusment.newsapi.common.AppConsts.Companion.EMPTY_STRING
import com.ramapitecusment.newsapi.common.AppConsts.Companion.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.network.Response
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class TopHeadlinesService(private val newsApi: NewsApi, private val articleDao: ArticleDao) {

    fun getTopHeadlinesRemote(country: String, page: Int): Single<retrofit2.Response<Response>> =
        newsApi.getTopHeadlinesRemote(country, API_KEY_VALUE, PAGE_SIZE_VALUE, page)

    fun insertAll(articles: List<Article>): Completable = articleDao.insert(articles)

    fun insert(article: Article): Completable = articleDao.insertArticle(article)

    fun getArticlesByCountry(country: String): Flowable<List<Article>> =
        articleDao.getArticlesByCountry(country)

    fun update(article: Article): Completable = articleDao.update(article)

    fun delete(article: Article): Completable = articleDao.delete(article)

    fun deleteAllByCountry(country: String = EMPTY_STRING): Completable =
        articleDao.deleteAllByCountry(country)

}