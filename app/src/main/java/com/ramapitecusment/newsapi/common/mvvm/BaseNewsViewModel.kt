package com.ramapitecusment.newsapi.common.mvvm

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.services.database.Article
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseNewsViewModel : BaseViewModel() {

    var articles = DataList<Article>()
    var page = Data(1)
    var pageRx: PublishProcessor<Int> = PublishProcessor.create()
    var isPageEndRx: PublishProcessor<Boolean> = PublishProcessor.create()
    protected val isPageEnd = Visible(false)

    val isLoadingPage = Visible(false)

    val loadingVisible = Visible(false)
    val errorVisible = Visible(false)
    val internetErrorVisible = Visible(false)
    val pageLoadingVisible = Visible(false)
    val recyclerViewVisible = Visible(false)


    fun increasePageValue() {
        showLog("${articles.value.size} - ${(articles.value.size / PAGE_SIZE_VALUE) + 1}")
        page.mutableValue = (articles.value.size / PAGE_SIZE_VALUE) + 1
        pageRx.onNext((articles.value.size / PAGE_SIZE_VALUE) + 1)
        isLoadingPage.mutableValue = true
    }

    protected fun successState() {
        recyclerViewVisible.mutableValue = true
        errorVisible.mutableValue = false
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
    }

    protected fun pageLoadingState() {
        pageLoadingVisible.mutableValue = true
        recyclerViewVisible.mutableValue = true
        errorVisible.mutableValue = false
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
    }

    protected fun loadingState() {
        loadingVisible.mutableValue = true
        internetErrorVisible.mutableValue = false
        errorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    protected fun errorState() {
        errorVisible.mutableValue = true
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    protected fun internetErrorState() {
        internetErrorVisible.mutableValue = true
        loadingVisible.mutableValue = false
        errorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }
}