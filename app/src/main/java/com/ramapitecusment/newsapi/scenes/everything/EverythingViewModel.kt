package com.ramapitecusment.newsapi.scenes.everything

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.RxPagingViewModel
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.toArticle
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor

class EverythingViewModel(private val everythingService: EverythingService, networkService: NetworkService) :
    RxPagingViewModel() {

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
            .withLatestFrom(searchTagRX
                .filter { charSequence ->
                    showLog("before filter rxSearch -$charSequence- ${internetErrorVisible.value}")
                    !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
                }
                .map { it.toString() }
                .distinctUntilChanged()
                .doOnNext { loadingState() }
            ) { _page, _search ->
                showLog("withLatestFrom $_search ---- $_page")
                showLog("withLatestFrom ${searchTag.value} ---- ${page.value}")
                everythingService.getEverythingRemote(_search, _page)
            }
            .switchMap { it.toFlowable() }
            .filter { response ->
                var goFuther = false
                showLog(response.toString())
                if (response.isSuccessful) {
                    showLog("Get from remote success: ${response.body()?.articles?.size}")
                    if (!isPageEnd.value) {
                        goFuther = true
                    } else {
                        showLog("Get from remote success pageEnd: ${isPageEnd.value}")
                        successState()
                    }
                    isPageEndRx.onNext((response.body()?.articles?.size ?: 0 < PAGE_SIZE_VALUE))
                } else {
                    errorState()
                    showErrorLog("Got error from the server: $response")
                    goFuther = false
                }
                return@filter goFuther
            }
            .map { response ->
                response.body()?.articles?.toArticle(searchTag = searchTag.value)?.let { it }
            }
            .switchMap { articleList ->
                everythingService.insertAll(articleList).andThen(Flowable.just(1))
            }
            .switchMap {
                everythingService.getArticlesBySearchTag(searchTag.value)
            }
            .map { articleList ->
                articleList.distinctBy { listOf(it.title, it.publishedAt, it.author) }
            }
            .subscribeOnIoObserveMain()
            .subscribe({ articleList ->
                showLog("On Next combine latest: ${articleList.size} - ${searchTag.value}")
                isLoadingPage.mutableValue = false
                if (articleList.isNotEmpty()) {
                    articles.mutableValue = articleList
                    successState()
                }
            }, {
                internetErrorState()
                showErrorLog("Error getFromRemote: $it")
            })
            .addToSubscription()
    }

    fun deleteAllClicked() {
        everythingService
            .deleteAllBySearchTag()
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Delete success")
                resetPageValue()
                this.articles.mutableValue = emptyList()
            }, { error ->
                showErrorLog("Delete error: $error")
            })
            .addToSubscription()
    }

    fun readLaterButtonClicked(article: Article) {
        showToast(getString(R.string.toast_added_read_later))
        var isReadLater = article.isReadLater
        if (article.isReadLater == 1) isReadLater = 0
        else if (article.isReadLater == 0) isReadLater = 1
        update(Article(
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
        ))
    }

    private fun update(article: Article) {
        Log.d(LOG, "update: ${article.id}")
        everythingService
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
}