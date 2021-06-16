package com.ramapitecusment.newsapi.scenes.topheadlines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramapitecusment.newsapi.common.COUNTRY_DEFAULT_VALUE
import com.ramapitecusment.newsapi.common.LOG
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.database.ArticleTopHeadline
import com.ramapitecusment.newsapi.services.network.Response
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import com.ramapitecusment.newsapi.services.network.toArticleTopHeadline
import com.ramapitecusment.newsapi.services.topheadlines.TopHeadlinesService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers

class TopHeadlinesViewModel(private val service: TopHeadlinesService) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    var articles: PublishProcessor<List<ArticleTopHeadline>> = PublishProcessor.create()
    var pageObservable: PublishProcessor<Int> = PublishProcessor.create()
    var countryObservable: PublishProcessor<String> = PublishProcessor.create()

    val getFromRemoteByCountryAndPage: Flowable<retrofit2.Response<Response>>

    private var page = 1
    private var country = COUNTRY_DEFAULT_VALUE

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
        countryObservable
            .switchMap {
                country = it
                _isError.postValue(false)
                _isLoading.postValue(true)
                service.getAllByCountry(country)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                articles.onNext(it)
                Log.d(LOG, "searchTag Articles on Next: ${it.size}")
            }, {
                Log.e(LOG, "searchTag Articles Error: $it")
                _isError.postValue(true)
                _isLoading.postValue(false)
            })

        pageObservable
            .distinct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(LOG, "pageObservable: $country - $page")
                page = it
                _isError.postValue(false)
                _isPageLoading.postValue(true)
            }, {
                Log.e(LOG, "pageObservable Error: $it")
                _isError.postValue(true)
                _isLoading.postValue(false)
            })


        getFromRemoteByCountryAndPage = Flowable
            .combineLatest(countryObservable, pageObservable) { t1, t2 ->
                country = t1
                page = t2
                service.getFromRemote(t1, t2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }.switchMap {
                it.toFlowable()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.d(LOG, "getFromRemote Response: $it")
                if (it.isSuccessful) {
                    _isInternetError.postValue(false)
                    it.body()?.let { body ->
                        Log.d(LOG, "isSuccessful Response: ${body.articles?.size}")
                        body.articles?.toArticleTopHeadline(country)?.let { it1 -> insertAll(it1) }
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
    }

    private fun insertAll(articles: List<ArticleTopHeadline>) {
        val disposable = service.insertAll(articles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(LOG, "Insert success: $it")
                if (it.isEmpty()) {
                    if (page == 1) {
                        _isError.postValue(true)
                        _isLoading.postValue(false)
                    } else if (page != 1) {

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
        val disposable = service.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(LOG, "Delete success")
            }, {
                Log.e(LOG, "Delete error: $it")
            })
        compositeDisposable.add(disposable)
    }

    fun onDestroy() {

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}