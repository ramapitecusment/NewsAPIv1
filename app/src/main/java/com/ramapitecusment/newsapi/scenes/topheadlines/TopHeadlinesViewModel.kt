package com.ramapitecusment.newsapi.scenes.topheadlines

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.*
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TopHeadlinesViewModel(
    private val newsService: NewsService,
    private val networkService: NetworkService
) : BaseNewsViewModel() {
//    private var country = Text()
//    private var countryRX: PublishSubject<String> = PublishSubject.create()
//
//    init {
//        if (networkService.isInternetAvailable(MainApplication.instance)) {
//            showLog("Connected to internet")
//            countryRX.onNext("ru")
//            pageRx.onNext(1)
//            country.mutableValue = "ru"
//            loadingState()
//        } else {
//            internetErrorState()
//            showErrorLog("There os no Internet connection")
//        }
//
//        pageRx
//            .doOnNext { page ->
//                showLog("doOnNext pageRx: $page")
//                if (page == 1) loadingState()
//                else pageLoadingState()
//                this.page.mutableValue = page
//                getFromRemote(country.value, page)
//            }
//            .doOnEach {
//                showLog("doOnEach")
//            }
//            .doOnError {
//                showLog("doOnError $it")
//            }.subscribeOnSingleObserveMain().subscribe().addToSubscription()
//
//        countryRX
//            .filter { charSequence ->
//                showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
//                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
//            }
//            .doOnNext {
//                showLog("doOnNext countryRX: $it")
//                country.mutableValue = it
//            }
//            .map { it.toString() }
//            .distinctUntilChanged().subscribeOnIoObserveMain().subscribe().addToSubscription()
//
//        Observable.interval(5, TimeUnit.SECONDS)
//            .switchMap {
//                getFromRemote(country.value, 1)
//                newsService.getArticlesByCountry(country.value).subscribeOnSingleObserveMain()
//                    .toObservable()
//            }
//            .distinct()
//            .subscribeOnIoObserveMain()
//            .subscribe(
//                {
//                    showLog("On Next interval: ${it.size}")
//                    isLoadingPage.mutableValue = false
//                    if (it.isNotEmpty()) {
//                        articles.mutableValue = it.toArticle().distinct()
//                        successState()
//                    }
//                }, {
//                    showErrorLog("Error interval: $it")
//                }).addToSubscription()
//    }
//
//
//    private fun getFromRemote(country: String, page: Int) {
//        newsService.getArticlesByCountry(country, page).subscribeOnSingleObserveMain()
//            .subscribe({ response ->
//                showLog(response.toString())
//                if (response.isSuccessful) {
//                    showLog("Get from remote success: ${response.body()?.articles?.size}")
//                    isPageEnd.value = (response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE)
//                    if (!isPageEnd.value!!) {
//                        response.body()?.articles?.let { insertAll(it.toArticleTopHeadline(country)) }
//                    } else {
//                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
//                    }
//                } else {
//                    showErrorLog("Got error from the server: $response")
//                    errorState()
//                }
//            }, { error ->
//                showErrorLog("Error getFromRemote: $error")
//                internetErrorState()
//            }, {
//                showLog("Complete getFromRemote")
//            }).addToSubscription()
//    }
//
//    private fun insert(articles: List<Article>) {
//        newsService.insert(articles)
//            .subscribeOnIoObserveMain()
//            .subscribe(
//                {
//                    showLog("Insert Complete")
//                }, { error ->
//                    showErrorLog("Insert error: $error")
//                }).addToSubscription()
//    }
//
//    fun deleteAllClicked() {
//        newsService.delete()
//            .subscribeOnIoObserveMain()
//            .subscribe(
//                {
//                    showLog("Delete success")
//                }, { error ->
//                    showErrorLog("Delete error: $error")
//                }).addToSubscription()
//    }
//
//    fun readLaterArticle(article: Article) {
//        article.isReadLater = 1
//        update(article)
//    }
//
//    fun unreadLaterArticle(article: Article) {
//        article.isReadLater = 0
//        update(article)
//    }
//
//    private fun update(article: Article) {
//        newsService.update(article)
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                showLog("Update success")
//            }, { error ->
//                showErrorLog("Update error: $error")
//            }).addToSubscription()
//    }
}