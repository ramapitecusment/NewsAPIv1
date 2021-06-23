package com.ramapitecusment.newsapi.scenes.topheadlines

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.COUNTRY_DEFAULT_VALUE
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.BaseViewModel
import com.ramapitecusment.newsapi.common.mvvm.Data
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.database.ArticleTopHeadline
import com.ramapitecusment.newsapi.services.database.toArticle
import com.ramapitecusment.newsapi.services.network.Response
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import com.ramapitecusment.newsapi.services.network.toArticleTopHeadline
import com.ramapitecusment.newsapi.services.topheadlines.TopHeadlinesService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TopHeadlinesViewModel(private val service: TopHeadlinesService) : BaseNewsViewModel() {
    var country = Text()
    var page = Data(1)
    var countryRX: PublishSubject<String> = PublishSubject.create()

    init {
        if (isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
            countryRX.onNext("ru")
            pageRx.onNext(1)
            country.mutableValue = "ru"
            loadingState()
        } else {
            internetErrorState()
            showErrorLog("There os no Internet connection")
        }

//        Observable.interval(5, TimeUnit.SECONDS)
//            .switchMap {
//                countryRX.onNext(country.value)
//                pageRx.onNext(page.value)
//                showLog("in switchmap")
//                Observable.combineLatest(pageRx
//                    .doOnNext { page ->
//                        showLog("doOnNext pageRx: $page")
//                        if (page == 1) loadingState()
//                        else pageLoadingState()
//                        this.page.mutableValue = page
//                    }
//                    .doOnEach {
//                        showLog("doOnEach")
//                    }
//                    .doOnError {
//                        showLog("doOnError $it")
//                    }, countryRX
//                    .filter { charSequence ->
//                        showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
//                        !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
//                    }
//                    .doOnNext
//                    {
//                        showLog("doOnNext countryRX: $it")
//                        country.mutableValue = it
//                    }
//                    .map { it.toString() }
//                    .distinctUntilChanged()
//                ) { t1, t2 ->
//                    showLog("withLatestFrom $t1 ---- $t2")
//                    getFromRemote(t2, t1)
//                    service.getAllByCountry(t2).subscribeOnIoObserveMain()
//                }
//                    .switchMap { it.toObservable() }
//                    .subscribeOnIoObserveMain()
//            }
//            .distinct()
//            .subscribeOnIoObserveMain()
//            .subscribe(
//                {
//                    showLog("On Next withLatestFrom: ${it.size}")
//                    isLoadingPage.mutableValue = false
//                    if (it.isNotEmpty()) {
//                        articles.mutableValue = it.toArticle().distinct()
//                        successState()
//                    }
//                }, {
//                    showErrorLog("Error getAllByCountry: it")
//                })
//            .addToSubscription()

        Observable.interval(5, TimeUnit.SECONDS)
            .switchMap {
                showLog("in switchmap")
                countryRX.onNext(country.value)
                pageRx.onNext(page.value)
                pageRx
                    .doOnNext { page ->
                        showLog("doOnNext pageRx: $page")
                        if (page == 1) loadingState()
                        else pageLoadingState()
                        this.page.mutableValue = page
                    }
                    .doOnEach {
                        showLog("doOnEach")
                    }
                    .doOnError {
                        showLog("doOnError $it")
                    }
                    .withLatestFrom(countryRX
                        .filter { charSequence ->
                            showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
                            !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
                        }
                        .doOnNext
                        {
                            showLog("doOnNext countryRX: $it")
                            country.mutableValue = it
                        }
                        .map { it.toString() }
                        .distinctUntilChanged()
                    ) { t1, t2 ->
                        showLog("withLatestFrom $t1 ---- $t2")
                        getFromRemote(t2, t1)
                        service.getAllByCountry(t2).subscribeOnIoObserveMain()
                    }
                    .switchMap { it.toObservable() }
                    .subscribeOnIoObserveMain()
            }
            .distinct()
            .subscribeOnIoObserveMain()
            .subscribe(
                {
                    showLog("On Next withLatestFrom: ${it.size}")
                    isLoadingPage.mutableValue = false
                    if (it.isNotEmpty()) {
                        articles.mutableValue = it.toArticle().distinct()
                        successState()
                    }
                }, {
                    showErrorLog("Error getAllByCountry: it")
                })
            .addToSubscription()

//        pageRx
//            .doOnNext { page ->
//                showLog("doOnNext pageRx: $page")
//                if (page == 1) loadingState()
//                else pageLoadingState()
//                this.page.mutableValue = page
//            }
//            .withLatestFrom(countryRX
//                .filter { charSequence ->
//                    showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
//                    !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
//                }
//                .doOnNext
//                {
//                    showLog("doOnNext countryRX: $it")
//                    country.mutableValue = it
//                }
//                .map { it.toString() }
//                .distinctUntilChanged()
//            ) { t1, t2 ->
//                showLog("withLatestFrom $t1 ---- $t2")
//                getFromRemote(t2, t1)
//                service.getAllByCountry(t2).subscribeOnIoObserveMain()
//            }
//            .switchMap { it.toObservable() }
//            .distinct()
//            .subscribeOnIoObserveMain()
//            .subscribe(
//                {
//                    showLog("On Next withLatestFrom: ${it.size}")
//                    isLoadingPage.mutableValue = false
//                    if (it.isNotEmpty()) {
//                        articles.mutableValue = it.toArticle().distinct()
//                        successState()
//                    }
//                }, {
//                    showErrorLog("Error getAllByCountry: it")
//                }).addToSubscription()
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