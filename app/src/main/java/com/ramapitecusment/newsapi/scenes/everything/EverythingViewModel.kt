package com.ramapitecusment.newsapi.scenes.everything

import android.util.Log
import android.util.TimeUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.common.QUERY_DEFAULT
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


class EverythingViewModel(private val everythingService: EverythingService) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    var searchTag: PublishProcessor<String> = PublishProcessor.create()
    var articles: PublishProcessor<List<ArticleEntity>> = PublishProcessor.create()

    //    var articlesSearch: PublishProcessor<List<ArticleEntity>> = PublishProcessor.create()
//    var articlesDynamic: PublishProcessor<List<ArticleEntity>> = PublishProcessor.create()
    var pageObservable: PublishProcessor<Int> = PublishProcessor.create()

//    val getFromRemoteBySearchTagAndPage: Disposable
    val getFromRemoteBySearchTagAndPage: Flowable<retrofit2.Response<Response>>

    private var search = QUERY_DEFAULT
    private var page = 1

    private val _isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    val isError: LiveData<Boolean>
        get() = _isError

    private val _isInternetError: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    val isInternetError: LiveData<Boolean>
        get() = _isInternetError

    private val _isPageLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    val isPageLoading: LiveData<Boolean>
        get() = _isPageLoading

    init {
//        searchTag
//            .switchMap {
//                Log.d(LOG, "SearchTag: $search - $page")
//                search = it
////                getFromRemote(search, page)
//                _isError.postValue(false)
//                _isLoading.postValue(true)
//                everythingService.getArticlesBySearchTag(search)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread()).onBackpressureBuffer()
//            }
//            .distinct()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                articles.onNext(it)
////                articlesSearch.onNext(it)
//                Log.d(LOG, "searchTag Articles on Next: ${it.size}")
//            }, {
//                Log.e(LOG, "searchTag Articles Error: $it")
//                _isError.postValue(true)
//                _isLoading.postValue(false)
//            })
//
//        pageObservable
//            .switchMap {
//                Log.d(LOG, "pageObservable: $search - $page")
//                page = it
//                _isError.postValue(false)
//                _isPageLoading.postValue(true)
////                getFromRemote(search, page)
//                everythingService.getArticlesBySearchTag(search)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//            }
//            .distinct()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                articles.onNext(it)
////                articlesDynamic.onNext(it)
//                Log.d(LOG, "pageObservable Articles on Next: ${it.size}")
//            }, {
//                Log.e(LOG, "pageObservable Articles Error: $it")
//                _isError.postValue(true)
//                _isLoading.postValue(false)
//            })

        getFromRemoteBySearchTagAndPage =
            combineLatest(searchTag, pageObservable) { t1, t2 ->
                search = t1
                page = t2
                everythingService.getArticlesBySearchTag(t1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                everythingService
                    .getFromRemote(t1, t2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
                .switchMap {
                    it.toFlowable()
                }
                .distinct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    Log.d(LOG, "getFromRemote Response: $it")
                    if (it.isSuccessful) {
                        _isInternetError.postValue(false)
                        it.body()?.let { body ->
                            Log.d(LOG, "isSuccessful Response: ${body.articles?.size}")
                            body.articles?.toArticleEntity(search)?.let { it1 -> insertAll(it1) }
                        }
                    } else {
                        if (page == 1) {
                            _isError.postValue(true)
                            _isLoading.postValue(false)
                        }
                    }
                }
                .doOnError {
                    Log.e(LOG, "Internet Error: $it")
                    _isInternetError.postValue(true)
                }
                .doOnComplete {
                    Log.d(LOG, "Internet Completed")
                }
//                .subscribe()
    }

//    private fun getFromRemoteMaybe(search: String, page: Int): Maybe<retrofit2.Response<Response>> =
//        everythingService.getFromRemote(search, page)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//
//    val articlesByTag: Flowable<List<ArticleEntity>> =
//        everythingService.getArticlesBySearchTag(search)
//            .switchMap {
//                // TODO Как не вызывать один и тот же объект дважды?
//                everythingService.getArticlesBySearchTag(search)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext {
//                Log.d(LOG, "Articles on Next: ${it.size}")
//            }
//            .doOnComplete {
//                Log.d(LOG, "Articles Completed")
//            }
//            .doOnError {
//                Log.e(LOG, "Articles Error: $it")
//                _isError.postValue(true)
//                _isLoading.postValue(false)
//            }
//            .doOnSubscribe {
//                Log.d(LOG, "Articles doOnSubscribe")
//                _isLoading.postValue(true)
//                _isError.postValue(false)
//            }

//    fun getFromRemote(searchTag: String, page: Int) {
//        val disposable = everythingService.getFromRemote(searchTag, page)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                Log.d(LOG, "getFromRemote Response: $it")
//                if (it.isSuccessful) {
//                    _isInternetError.postValue(false)
//                    it.body()?.let { body ->
//                        Log.d(LOG, "isSuccessful Response: ${body.articles?.size}")
//                        body.articles?.toArticleEntity(searchTag)?.let { it1 -> insertAll(it1) }
//                    }
//                } else {
//                    if (page == 1) {
//                        _isError.postValue(true)
//                        _isLoading.postValue(false)
//                    }
//                }
//            }, {
//                Log.e(LOG, "Internet Error: $it")
//                _isInternetError.postValue(true)
//            }, {
//                Log.d(LOG, "Internet Completed")
//            })
//        compositeDisposable.add(disposable)
//    }

    private fun insertAll(articles: List<ArticleEntity>) {
        val disposable = everythingService.insertAll(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(LOG, "Insert success: $it")
                if (it.isEmpty()) {
                    if (page == 1) {
                        _isError.postValue(true)
                        _isLoading.postValue(false)
                    }
                }
            }, {
                Log.e(LOG, "Insert error: $it")
            }, {
                Log.d(LOG, "Insert complete")
            })
        compositeDisposable.add(disposable)
    }

    fun deleteAll() {
        val disposable = everythingService.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(LOG, "Delete success")
            }, {
                Log.e(LOG, "Delete error: $it")
            })
        compositeDisposable.add(disposable)
    }

    fun destroy() {
        // Using clear will clear all, but can accept new disposable
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        deleteAll()
//        getFromRemoteBySearchTagAndPage.dispose()
        // Using dispose will clear all and set isDisposed = true, so it will not accept any new disposable
        compositeDisposable.dispose()
    }
}