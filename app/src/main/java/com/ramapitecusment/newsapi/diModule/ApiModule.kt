package com.ramapitecusment.newsapi.diModule

import com.ramapitecusment.newsapi.services.network.NewsApi
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {

    fun provideNewsApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }
    single { provideNewsApi(get()) }

}