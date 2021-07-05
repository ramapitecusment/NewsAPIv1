package com.ramapitecusment.newsapi.services.everything

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

class EverythingService(private val newsApi: NewsApi, private val articleDao: ArticleDao) {

    fun getEverythingRemote(searchTag: String, page: Int): Single<retrofit2.Response<Response>> =
        newsApi.getEverythingRemote(searchTag, API_KEY_VALUE, PAGE_SIZE_VALUE, page)

    fun insertAll(articles: List<Article>): Completable = articleDao.insert(articles)

    fun insert(article: Article): Completable = articleDao.insertArticle(article)

    fun getArticlesBySearchTag(searchTag: String): Flowable<List<Article>> =
        articleDao.getArticlesBySearchTag(searchTag)

    fun update(article: Article): Completable = articleDao.update(article)

    fun delete(article: Article): Completable = articleDao.delete(article)

    fun deleteAllBySearchTag(searchTag: String = EMPTY_STRING): Completable =
        articleDao.deleteAllBySearchTag(searchTag)

}