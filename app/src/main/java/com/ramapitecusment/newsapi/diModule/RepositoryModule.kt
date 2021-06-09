package com.ramapitecusment.newsapi.diModule

import android.content.Context
import com.ramapitecusment.newsapi.model.database.ArticleDao
import com.ramapitecusment.newsapi.model.network.NewsApi
import com.ramapitecusment.newsapi.model.repository.NewsRepository
import com.ramapitecusment.newsapi.model.repository.NewsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    fun provideNewsRepository(api: NewsApi, context: Context, dao: ArticleDao): NewsRepository {
        return NewsRepositoryImpl(api, context, dao)
    }
    single { provideNewsRepository(get(), androidContext(), get()) }

}