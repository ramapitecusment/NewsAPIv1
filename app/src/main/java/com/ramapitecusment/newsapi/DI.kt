package com.ramapitecusment.newsapi

import android.content.Context
import com.ramapitecusment.newsapi.scenes.everything.EverythingViewModel
import com.ramapitecusment.newsapi.scenes.newsDetails.NewsDetailsViewModel
import com.ramapitecusment.newsapi.scenes.readLater.ReadLaterViewModel
import com.ramapitecusment.newsapi.services.topHeadlines.TopHeadlinesService
import com.ramapitecusment.newsapi.scenes.topheadlines.TopHeadlinesViewModel
import com.ramapitecusment.newsapi.services.database.ArticleDatabase
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.NewsApiService
import com.ramapitecusment.newsapi.services.news.NewsService
import com.ramapitecusment.newsapi.services.readLater.ReadLaterService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initDI(context: Context) {
    startKoin {
        androidLogger()
        androidContext(context)
        modules(
            module {
                single { ArticleDatabase.getDatabase(context) }
                single { ArticleDatabase.getDatabase(context).articleDao() }

                single { NewsApiService() }
                single { NewsApiService().retrofitApi }
                single { NetworkService() }

                single { NewsService(get(), get()) }
                single { TopHeadlinesService(get(), get()) }
                single { EverythingService(get(), get()) }
                single { ReadLaterService(get(), get()) }

                viewModel { EverythingViewModel(get(), get()) }
                viewModel { TopHeadlinesViewModel(get(), get()) }
                viewModel { ReadLaterViewModel(get(), get()) }
                viewModel { NewsDetailsViewModel(get(), get()) }
            }
        )
    }
}