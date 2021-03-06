package com.ramapitecusment.newsapi.services.everything

import android.content.Context
import com.ramapitecusment.newsapi.common.API_KEY_VALUE
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ArticleEntity
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertAll(articles: List<ArticleEntity>): Maybe<List<Long>> =
        dao.insertAllArticles(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getAll(): Flowable<List<ArticleEntity>> =
        dao.getAllArticles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getArticlesBySearchTag(searchTag: String): Flowable<List<ArticleEntity>> =
        dao.getArticlesBySearchTag(searchTag)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun deleteAll(): Completable =
        dao.deleteAllArticles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}