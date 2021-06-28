package com.ramapitecusment.newsapi.services.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.ramapitecusment.newsapi.common.PAGE_START_VALUE
import com.ramapitecusment.newsapi.services.database.Articles
import com.ramapitecusment.newsapi.services.network.ArticleMapper
import com.ramapitecusment.newsapi.services.news.NewsService
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class PagingSource(
    private val newsService: NewsService,
    private val mapper: ArticleMapper,
    private val searchTag: String
) : RxPagingSource<Int, Articles.Article>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Articles.Article>> {
        val pageIndex = params.key ?: PAGE_START_VALUE

        return newsService
            .getEverythingRemote(searchTag, pageIndex)
            .subscribeOn(Schedulers.io())
            .map { it.body()?.let { it1 -> mapper.transform(it1, searchTag = searchTag) } }
            .map { toLoadResult(it, pageIndex) }
            .onErrorReturn { LoadResult.Error(it) }
    }


    override fun getRefreshKey(state: PagingState<Int, Articles.Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun toLoadResult(
        data: Articles,
        position: Int
    ): LoadResult<Int, Articles.Article> {
        return LoadResult.Page(
            data.articles ?: listOf(),
            if (position == 1) null else position - 1,
            if (position == data.total) null else position + 1
        )
    }
}