package com.ramapitecusment.newsapi.scenes.readLater

import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.common.mvvm.BaseNewsViewModel
import com.ramapitecusment.newsapi.services.readLater.ReadLaterService

class ReadLaterViewModel(private val service: ReadLaterService) : BaseNewsViewModel() {


    fun deleteAll() {
        service.deleteAll().subscribeOnIoObserveMain().subscribe(
            {
                showLog("Delete success")
            }, { error ->
                showErrorLog("Delete error: $error")
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