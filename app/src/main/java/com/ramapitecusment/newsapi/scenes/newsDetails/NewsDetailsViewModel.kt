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

//    val articles = DataList<Article>()
//
//    fun update(article: Article) {
//        if (article.searchTag.equals("NaN")) updateArticleTopHeadline(article)
//        else if (article.country.equals("NaN")) updateArticleEntity(article)
//    }
//
//    private fun updateArticleTopHeadline(article: Article) {
//        topHeadlinesService.update(article.toTopHeadlines())
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                showLog("Update success")
//            }, { error ->
//                showErrorLog("Update error: $error")
//            }).addToSubscription()
//    }
//
//    private fun updateArticleEntity(article: Article) {
//        everythingService.update(article.toArticleEntity())
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                showLog("Update success")
//            }, { error ->
//                showErrorLog("Update error: $error")
//            }).addToSubscription()
//    }
}