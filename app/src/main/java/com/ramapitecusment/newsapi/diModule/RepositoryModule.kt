package com.ramapitecusment.newsapi.diModule

import android.content.Context
import com.ramapitecusment.newsapi.model.network.NewsApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    fun provideNewsRepository(api: NewsApi, context: Context, dao : CountriesDao): CountriesRepository {
        return NewsRepositoryImpl(api, context, dao)
    }
    single { provideNewsRepository(get(), androidContext(), get()) }

}