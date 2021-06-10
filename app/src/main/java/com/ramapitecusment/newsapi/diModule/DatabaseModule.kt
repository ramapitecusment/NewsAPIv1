package com.ramapitecusment.newsapi.diModule

import android.app.Application
import androidx.room.Room
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.database.ArticleDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    fun provideDatabase(application: Application): ArticleDatabase {
        return Room.databaseBuilder(application, ArticleDatabase::class.java, "countries")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideCountriesDao(database: ArticleDatabase): ArticleDao {
        return  database.databaseDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideCountriesDao(get()) }
}