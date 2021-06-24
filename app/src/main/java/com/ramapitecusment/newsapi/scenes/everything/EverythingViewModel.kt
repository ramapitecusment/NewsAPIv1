package com.ramapitecusment.newsapi.scenes.everything

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.*
import com.ramapitecusment.newsapi.services.database.*
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import io.reactivex.rxjava3.subjects.PublishSubject

class EverythingViewModel(private val service: EverythingService) : BaseNewsViewModel() {
    var searchTag = Text()
    var searchTagRX: PublishSubject<String> = PublishSubject.create()

    init {
        if (isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
        } else {
            internetErrorState()
            showErrorLog("There os no Internet connection")
        }

        pageRx
            .doOnNext { page ->
                showLog("doOnNext pageRx: $page")
                if (page == 1) loadingState()
                else pageLoadingState()
            }.withLatestFrom(searchTagRX
                .filter { charSequence ->
                    showLog("before filter rxSearch -$charSequence- ${internetErrorVisible.value}")
                    !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
                }
                .doOnNext { showLog("doOnNext searchTagRX: $it") }
                .map { it.toString() }
                .distinctUntilChanged()
            ) { t1, t2 ->
                showLog("withLatestFrom $t1 ---- $t2")
                getFromRemote(t2, t1)
                service.getArticlesBySearchTag(t2).subscribeOnIoObserveMain()
            }
            .switchMap { it.toObservable() }
            .distinct()
            .subscribeOnIoObserveMain()
            .subscribe(
                {
                    showLog("On Next combine latest: ${it.size}")
                    isLoadingPage.mutableValue = false
                    if (it.isNotEmpty()) {
                        articles.mutableValue = it.toArticle().distinct()
                        successState()
                    }
                }, {
                    showErrorLog("Error getArticlesBySearchTag: it")
                }).addToSubscription()
    }

    private fun getFromRemote(search: String, page: Int) {
        service.getFromRemote(search, page).subscribeOnSingleObserveMain().subscribe({ response ->
            showLog(response.toString())
            if (response.isSuccessful) {
                showLog("Get from remote success: ${response.body()?.articles?.size}")
                isPageEnd.value = (response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE)
                if (!isPageEnd.value!!) {
                    response.body()?.articles?.let { insertAll(it.toArticleEntity(search)) }
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

    private fun insertAll(articles: List<ArticleEntity>) {
        service.insertAll(articles).subscribeOnIoObserveMain()
            .subscribe({
                showLog("Insert Complete")
            }, { error ->
                showErrorLog("Insert error: $error")
            }).addToSubscription()
    }

    fun deleteAll() {
        service.deleteAll().subscribeOnIoObserveMain()
            .subscribe({
                showLog("Delete success")
            }, { error ->
                showErrorLog("Delete error: $error")
            }).addToSubscription()
    }

    fun readLaterArticle(article: ArticleEntity) {
        Log.d(LOG, "readLaterArticle: ${article.id}")
        article.isReadLater = 1
        showLog("readLaterArticle ${article.isReadLater}")
        update(article)
        insertReadLater(article.toReadLaterArticle())
    }

    fun unreadLaterArticle(article: ArticleEntity) {
        Log.d(LOG, "unreadLaterArticle: ${article.id}")
        article.isReadLater = 0
        showLog("unreadLaterArticle ${article.isReadLater}")
        update(article)
        deleteReadLater(article.toReadLaterArticle())
    }

    private fun insertReadLater(article: ReadLater) {
        service.insertToReadLater(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("InsertReadLater Complete")
            }, { error ->
                showErrorLog("InsertReadLater error: $error")
            }).addToSubscription()
    }

    private fun update(article: ArticleEntity) {
        Log.d(LOG, "update: ${article.id}")
        service.update(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            }).addToSubscription()
    }

    private fun deleteReadLater(article: ReadLater) {
        service.deleteReadLater(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("InsertReadLater Complete")
            }, { error ->
                showErrorLog("InsertReadLater error: $error")
            }).addToSubscription()
    }

    fun searchButtonClicked() {
        resetPageValue()
    }

    private fun resetPageValue() {
        pageRx.onNext(1)
    }

    fun increasePageValue() {
        increasePageValueProtected()
    }
}