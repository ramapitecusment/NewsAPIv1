package com.ramapitecusment.newsapi.scenes.everything

import android.text.TextUtils
import android.util.Log
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.DataList
import com.ramapitecusment.newsapi.common.mvvm.Text
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.toArticle
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.processors.PublishProcessor

class EverythingViewModel(private val newsService: NewsService, networkService: NetworkService) :
    BaseNewsViewModel() {

    var articles = DataList<Article>()
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
                response.body()?.articles?.toArticle(searchTag.value)?.let { it }
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

        searchTagRX
            .filter { charSequence ->
                !(TextUtils.isEmpty(charSequence.trim { it <= ' ' })) && !internetErrorVisible.value
            }
            .map { it.toString() }
            .distinctUntilChanged()
            .switchMap {
                newsService.getArticlesBySearchTag(searchTag.value)
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
    }

    fun deleteAllClicked() {
        newsService
            .deleteAll()
            .subscribeOnIoObserveMain()
            .subscribe({
                showLog("Delete success")
                resetPageValue()
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

    fun increasePageValue() {
        showLog("${articles.value.size} - ${(articles.value.size / PAGE_SIZE_VALUE) + 1}")
        page.mutableValue = (articles.value.size / PAGE_SIZE_VALUE) + 1
        pageRx.onNext((articles.value.size / PAGE_SIZE_VALUE) + 1)
        isLoadingPage.mutableValue = true
    }

}