package com.ramapitecusment.newsapi.services.everything

import android.content.Context
import com.ramapitecusment.newsapi.common.API_KEY_VALUE
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.network.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers

class EverythingService(
    private val api: NewsApi,
    private val context: Context,
    private val dao: ArticleDao
) {
    fun getFromRemote(searchTag: String): Maybe<retrofit2.Response<Response>> =
        api.getEverythingRemote(searchTag, API_KEY_VALUE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertAll(articles: List<ArticleEntity>): Maybe<List<Long>> =
        dao.insertAllArticles(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}