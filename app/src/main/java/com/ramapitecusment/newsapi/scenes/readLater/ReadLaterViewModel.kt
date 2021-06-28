package com.ramapitecusment.newsapi.scenes.readLater

import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ReadLater
import com.ramapitecusment.newsapi.services.database.toArticle
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.disposables.Disposable

class ReadLaterViewModel(
    private val newsService: NewsService,
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

        newsService.getAll().subscribeOnSingleObserveMain()
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
        disposable = newsService.getAll().subscribeOnSingleObserveMain()
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

    fun deleteAllClicked() {
        newsService.deleteAll().subscribeOnIoObserveMain().subscribe(
            {
                showLog("Delete success")
                getFromDatabase()
            }, { error ->
                showErrorLog("Delete error: $error")
            }).addToSubscription()
    }

    fun delete(article: Article) {
        newsService.deleteReadLater(article).subscribeOnIoObserveMain().subscribe({
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