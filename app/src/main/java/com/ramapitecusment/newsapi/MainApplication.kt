package com.ramapitecusment.newsapi

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        initDI(this@MainApplication)
    }

    companion object {
        lateinit var instance: MainApplication
    }
}