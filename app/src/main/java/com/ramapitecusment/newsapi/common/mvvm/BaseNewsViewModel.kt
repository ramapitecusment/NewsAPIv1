package com.ramapitecusment.newsapi.common.mvvm

import com.ramapitecusment.newsapi.services.database.Article

abstract class BaseNewsViewModel : BaseViewModel() {

    var articles = DataList<Article>()

    val loadingVisible = Visible(false)
    val errorVisible = Visible(false)
    val internetErrorVisible = Visible(false)
    val recyclerViewVisible = Visible(false)

    protected open fun successState() {
        recyclerViewVisible.mutableValue = true
        errorVisible.mutableValue = false
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
    }

    protected open fun loadingState() {
        loadingVisible.mutableValue = true
        internetErrorVisible.mutableValue = false
        errorVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    protected open fun errorState() {
        errorVisible.mutableValue = true
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    protected open fun internetErrorState() {
        internetErrorVisible.mutableValue = true
        loadingVisible.mutableValue = false
        errorVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }
}