package com.ramapitecusment.newsapi.services.network

import com.ramapitecusment.newsapi.common.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.internal.operators.maybe.MaybeFromRunnable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val api = Retrofit.Builder()
    .baseUrl(URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .build()

interface NewsApi {

    @GET(EVERYTHING)
    fun getEverythingRemote(
        @Query(QUERY) q: String,
        @Query(API_KEY) apiKey: String,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(PAGE) page: Int
    ): Maybe<retrofit2.Response<Response>>

    @GET(TOP_HEADLINES)
    fun getTopHeadlinesRemote(
        @Query(COUNTRY) country: String,
        @Query(API_KEY) apiKey: String,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(PAGE) page: Int
    ): Maybe<retrofit2.Response<Response>>
}

class NewsApiService {
    val retrofitApi: NewsApi by lazy {
        api.create(NewsApi::class.java)
    }
}