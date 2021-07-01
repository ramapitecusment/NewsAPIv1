package com.ramapitecusment.newsapi.scenes.newsDetails

import com.ramapitecusment.newsapi.common.mvvm.BaseViewModel
import com.ramapitecusment.newsapi.common.mvvm.DataList
import com.ramapitecusment.newsapi.services.database.*
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.news.NewsService

class NewsDetailsViewModel(
    private val newsService: NewsService,
    private val networkService: NetworkService
) : BaseViewModel() {

    val articles = DataList<Article>()

    fun update(article: Article) {
        val isReadLater = article.isReadLater
        newsService.update(
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
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            })
            .addToSubscription()
    }
}