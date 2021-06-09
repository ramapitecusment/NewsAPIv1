package com.ramapitecusment.newsapi.model.repository

import com.ramapitecusment.newsapi.model.network.Response
import io.reactivex.rxjava3.core.Maybe

interface NewsRepository {
    fun getAllNewsRemote(): Maybe<Response>
}