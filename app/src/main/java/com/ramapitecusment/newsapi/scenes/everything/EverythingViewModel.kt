package com.ramapitecusment.newsapi.scenes.everything

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.QUERY_DEFAULT
import com.ramapitecusment.newsapi.common.mvvm.*
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleEntity
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.toArticleEntity
import io.reactivex.rxjava3.core.Flowable


class EverythingViewModel(private val service: EverythingService) : BaseViewModel() {
    var searchTag = Text(QUERY_DEFAULT)
    var articlesEntity = DataList<ArticleEntity>()
    var articles = DataList<Article>()
    var page = Data(1)

    val loadingVisible = Visible(false)
    val errorVisible = Visible(false)
    val internetErrorVisible = Visible(false)
    val pageLoadingVisible = Visible(false)
    val recyclerViewVisible = Visible(false)

    fun init() {
        initDatabaseFlowable()
        if (isInternetAvailable(MainApplication.instance)) {
            showLog("Connected to internet")
            getFromRemote()
        } else {
            internetErrorState()
            showErrorLog("There os no Internet connection")
        }
    }

    private fun initDatabaseFlowable() {
        Flowable.just(searchTag.value)
            .switchMap { search ->
                service.getArticlesBySearchTag(search)
                    .subscribeOnIoObserveMain()
                    .doOnNext {
                        showLog("OnNext getArticlesBySearchTag: ${it.size}")
                    }
                    .doOnError {
                        showErrorLog("Error getArticlesBySearchTag: $it")
                    }
                    .doOnCancel {
                        showLog("Cancel getArticlesBySearchTag")
                    }
                    .doOnComplete {
                        showLog("Complete getArticlesBySearchTag")
                    }
            }.subscribe().addToSubscription()
    }

    private fun getFromRemote() {
        service.getFromRemote(searchTag.value, page.value).subscribeOnIoObserveMain()
            .doOnSuccess { response ->
                if (response.isSuccessful) {
                    showLog("Get from remote success: ${response.body()?.articles?.size}")
                    response.body()?.articles?.let { insertAll(it.toArticleEntity(searchTag.value)) }
                } else {
                    showErrorLog("Got error from the server: $response")
                    errorState()
                }
            }
            .doOnError { error ->
                showErrorLog("Error getFromRemote: $error")
                internetErrorState()
            }
            .doOnComplete {
                showLog("Complete getFromRemote")
            }
            .doOnSubscribe {
                loadingState()
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

    private fun successState() {
        recyclerViewVisible.mutableValue = true
        errorVisible.mutableValue = false
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
    }

    private fun pageLoadingState() {
        pageLoadingVisible.mutableValue = true
        recyclerViewVisible.mutableValue = true
        errorVisible.mutableValue = false
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
    }

    private fun loadingState() {
        loadingVisible.mutableValue = true
        internetErrorVisible.mutableValue = false
        errorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    private fun errorState() {
        errorVisible.mutableValue = true
        loadingVisible.mutableValue = false
        internetErrorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    private fun internetErrorState() {
        internetErrorVisible.mutableValue = true
        loadingVisible.mutableValue = false
        errorVisible.mutableValue = false
        pageLoadingVisible.mutableValue = false
        recyclerViewVisible.mutableValue = false
    }

    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }
}


//    val getFromRemoteBySearchTagAndPage: Flowable<retrofit2.Response<Response>>

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