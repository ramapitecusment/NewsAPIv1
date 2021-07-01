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

class TopHeadlinesViewModel(
    private val newsService: NewsService,
    private val networkService: NetworkService
) : BaseNewsViewModel() {
    private var country = Text()
    private var countryRX: PublishProcessor<String> = PublishProcessor.create()

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
                if (page == 1) {
                    loadingState()
                    isPageEndRx.onNext(false)
                } else pageLoadingState()
            }
            .doOnError {
                showLog("doOnError $it")
            }
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
                newsService.getTopHeadlinesRemote(country.value, page.value).toFlowable()
            }
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
            .map { response ->
                response.body()?.articles?.toArticle(country.value)?.let { it }
            }
            .switchMap { articleList ->
                newsService.insertAll(articleList).toFlowable<Unit>()
            }
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Insert Complete")
            }, {
                showErrorLog("Insert Error: $it")
            })
            .addToSubscription()

        Flowable
            .interval(5, TimeUnit.SECONDS)
            .switchMap {
                newsService.getTopHeadlinesRemote(country.value, 1).toFlowable()
            }
            .doOnNext {
                showLog("$it")
            }
            .map { response ->
                response.body()?.articles?.toArticle(country.value)?.let { it }
            }
            .switchMap { articleList ->
                newsService.insertAll(articleList).toFlowable<Unit>()
            }
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Insert Complete")
            }, {
                showErrorLog("Insert Error: $it")
            })
            .addToSubscription()

        countryRX
            .filter { charSequence ->
                showLog("before filter countryRX -$charSequence- ${internetErrorVisible.value}")
                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
            }
            .switchMap {
                newsService.getArticlesByCountry(it)
            }
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("On Next interval: ${it.size}")
                isLoadingPage.mutableValue = false
                if (it.isNotEmpty()) {
                    articles.mutableValue = it.distinct()
                    successState()
                }
            }, {
                showErrorLog("Error interval: $it")
            })
            .addToSubscription()
    }

    private fun insert(articles: List<Article>) {
        newsService.insertAll(articles)
            .subscribeOnIoObserveMain()
            .subscribe(
                {
                    showLog("Insert Complete")
                }, { error ->
                    showErrorLog("Insert error: $error")
                }).addToSubscription()
    }

    fun deleteAllClicked() {
        newsService
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

    private fun resetPageValue() {
        page.mutableValue = 1
        pageRx.onNext(1)
    }
}