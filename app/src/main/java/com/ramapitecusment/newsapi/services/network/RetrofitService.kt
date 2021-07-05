package com.ramapitecusment.newsapi.services.network

import com.ramapitecusment.newsapi.common.AppConsts.Companion.API_KEY
import com.ramapitecusment.newsapi.common.AppConsts.Companion.COUNTRY
import com.ramapitecusment.newsapi.common.AppConsts.Companion.EVERYTHING
import com.ramapitecusment.newsapi.common.AppConsts.Companion.PAGE
import com.ramapitecusment.newsapi.common.AppConsts.Companion.PAGE_SIZE
import com.ramapitecusment.newsapi.common.AppConsts.Companion.QUERY
import com.ramapitecusment.newsapi.common.AppConsts.Companion.TOP_HEADLINES
import com.ramapitecusment.newsapi.common.AppConsts.Companion.URL
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
private val client = OkHttpClient.Builder().addInterceptor(logging).build()

private val api = Retrofit.Builder()
    .baseUrl(URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .client(client)
    .build()

interface NewsApi {

    @GET(EVERYTHING)
    fun getEverythingRemote(
        @Query(QUERY) q: String,
        @Query(API_KEY) apiKey: String,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(PAGE) page: Int
    ): Single<retrofit2.Response<Response>>

    @GET(TOP_HEADLINES)
    fun getTopHeadlinesRemote(
        @Query(COUNTRY) country: String,
        @Query(API_KEY) apiKey: String,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(PAGE) page: Int
    ): Single<retrofit2.Response<Response>>
}

class NewsApiService {
    val retrofitApi: NewsApi by lazy {
        api.create(NewsApi::class.java)
    }
}