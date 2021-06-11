package com.ramapitecusment.newsapi.scenes.everything

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject


class EverythingViewModel(private val everythingService: EverythingService) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
//    val searchTag: MutableLiveData<String> = MutableLiveData<String>(QUERY_DEFAULT)
    var searchTag: PublishSubject<String> = PublishSubject.create()

    val articles: Flowable<List<ArticleEntity>?> = everythingService.getAll()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val articlesByTag: Flowable<List<ArticleEntity>> = everythingService.getArticlesBySearchTag(searchTag.value)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext { Log.d(LOG, "articlesByTag: ${searchTag.}") }

    fun getFromRemote(searchTag: String) {
        val disposable = everythingService.getFromRemote(searchTag)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(LOG, "Response: $it")
                if (it.isSuccessful) {
                    it.body()?.let { body ->
                        Log.e(LOG, "Response: ${body.articles?.size}")
                        body.articles?.toArticleEntity(searchTag)?.let { it1 -> insertAll(it1) }
                        searchTag.
                    }
                }
            }, {
                Log.e(LOG, "Error: $it")
            }, {
                Log.e(LOG, "Completed")
            })
        compositeDisposable.add(disposable)
    }

    private fun insertAll(articles: List<ArticleEntity>) {
        val disposable = everythingService.insertAll(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(LOG, "Insert success: $it")
            }, {
                Log.e(LOG, "Insert error: $it")
            }, {
                Log.e(LOG, "Insert complete")
            })
        compositeDisposable.add(disposable)
    }

    fun deleteAll() {
        val disposable = everythingService.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(LOG, "Delete success")
            }, {
                Log.e(LOG, "Delete error: $it")
            })
        compositeDisposable.add(disposable)
    }

    fun destroy() {
        // Using clear will clear all, but can accept new disposable
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        // Using dispose will clear all and set isDisposed = true, so it will not accept any new disposable
        compositeDisposable.dispose()
    }
}