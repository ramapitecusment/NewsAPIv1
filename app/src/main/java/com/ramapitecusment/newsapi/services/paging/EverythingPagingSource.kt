package com.ramapitecusment.newsapi.services.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.ramapitecusment.newsapi.common.PAGE_SIZE_VALUE
import com.ramapitecusment.newsapi.common.PAGE_START_VALUE
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.network.Response
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class EverythingPagingSource(
    private val newsService: NewsService,
    private val searchTag: String
) :
    RxPagingSource<Int, Article>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Article>> {
        val pageIndex = params.key ?: PAGE_START_VALUE

        return newsService
            .getArticlesBySearchTag(searchTag, pageIndex)
            .subscribeOn(Schedulers.io())
            .map { it.body()?.articles.toA }
            .map { toLoadResult(it, pageIndex) }
            .onErrorReturn { LoadResult.Error(it) }
    }


    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun toLoadResult(
        data: retrofit2.Response<Response>,
        position: Int
    ): LoadResult<Int, Article> {
        return LoadResult.Page(
            data.body()?.articles ?: listOf(),
            if (position == 1) null else position - 1,
            if (position == data.body()?.totalResults?.div(PAGE_SIZE_VALUE) ?: 1) null else position + 1
        )
    }
}