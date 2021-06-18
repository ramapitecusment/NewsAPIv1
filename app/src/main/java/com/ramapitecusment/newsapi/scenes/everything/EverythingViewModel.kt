package com.ramapitecusment.newsapi.scenes.everything

import android.util.Log
import android.util.TimeUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.QUERY_DEFAULT
import com.ramapitecusment.newsapi.common.mvvm.*
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.Response
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Flowable.combineLatest
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.processors.ReplayProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


class EverythingViewModel(private val service: EverythingService) : BaseViewModel() {
    var searchTag = Text(QUERY_DEFAULT)
    var articles = DataList<ArticleEntity>()
    var page = Data(1)

//    val getFromRemoteBySearchTagAndPage: Flowable<retrofit2.Response<Response>>

    val isLoading = Visible(false)
    val isError = Visible(false)
    val isInternetError = Visible(false)
    val isPageLoading = Visible(false)

    //    init {
//        searchTag
//            .switchMap {
//                Log.d(LOG, "SearchTag: $search - $page")
//                search = it
//                _isError.postValue(false)
//                _isLoading.postValue(true)
//                everythingService.getArticlesBySearchTag(search)
//                    .subscribeOnIoObserveMain()
//            }
//            .distinct()
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                articles.onNext(it)
//                Log.d(LOG, "searchTag Articles on Next: ${it.size}")
//            }, {
//                Log.e(LOG, "searchTag Articles Error: $it")
//                _isError.postValue(true)
//                _isLoading.postValue(false)
//            })
//
//        pageObservable
//            .distinct()
//            .subscribeOnIoObserveMain()
//            .subscribe({
//                Log.d(LOG, "pageObservable: $search - $page")
//                page = it
//                _isError.postValue(false)
//                _isPageLoading.postValue(true)
//            }, {
//                Log.e(LOG, "pageObservable Error: $it")
//                _isError.postValue(true)
//                _isLoading.postValue(false)
//                _isPageLoading.postValue(false)
//            })
//
//        getFromRemoteBySearchTagAndPage =
//            combineLatest(searchTag.value, pageObservable) { t1, t2 ->
//                search = t1
//                page = t2
//                everythingService.getArticlesBySearchTag(t1)
//                    .subscribeOnIoObserveMain()
//                everythingService
//                    .getFromRemote(t1, t2)
//                    .subscribeOnIoObserveMain()
//            }
//                .switchMap {
//                    it.toFlowable()
//                }
//                .distinct()
//                .subscribeOnIoObserveMain()
//                .doOnNext {
//                    Log.d(LOG, "getFromRemote Response: $it")
//                    if (it.isSuccessful) {
//                        _isInternetError.postValue(false)
//                        it.body()?.let { body ->
//                            Log.d(LOG, "isSuccessful Response: ${body.articles?.size}")
//                            body.articles?.toArticleEntity(search)?.let { it1 -> insertAll(it1) }
//                        }
//                    } else {
//                        if (page == 1) {
//                            _isError.postValue(true)
//                            _isLoading.postValue(false)
//                        }
//                    }
//                }
//                .doOnError {
//                    Log.e(LOG, "Internet Error: $it")
//                    isInternetError.mutableValue = true
//                }
//                .doOnComplete {
//                    Log.d(LOG, "Internet Completed")
//                }
//    }
//

    init {
        Flowable.just()
        service.getArticlesBySearchTag(searchTag.value)
            .subscribeOnIoObserveMain()
            .doOnNext {
                showLog("OnNext getArticlesBySearchTag: ${it.size}")
            }
            .doOnError {
                showLog("Error getArticlesBySearchTag: $it")
            }
            .doOnCancel {
                showLog("Cancel getArticlesBySearchTag")
            }
            .doOnComplete {
                showLog("Complete getArticlesBySearchTag")
            }
    }

    private fun getFromRemote() {
        service.getFromRemote(searchTag.value, page.value).subscribeOnIoObserveMain()
            .doOnSuccess { responce ->
                if (responce.isSuccessful) {
                    showLog("Get from remote success: ${responce.body()?.articles?.size}")
                } else {
                    showErrorLog("Got error from the server: $responce")
                }
            }
            .doOnError { error ->
                showErrorLog("Error getFromRemote: $error")
            }
            .doOnComplete {
                showLog("Complete getFromRemote")
            }
            .subscribe().addToSubscription()
    }

    private fun insertAll(articles: List<ArticleEntity>) {
        service.insertAll(articles).subscribeOnIoObserveMain()
            .doOnComplete {
                showLog("Insert Complete")
            }
            .doOnError { error ->
                showErrorLog("Insert error: $error")
            }
            .subscribe().addToSubscription()
    }

    fun deleteAll() {
        service.deleteAll().subscribeOnIoObserveMain()
            .doOnComplete {
                showLog("Delete success")
            }
            .doOnError { error ->
                showErrorLog("Delete error: $error")
            }
            .subscribe().addToSubscription()
    }

    override fun onCleared() {
        super.onCleared()
        // TODO Когда необходимо вызвать stop() и destroy()
        destroy()
    }
}