package com.ramapitecusment.newsapi

import android.content.Context
import com.ramapitecusment.newsapi.diModule.*
import com.ramapitecusment.newsapi.scenes.everything.EverythingViewModel
import com.ramapitecusment.newsapi.services.database.ArticleDatabase
import com.ramapitecusment.newsapi.services.everything.EverythingService
import com.ramapitecusment.newsapi.services.network.NewsApi
import com.ramapitecusment.newsapi.services.network.NewsApiService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initDI(context: Context) {
    startKoin {
        androidContext(context)
        modules(
            apiModule,
            repositoryModule,
            networkModule,
            viewModelModule,
            databaseModule
//            module {
//
//                single { ArticleDatabase.getDatabase(context) }
//                single { NewsApiService() }
//                single { EverythingService(get(), androidContext(), get()) }
//
//                viewModel { EverythingViewModel(get()) }
//            }
        )
    }
}
