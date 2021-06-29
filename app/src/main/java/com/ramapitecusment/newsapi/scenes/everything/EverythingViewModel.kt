package com.ramapitecusment.newsapi.scenes.everything

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.toArticle
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EverythingViewModel(private val newsService: NewsService, networkService: NetworkService) :
    BaseNewsViewModel() {

    var searchTag = Text()
    var searchTagRX: PublishProcessor<String> = PublishProcessor.create()

    init {
        if (networkService.isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
        } else {
            internetErrorState()
            showErrorLog("There is no Internet connection")
        }

        pageRx
            .doOnNext { page ->
                showLog("doOnNext pageRx: $page")
                if (page == 1) {
                    loadingState()
                    isPageEndRx.onNext(false)
                } else pageLoadingState()
            }
            .doOnError { showErrorLog("pageRx Error: $it") }
            .withLatestFrom(searchTagRX
                .filter { charSequence ->
                    showLog("before filter rxSearch -$charSequence- ${internetErrorVisible.value}")
                    !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
                }
                .map { it.toString() }
                .distinctUntilChanged()
                .doOnNext { loadingState() }
            ) { t1, t2 ->
                showLog("withLatestFrom $t1 ---- $t2")
                showLog("withLatestFrom ${searchTag.value} ---- ${page.value}")
            }
            .switchMap {
                newsService.getEverythingRemote(searchTag.value, page.value).toFlowable()
                    .filter { response ->
                        showLog(response.toString())
                        if (response.isSuccessful) {
                            showLog("Get from remote success: ${response.body()?.articles?.size}")
                            if (!isPageEnd.value) {
                                return@filter true
                            } else {
                                showLog("Get from remote success pageEnd: ${isPageEnd.value}")
                                successState()
                            }
                            isPageEndRx.onNext((response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE))
                        } else {
                            errorState()
                            showErrorLog("Got error from the server: $response")
                        }
                        return@filter false
                    }
            }
            .switchMap {
                it.body()?.articles?.toArticle(searchTag.value)?.let { it1 ->
                    showLog("switchMap 1")
                    newsService.insertAll(it1).toFlowable()
                }
            }
            .switchMap {
                showLog("switchMap 2 --- ${searchTag.value}")
                newsService.getArticlesBySearchTag(searchTag.value)
            }
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("On Next combine latest: ${it.size} - ${searchTag.value}")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
                    articles.mutableValue = it.distinct()
                    successState()
                }
            }, {
                internetErrorState()
                showErrorLog("Error getFromRemote: $it")
            })
            .addToSubscription()
//            .doOnNext { response ->
//                showLog(response.toString())
//                if (response.isSuccessful) {
//                    showLog("Get from remote success: ${response.body()?.articles?.size}")
//                    if (!isPageEnd.value) {
//                        showLog("I am in insert module")
//                        response.body()?.articles?.let {
//                            newsService.insertAll(it.toArticle(searchTag.value))
//                                .doOnComplete {
//                                    showLog("Inert complete")
//                                }
//                        }
//                    } else {
//                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
//                        successState()
//                    }
//                    isPageEndRx.onNext((response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE))
//                } else {
//                    errorState()
//                    showErrorLog("Got error from the server: $response")
//                }
//            }
//                    .doOnError {
//                        internetErrorState()
//                        showErrorLog("Error getFromRemote: $it")
//                    }
//                    .switchMap {
//                        newsService.getArticlesBySearchTag(searchTag.value)
//                    }
//                    .distinct()
//                    .subscribeOnIoObserveMain()
//                    .subscribe({
//                        showLog("On Next combine latest: ${it.size} - ${searchTag.value}")
//                        isLoadingPage.mutableValue = false
//                        if (it.isNotEmpty()) {
//                            articles.mutableValue = it.distinct()
//                            successState()
//                        }
//                    }, {
//                        internetErrorState()
//                        showErrorLog("Error getFromRemote: $it")
//                    })
//                    .addToSubscription()


        isPageEndRx
            .subscribeOnIoObserveMain()
            .subscribe(
                { isPageEnd ->
                    showLog("isPageEnd --- $isPageEnd")
                    this.isPageEnd.mutableValue = isPageEnd
                },
                {
                    showErrorLog("isPageEnd Error: $it")
                })
            .addToSubscription()

