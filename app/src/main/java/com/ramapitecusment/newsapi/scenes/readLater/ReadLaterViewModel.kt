package com.ramapitecusment.newsapi.scenes.readLater

import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.RxPagingViewModel
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.readLater.ReadLaterService

class ReadLaterViewModel(
    private val readLaterService: ReadLaterService, networkService: NetworkService
) : RxPagingViewModel() {

    init {
        if (networkService.isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
        } else {
            internetErrorState()
            showErrorLog("There is no Internet connection")
        }

        readLaterService.getArticlesByReadLater()
            .subscribeOnSingleObserveMain()
            .subscribe({
                showLog("On Next readLaterService: ${it.size}")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
                    articles.mutableValue = it
                    successState()
                }
            }, {
                showErrorLog("Error getArticlesBySearchTag: it")
            })
            .addToSubscription()
    }

    fun readLaterClicked(article: Article) {
        showToast(getString(R.string.toast_added_read_later))
        var isReadLater = article.isReadLater
        if (article.isReadLater == 1) isReadLater = 0
        else if (article.isReadLater == 0) isReadLater = 1
        update(
            Article(
                article.id,
                article.author,
                article.content,
                article.description,
                article.publishedAt,
                article.source,
                article.title,
                article.url,
                article.urlToImage,
                article.searchTag,
                article.country,
                isReadLater
            )
        )
    }

    fun deleteAllClicked() {
        updateArticles(articles.value)
    }

    private fun update(article: Article) {
        showLog("update: ${article.id}")
        readLaterService.update(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            })
            .addToSubscription()
    }

    private fun updateArticles(articles: List<Article>) {
        articles.map { it.isReadLater = 0 }
        readLaterService.updateArticles(articles)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update articles success")
                this.articles.mutableValue = emptyList()
            }, { error ->
                showErrorLog("Update articles error: $error")
            })
            .addToSubscription()
    }
}