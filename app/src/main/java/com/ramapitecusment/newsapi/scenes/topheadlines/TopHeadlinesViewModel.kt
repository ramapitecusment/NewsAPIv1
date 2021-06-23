package com.ramapitecusment.newsapi.scenes.topheadlines

import android.text.TextUtils
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.Data
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.ArticleTopHeadline
import com.ramapitecusment.newsapi.services.database.toArticle
import com.ramapitecusment.newsapi.services.network.Response
import com.ramapitecusment.newsapi.services.network.toArticleTopHeadline
import com.ramapitecusment.newsapi.services.topheadlines.TopHeadlinesService
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TopHeadlinesViewModel(private val service: TopHeadlinesService) : BaseNewsViewModel() {
    var country = Text()
    var page = Data(1)
    var countryRX: PublishSubject<String> = PublishSubject.create()
    private lateinit var _getFromRemote: Maybe<retrofit2.Response<Response>>

    init {
        if (isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
            countryRX.onNext("ru")
            pageRx.onNext(1)
            country.mutableValue = "ru"
            loadingState()
            _getFromRemote = service.getFromRemote(country.value, page.value)
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
            }.subscribeOnIoObserveMain().subscribe().addToSubscription()

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
            .subscribeOnIoObserveMain()
            .switchMap {
                getFromRemote(country.value, 1)
                service.getAllByCountry(country.value).subscribeOnSingleObserveMain().toObservable()
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
        service.getFromRemote(country, page).subscribeOnSingleObserveMain().subscribe({ response ->
            showLog(response.toString())
            if (response.isSuccessful) {
                showLog("Get from remote success: ${response.body()?.articles?.size}")
                response.body()?.articles?.let { insertAll(it.toArticleTopHeadline(country)) }
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
        service.insertAll(articles).subscribeOnIoObserveMain().subscribe(
            {
                showLog("Insert Complete")
            }, { error ->
                showErrorLog("Insert error: $error")
            }).addToSubscription()
    }

    fun deleteAll() {
        service.deleteAll().subscribeOnIoObserveMain().subscribe(
            {
                showLog("Delete success")
            }, { error ->
                showErrorLog("Delete error: $error")
            }).addToSubscription()
    }
}