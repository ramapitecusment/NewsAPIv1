package com.ramapitecusment.newsapi.scenes.topheadlines

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.Data
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.*
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.toArticleTopHeadline
import com.ramapitecusment.newsapi.services.readLater.ReadLaterService
import com.ramapitecusment.newsapi.services.topheadlines.TopHeadlinesService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TopHeadlinesViewModel(
    private val topHeadlinesService: TopHeadlinesService,
    private val readLaterService: ReadLaterService,
    private val networkService: NetworkService
) : BaseNewsViewModel() {
    private var country = Text()
    private var countryRX: PublishSubject<String> = PublishSubject.create()

    init {
        if (networkService.isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
            countryRX.onNext("ru")
            pageRx.onNext(1)
            country.mutableValue = "ru"
            loadingState()
        } else {
            internetErrorState()
            showErrorLog("There os no Internet connection")
        }

        pageRx
            .doOnNext { page ->
                showLog("doOnNext pageRx: $page")
                if (page == 1) loadingState()
                else pageLoadingState()
                this.page.mutableValue = page
                getFromRemote(country.value, page)
            }
            .doOnEach {
                showLog("doOnEach")
            }
            .doOnError {
                showLog("doOnError $it")
            }.subscribeOnSingleObserveMain().subscribe().addToSubscription()

        countryRX
            .filter { charSequence ->
                showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
            }
            .doOnNext {
                showLog("doOnNext countryRX: $it")
                country.mutableValue = it
            }
            .map { it.toString() }
            .distinctUntilChanged().subscribeOnIoObserveMain().subscribe().addToSubscription()

        Observable.interval(5, TimeUnit.SECONDS)
            .switchMap {
                getFromRemote(country.value, 1)
                topHeadlinesService.getAllByCountry(country.value).subscribeOnSingleObserveMain()
                    .toObservable()
            }
            .distinct()
            .subscribeOnIoObserveMain()
            .subscribe(
                {
                    showLog("On Next interval: ${it.size}")
                    isLoadingPage.mutableValue = false
                    if (it.isNotEmpty()) {
                        articles.mutableValue = it.toArticle().distinct()
                        successState()
                    }
                }, {
                    showErrorLog("Error interval: $it")
                }).addToSubscription()
    }


    private fun getFromRemote(country: String, page: Int) {
        topHeadlinesService.getFromRemote(country, page).subscribeOnSingleObserveMain()
            .subscribe({ response ->
                showLog(response.toString())
                if (response.isSuccessful) {
                    showLog("Get from remote success: ${response.body()?.articles?.size}")
                    isPageEnd.value = (response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE)
                    if (!isPageEnd.value!!) {
                        response.body()?.articles?.let { insertAll(it.toArticleTopHeadline(country)) }
                    } else {
                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
                    }
                } else {
                    showErrorLog("Got error from the server: $response")
                    errorState()
                }
            }, { error ->
                showErrorLog("Error getFromRemote: $error")
                internetErrorState()
            }, {
                showLog("Complete getFromRemote")
            }).addToSubscription()
    }

    fun increasePageValue() {
        increasePageValueProtected()
    }

    private fun insertAll(articles: List<ArticleTopHeadline>) {
        topHeadlinesService.insertAll(articles).subscribeOnIoObserveMain().subscribe(
            {
                showLog("Insert Complete")
            }, { error ->
                showErrorLog("Insert error: $error")
            }).addToSubscription()
    }

    fun deleteAll() {
        topHeadlinesService.deleteAll().subscribeOnIoObserveMain().subscribe(
            {
                showLog("Delete success")
            }, { error ->
                showErrorLog("Delete error: $error")
            }).addToSubscription()
    }

    fun readLaterArticle(article: ArticleTopHeadline) {
        Log.d(LOG, "readLaterArticle: ${article.id}")
        article.isReadLater = 1
        showLog("readLaterArticle isReadLater ${article.isReadLater}")
        update(article)
        insertReadLater(article.toReadLaterArticle())
    }

    fun unreadLaterArticle(article: ArticleTopHeadline) {
        Log.d(LOG, "unreadLaterArticle: ${article.id}")
        article.isReadLater = 0
        showLog("unreadLaterArticle ${article.isReadLater}")
        update(article)
        deleteReadLater(article.toReadLaterArticle())
    }

    private fun insertReadLater(article: ReadLater) {
        readLaterService.insertToReadLater(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("InsertReadLater Complete")
            }, { error ->
                showErrorLog("InsertReadLater error: $error")
            }).addToSubscription()
    }

    private fun update(article: ArticleTopHeadline) {
        Log.d(LOG, "update: ${article.id}")
        topHeadlinesService.update(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            }).addToSubscription()
    }

    private fun deleteReadLater(article: ReadLater) {
        readLaterService.deleteReadLater(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("deleteReadLater Complete")
            }, { error ->
                showErrorLog("deleteReadLater error: $error")
            }).addToSubscription()
    }
}