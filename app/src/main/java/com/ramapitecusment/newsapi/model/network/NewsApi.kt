package com.ramapitecusment.newsapi.model.network

import com.ramapitecusment.newsapi.util.*
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET(EVERYTHING)
    fun getEverythingRemote(@Query(QUERY) q: String, @Query(API_KEY) api_key: String)

    @GET(TOP_HEADLINES)
    fun getTopHeadlinesRemote(@Query(COUNTRY) country: String, @Query(API_KEY) api_key: String)
}