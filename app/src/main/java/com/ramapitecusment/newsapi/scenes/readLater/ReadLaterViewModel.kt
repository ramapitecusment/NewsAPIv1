package com.ramapitecusment.newsapi.scenes.readLater

import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.services.database.ReadLater
import com.ramapitecusment.newsapi.services.database.toArticle
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.readLater.ReadLaterService
import io.reactivex.rxjava3.disposables.Disposable

class ReadLaterViewModel(
    private val readLaterService: ReadLaterService,
    private val networkService: NetworkService
) : BaseNewsViewModel() {
    var disposable: Disposable? = null

    init {
        if (networkService.isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
        } else {
            internetErrorState()
            showErrorLog("There is no Internet connection")
        }

        readLaterService.getAll().subscribeOnSingleObserveMain()
            .subscribe({
                showLog("On Next readLaterService: ${it.size}")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
                    articles.mutableValue = it.toArticle().distinct()
                    successState()
                }
            }, {
                showErrorLog("Error getArticlesBySearchTag: it")
            })
    }

    fun getFromDatabase() {
        disposable?.dispose()
        disposable = readLaterService.getAll().subscribeOnSingleObserveMain()
            .subscribe({
                showLog("On Next readLaterService: ${it.size}")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
                    articles.mutableValue = it.toArticle().distinct()
                    successState()
                }
            }, {
                showErrorLog("Error getArticlesBySearchTag: it")
            })
    }

    fun deleteAll() {
        readLaterService.deleteAll().subscribeOnIoObserveMain().subscribe(
            {
                showLog("Delete success")
                getFromDatabase()
            }, { error ->
                showErrorLog("Delete error: $error")
            }).addToSubscription()
    }

    fun delete(article: ReadLater) {
        readLaterService.deleteReadLater(article).subscribeOnIoObserveMain().subscribe({
            showLog("deleteReadLater Complete")
        }, { error ->
            showErrorLog("deleteReadLater error: $error")
        }).addToSubscription()
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}