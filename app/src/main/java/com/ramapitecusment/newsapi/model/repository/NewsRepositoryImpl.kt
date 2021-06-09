package com.ramapitecusment.newsapi.model.repository

import android.content.Context
import com.ramapitecusment.newsapi.model.database.ArticleDao
import com.ramapitecusment.newsapi.model.network.NewsApi
import com.ramapitecusment.newsapi.model.network.Response
import io.reactivex.rxjava3.core.Maybe

class NewsRepositoryImpl(api: NewsApi, context: Context, dao: ArticleDao): NewsRepository {
    override fun getAllNewsRemote(): Maybe<Response> {

    }
}