package com.ramapitecusment.newsapi.services.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxRemoteMediator
import com.ramapitecusment.newsapi.services.database.Article
import io.reactivex.rxjava3.core.Single

@ExperimentalPagingApi
class RemoteMediator : RxRemoteMediator<Int, Article>() {
    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): Single<MediatorResult> {
        TODO("Not yet implemented")
    }
}