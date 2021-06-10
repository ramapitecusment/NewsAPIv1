package com.ramapitecusment.newsapi

import android.app.Application

class NewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initDI(applicationContext)
    }
}