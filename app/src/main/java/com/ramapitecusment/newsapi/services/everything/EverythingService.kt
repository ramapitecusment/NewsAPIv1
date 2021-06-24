package com.ramapitecusment.newsapi.services.everything

import android.content.Context
import com.ramapitecusment.newsapi.common.API_KEY_VALUE
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.database.ReadLater
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.network.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers

class EverythingService(
    private val api: NewsApi,
    private val context: Context,
    private val dao: ArticleDao
) {
    fun getFromRemote(searchTag: String, page: Int): Maybe<retrofit2.Response<Response>> =
        api.getEverythingRemote(searchTag, API_KEY_VALUE, PAGE_SIZE_VALUE, page)

    fun insertAll(articles: List<ArticleEntity>): Completable = dao.insertAllArticles(articles)

    fun getAll(): Flowable<List<ArticleEntity>> = dao.getAllArticles()

    fun getArticlesBySearchTag(searchTag: String): Flowable<List<ArticleEntity>> =
        dao.getArticlesBySearchTag(searchTag)

    fun deleteAll(): Completable = dao.deleteAllArticles()

    fun update(article: ArticleEntity): Completable = dao.updateArticle(article)

    fun insert(article: ArticleEntity): Completable = dao.insertArticle(article)

    fun insertToReadLater(article: ReadLater): Completable = dao.insertReadLater(article)

    fun deleteReadLater(article: ReadLater): Completable = dao.deleteReadLater(article)

}