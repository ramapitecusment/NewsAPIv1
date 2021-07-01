package com.ramapitecusment.newsapi.scenes.topheadlines

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.*
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.toArticle
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TopHeadlinesViewModel(private val topHeadlinesService: TopHeadlinesService, networkService: NetworkService) :
    BaseNewsViewModel() {
    private var country = Text()
    private var countryRX: PublishProcessor<String> = PublishProcessor.create()

    init {
        pageRx
            .withLatestFrom(countryRX
                .filter { charSequence ->
                    showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
                    !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
                }
                .map { it.toString() }
            ) { t1, t2 ->
                showLog("withLatestFrom $t1 ---- $t2")
                showLog("withLatestFrom ${country.value} ---- ${page.value}")
            }
            .switchMap {
                showLog("pager")
                topHeadlinesService.getTopHeadlinesRemote(country.value, page.value).toFlowable()
            }
            .filter { response ->
                showLog(response.toString())
                if (response.isSuccessful) {
                    showLog("Get from remote success: ${response.body()?.articles?.size}")
                    if (!isPageEnd.value) {
                        showLog("In isPageEnd")
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
            .map { response ->
                showLog("In map")
                response.body()?.articles?.toArticle(country = country.value)?.let { it }
            }
            .switchMap { articleList ->
                showLog("In switchMap")
                topHeadlinesService.insertAll(articleList).andThen(Flowable.just(1))
            }
            .switchMap {
                topHeadlinesService.getArticlesByCountry(country.value)
            }
            .map { articleList ->
                articleList.distinctBy { listOf(it.title, it.publishedAt, it.author) }
            }
            .subscribe({
                showLog("On Next interval: ${it.size}")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
                    articles.mutableValue = it
                    successState()
                }
            }, {
                showErrorLog("Error interval: $it")
            })
            .addToSubscription()

//        Flowable
//            .interval(5, TimeUnit.SECONDS)
//            .switchMap {
//                newsService.getTopHeadlinesRemote(country.value, 1).toFlowable()
//            }
//            .map { response ->
//                response.body()?.articles?.toArticle(country = country.value)?.let { it }
//            }
//            .switchMap { articleList ->
//                newsService.insertAll(articleList).andThen(Flowable.just(1))
//            }
//            .switchMap {
//                newsService.getArticlesByCountry(country.value)
//            }
//            .map { articleList ->
//                articleList.distinctBy { listOf(it.title, it.publishedAt, it.author) }
//            }
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                showLog("On Next interval: ${it.size}")
//                isLoadingPage.mutableValue = false
//                if (it.isNotEmpty()) {
//                    articles.mutableValue = it
//                    successState()
//                }
//            }, {
//                showErrorLog("Error interval: $it")
//            })
//            .addToSubscription()

//        countryRX
//            .filter { charSequence ->
//                showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
//                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
//            }
//            .doOnNext { resetPageValue() }
//            .map { it.toString() }
//            .doAfterNext {
//                country.mutableValue = it
//            }
//            .subscribeOnIoObserveMain()
//            .subscribe({
//
//            }, {
//                showErrorLog("Error countryRX: $it")
//            })
//            .addToSubscription()

        if (networkService.isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
            pageRx.onNext(1)
            countryRX.onNext("ru")
            country.mutableValue = "ru"
            loadingState()
        } else {
            internetErrorState()
            showErrorLog("There is no Internet connection")
        }
    }

    fun deleteAllClicked() {
        topHeadlinesService
            .deleteAllByCountry()
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
        update(
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
    }

    private fun update(article: Article) {
        Log.d(LOG, "update: ${article.id}")
        topHeadlinesService
            .update(article)
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Update success")
            }, { error ->
                showErrorLog("Update error: $error")
            })
            .addToSubscription()
    }

    private fun resetPageValue() {
//        page.mutableValue = 1
        pageRx.onNext(1)
    }
}