//        pageRx
//            .doOnNext { page ->
//                showLog("doOnNext pageRx: $page")
//                this.page.mutableValue = page
//                if (page != 1) pageLoadingState()
//            }
//            .doOnError { showErrorLog("pageRx Error: $it") }
//            .switchMap {
//                newsService.getEverythingRemote(searchTag.value, it).toFlowable()
//            }
//            .doOnNext { response ->
//                showLog(response.toString())
//                if (response.isSuccessful && !isPageEnd.value) {
//                    response.body()?.articles?.let {
//                        newsService.insertAll(it.toArticle(searchTag.value))
//                    }
//                }
//            }
//            .subscribeOnIoObserveMain()
//            .subscribe({ response ->
//                showLog(response.toString())
//                if (response.isSuccessful) {
//                    showLog("Get from remote success: ${response.body()?.articles?.size}")
//                    if (isPageEnd.value) {
//                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
//                        successState()
//                    }
//                    isPageEnd.mutableValue =
//                        (response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE)
//                } else {
//                    errorState()
//                    showErrorLog("Got error from the server: $response")
//                }
//
//            }, {
//                internetErrorState()
//                showErrorLog("Error getFromRemote: $it")
//            })
//            .addToSubscription()
//
//        searchTagRX
//            .filter { charSequence ->
//                showLog("before filter rxSearch -$charSequence- ${internetErrorVisible.value}")
//                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
//            }
//            .map { it.toString() }
//            .distinctUntilChanged()
//            .doOnNext {
//                showLog("doOnNext searchTagRX: $it")
//                loadingState()
//            }
//            .doOnError { showErrorLog("searchTagRX Error: $it") }
//            .switchMap { search ->
//                newsService.getEverythingRemote(search, 1).toFlowable()
//            }
//            .doOnError {
//                internetErrorState()
//                showErrorLog("Error getFromRemote: $it")
//            }
//            .switchMap {
//                newsService.getArticlesBySearchTag(searchTag.value)
//            }
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                showLog("On Next combine latest: ${it.size} - ${searchTag.value}")
//                isLoadingPage.mutableValue = false
//                if (it.isNotEmpty()) {
//                    articles.mutableValue = it.distinct()
//                    successState()
//                }
//            }, {
//                internetErrorState()
//                showErrorLog("Error getFromRemote: $it")
//            })
//            .addToSubscription()
    }

    fun deleteAllClicked() {
        newsService
            .deleteAll()
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Delete success")
            }, { error ->
                showErrorLog("Delete error: $error")
            })
            .addToSubscription()
    }

    fun readLaterButtonClicked(article: Article) {
        if (article.isReadLater == 1) article.isReadLater = 0
        else if (article.isReadLater == 0) article.isReadLater = 1
        update(article)
    }

    private fun update(article: Article) {
        Log.d(LOG, "update: ${article.id}")
        newsService
            .update(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            })
            .addToSubscription()
    }

    fun searchButtonClicked() {
        resetPageValue()
    }

    private fun resetPageValue() {
        page.mutableValue = 1
        pageRx.onNext(1)
    }

    override fun onCleared() {
        super.onCleared()
//        disposable?.dispose()
    }

//    fun getFromRemote(search: String, page: Int) {
//        newsService.getEverythingRemote(search, page)
//            .doOnSuccess { response ->
//                showLog(response.toString())
//                if (response.isSuccessful) {
//                    showLog("Get from remote success: ${response.body()?.articles?.size}")
//                    if (!isPageEnd.value) {
//                        response.body()?.articles?.let { newsService.insertAll(it.toArticle(search)) }
//                    } else {
//                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
//                        successState()
//                    }
//                    isPageEnd.mutableValue =
//                        (response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE)
//                } else {
//                    errorState()
//                    showErrorLog("Got error from the server: $response")
//                }
//            }
//            .doOnError {
//                internetErrorState()
//                showErrorLog("Error getFromRemote: $it")
//            }
//            .subscribeOnSingleObserveMain()
//            .subscribe()
//            .addToSubscription()
//    }

//    fun getFromDatabase(search: String) {
//        disposable?.dispose()
//        disposable = newsService.getArticlesBySearchTag(search)
//            .subscribeOnSingleObserveMain()
//            .subscribe({
//                showLog("On Next combine latest: ${it.size} - $search")
//                isLoadingPage.mutableValue = false
//                if (it.isNotEmpty()) {
//                    articles.mutableValue = it.distinct()
//                    successState()
//                }
//            }, {
//                showErrorLog("Error getArticlesBySearchTag: it")
//            })
//    }

}