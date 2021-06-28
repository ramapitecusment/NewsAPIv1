package com.ramapitecusment.newsapi.scenes.everything

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.*
import com.ramapitecusment.newsapi.services.database.*
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

class EverythingViewModel(
    private val newsService: NewsService,
    private val networkService: NetworkService
) : BaseNewsViewModel() {
    var searchTag = Text()
    var disposable: Disposable? = null
    var searchTagRX: PublishSubject<String> = PublishSubject.create()

    init {
        if (networkService.isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
        } else {
            internetErrorState()
            showErrorLog("There is no Internet connection")
        }

        pageRx.subscribeOnIoObserveMain().subscribe({ page ->
            showLog("doOnNext pageRx: $page")
            this.page.mutableValue = page
            if (page != 1) pageLoadingState()
        }, { error ->
            showErrorLog("pageRx Error: $error")
        })
            .addToSubscription()

        searchTagRX
            .filter { charSequence ->
                loadingState()
                showLog("before filter rxSearch -$charSequence- ${internetErrorVisible.value}")
                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
            }
            .map { it.toString() }
            .distinctUntilChanged()
            .subscribe({ searchTag ->
                showLog("doOnNext searchTagRX: $searchTag")
                this.searchTag.mutableValue = searchTag
            }, { error ->
                showErrorLog("searchTagRX Error: $error")
            })
            .addToSubscription()
    }

    fun getFromRemote(search: String, page: Int) {
        everythingService.getFromRemote(search, page)
            .subscribeOnSingleObserveMain()
            .subscribe({ response ->
                showLog(response.toString())
                if (response.isSuccessful) {
                    showLog("Get from remote success: ${response.body()?.articles?.size}")
                    if (!isPageEnd.value!!) {
                        response.body()?.articles?.let { insertAll(it.toArticleEntity(search)) }
                    } else {
                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
                        successState()
                    }
                    isPageEnd.value = (response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE)
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

    fun getFromDatabase(search: String) {
        disposable?.dispose()
        disposable = everythingService.getArticlesBySearchTag(search)
            .subscribeOnSingleObserveMain()
            .subscribe({
                showLog("On Next combine latest: ${it.size} - $search")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
//                    showLog("$it")
                    articles.mutableValue = it.toArticle().distinct()
                    successState()
                }
            }, {
                showErrorLog("Error getArticlesBySearchTag: it")
            })
    }

    private fun insertAll(articles: List<ArticleEntity>) {
        everythingService
            .insertAll(articles)
            .subscribeOnSingleObserveMain()
            .subscribe({
                showLog("Insert Complete")
            }, { error ->
                showErrorLog("Insert error: $error")
            }).addToSubscription()
    }

    fun deleteAllClicked() {
        everythingService
            .deleteAll()
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Delete success")
            }, { error ->
                showErrorLog("Delete error: $error")
            }).addToSubscription()
    }

    fun readLaterArticle(article: ArticleEntity) {
        Log.d(LOG, "readLaterArticle: ${article.id}")
        article.isReadLater = 1
        showLog("readLaterArticle isReadLater ${article.isReadLater}")
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
        readLaterService
            .insertToReadLater(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("InsertReadLater Complete")
            }, { error ->
                showErrorLog("InsertReadLater error: $error")
            }).addToSubscription()
    }

    private fun update(article: ArticleEntity) {
        Log.d(LOG, "update: ${article.id}")
        everythingService
            .update(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            }).addToSubscription()
    }

    private fun deleteReadLater(article: ReadLater) {
        readLaterService
            .deleteReadLater(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("deleteReadLater Complete")
            }, { error ->
                showErrorLog("deleteReadLater error: $error")
            }).addToSubscription()
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
        disposable?.dispose()
    }
}