package com.ramapitecusment.newsapi.scenes.everything

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class EverythingViewModel(private val everythingService: EverythingService) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun getFromRemote(searchTag: String) {
        val result = everythingService.getFromRemote(searchTag)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(LOG, "Response: $it")
                if (it.isSuccessful) {
                    it.body()?.let { body ->
                        Log.e(LOG, "Response: ${body.articles}")
                        insertAll(body.articles.toArticleEntity(searchTag))
                    }
                }
            }, {
                Log.e(LOG, "Error: $it")
            }, {
                Log.e(LOG, "Completed")
            })
        compositeDisposable.add(result)
    }

    private fun insertAll(articles: List<ArticleEntity>) {
        everythingService.insertAll(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(LOG, "Database success: $it")
            }, {
                Log.e(LOG, "Database error: $it")
            }, {
                Log.e(LOG, "Database complete")
            })
    }

    fun destroy() {
        compositeDisposable.clear()
    }
}