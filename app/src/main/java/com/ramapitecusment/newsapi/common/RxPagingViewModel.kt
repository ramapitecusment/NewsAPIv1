package com.ramapitecusment.newsapi.common

import com.ramapitecusment.newsapi.common.AppConsts.Companion.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.common.mvvm.Data
import com.ramapitecusment.newsapi.common.mvvm.Visible
import io.reactivex.rxjava3.processors.PublishProcessor

open class RxPagingViewModel : BaseNewsViewModel() {

    var page = Data(1)
    var pageRx: PublishProcessor<Int> = PublishProcessor.create()
    var isPageEndRx: PublishProcessor<Boolean> = PublishProcessor.create()
    protected val isPageEnd = Visible(false)


    val isLoadingPage = Visible(false)
    val pageLoadingVisible = Visible(false)

    init {
        isPageEndRx
            .subscribeOnIoObserveMain()
            .subscribe({ isPageEnd ->
                showLog("isPageEnd --- $isPageEnd")
                this.isPageEnd.mutableValue = isPageEnd
            }, {
                showErrorLog("isPageEnd Error: $it")
            })
            .addToSubscription()

        pageRx
            .subscribeOnIoObserveMain()
            .subscribe({ page ->
                showLog("doOnNext pageRx: $page")
                this.page.mutableValue = page
                if (page == 1) {
                    loadingState()
                    isPageEndRx.onNext(false)
                } else pageLoadingState()
            }, {
                showLog("doOnError $it")
            })
            .addToSubscription()
    }

    fun increasePageValue() {
        showLog("${articles.value.size} - ${(articles.value.size / PAGE_SIZE_VALUE) + 1}")
        pageRx.onNext((articles.value.size / PAGE_SIZE_VALUE) + 1)
        isLoadingPage.mutableValue = true
    }

    override fun successState() {
        super.successState()
        pageLoadingVisible.mutableValue = false
    }

    protected fun pageLoadingState() {
        pageLoadingVisible.mutableValue = true
        recyclerViewVisible.mutableValue = true
        errorVisible.mutableValue = false
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
    }

    override fun loadingState() {
        super.loadingState()
        pageLoadingVisible.mutableValue = false
    }

    override fun errorState() {
        super.errorState()
        pageLoadingVisible.mutableValue = false
    }

    override fun internetErrorState() {
        super.internetErrorState()
        pageLoadingVisible.mutableValue = false
    }

}