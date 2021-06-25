package com.ramapitecusment.newsapi.services.readLater

import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ReadLater
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

class ReadLaterService(
    private val articleDao: ArticleDao
) {

    fun getAll(): Flowable<List<ReadLater>> = articleDao.getAllReadLater()

    fun deleteAll(): Completable = articleDao.deleteAllReadLater()

    fun insertToReadLater(article: ReadLater): Completable = articleDao.insertReadLater(article)

    fun deleteReadLater(article: ReadLater): Completable = articleDao.deleteReadLater(article)

}