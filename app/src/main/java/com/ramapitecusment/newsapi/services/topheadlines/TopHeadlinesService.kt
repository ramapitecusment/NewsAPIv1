package com.ramapitecusment.newsapi.services.topheadlines

import android.content.Context
import com.ramapitecusment.newsapi.common.API_KEY_VALUE
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ArticleTopHeadline
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.network.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers

class TopHeadlinesService(
    private val api: NewsApi,
    private val context: Context,
    private val dao: ArticleDao
) {

    fun getFromRemote(country: String, page: Int): Maybe<retrofit2.Response<Response>> =
        api.getTopHeadlinesRemote(country, API_KEY_VALUE, PAGE_SIZE_VALUE, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertAll(articles: List<ArticleTopHeadline>): Maybe<List<Long>> =
        dao.insertAllTopHeadlines(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getAll(): Flowable<List<ArticleTopHeadline>> =
        dao.getAllTopHeadlines()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getAllByCountry(country: String): Flowable<List<ArticleTopHeadline>> =
        dao.getTopHeadlinesByCountry(country)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun deleteAll(): Completable =
        dao.deleteAllTopHeadlines()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}