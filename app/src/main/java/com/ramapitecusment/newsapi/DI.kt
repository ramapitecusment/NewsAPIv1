package com.ramapitecusment.newsapi

import android.content.Context
import com.ramapitecusment.newsapi.scenes.everything.EverythingViewModel
import com.ramapitecusment.newsapi.scenes.newsDetails.NewsDetailsViewModel
import com.ramapitecusment.newsapi.scenes.readLater.ReadLaterViewModel
import com.ramapitecusment.newsapi.scenes.topheadlines.TopHeadlinesViewModel
import com.ramapitecusment.newsapi.services.database.ArticleDatabase
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.NetworkService
import com.ramapitecusment.newsapi.services.network.NewsApiService
import com.ramapitecusment.newsapi.services.newsdetails.NewsDetailsService
import com.ramapitecusment.newsapi.services.readLater.ReadLaterService
import com.ramapitecusment.newsapi.services.topheadlines.TopHeadlinesService
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
                single { ArticleDatabase.getDatabase(context).databaseDao() }

                single { NewsApiService() }
                single { NewsApiService().retrofitApi }
                single { NetworkService() }

                single { EverythingService(get(), get()) }
                single { TopHeadlinesService(get(), get()) }
                single { ReadLaterService(get()) }
                single { NewsDetailsService(get(), get()) }

                viewModel { EverythingViewModel(get(), get(), get()) }
                viewModel { TopHeadlinesViewModel(get(), get(), get()) }
                viewModel { ReadLaterViewModel(get(), get()) }
                viewModel { NewsDetailsViewModel(get(), get(), get()) }
            }
        )
    }
}