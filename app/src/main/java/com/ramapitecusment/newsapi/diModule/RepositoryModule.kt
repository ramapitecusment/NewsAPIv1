package com.ramapitecusment.newsapi.diModule

import android.content.Context
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.everything.EverythingService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    fun provideNewsRepository(api: NewsApi, context: Context, dao: ArticleDao): EverythingService {
        return EverythingService(api, context, dao)
    }
    single { provideNewsRepository(get(), androidContext(), get()) }

}