package com.ramapitecusment.newsapi.services.readLater

import android.content.Context
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ReadLater
import com.ramapitecusment.newsapi.services.network.NewsApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

class ReadLaterService(
    private val api: NewsApi,
    private val context: Context,
    private val dao: ArticleDao
) {

    fun getAll(): Flowable<List<ReadLater>> = dao.getAllReadLater()

    fun deleteAll(): Completable = dao.deleteAllReadLater()

    fun deleteReadLater(article: ReadLater): Completable = dao.deleteReadLater(article)
}