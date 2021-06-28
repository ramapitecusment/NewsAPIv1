package com.ramapitecusment.newsapi.services.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxRemoteMediator
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleDao
import com.ramapitecusment.newsapi.services.network.NewsApi
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@ExperimentalPagingApi
class RemoteMediator(
    private val newsApi: NewsApi,
    private val articleDao: ArticleDao
) : RxRemoteMediator<Int, Article>() {

    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): Single<MediatorResult> {
        return Single.just(loadType)
            .subscribeOn(Schedulers.io())
            .map {
                when (it) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)

                        remoteKeys?.nextKey?.minus(1) ?: 1
                    }
                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                            ?: throw InvalidObjectException("Result is empty")

                        remoteKeys.prevKey ?: INVALID_PAGE
                    }
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state)
                            ?: throw InvalidObjectException("Result is empty")

                        remoteKeys.nextKey ?: INVALID_PAGE
                    }
                }
            }
            .flatMap { page ->
                if (page == INVALID_PAGE) {
                    Single.just(MediatorResult.Success(endOfPaginationReached = true))
                } else {
                    service.popularMovieRx(
                        apiKey = apiKey,
                        page = page,
                        language = locale.language
                    )
                        .map { mapper.transform(it, locale) }
                        .map { insertToDb(page, loadType, it) }
                        .map<MediatorResult> { MediatorResult.Success(endOfPaginationReached = it.endOfPage) }
                        .onErrorReturn { MediatorResult.Error(it) }
                }

            }
            .onErrorReturn { MediatorResult.Error(it) }

    }


    private fun insertToDb(page: Int, loadType: LoadType, data: Article): Article {

        try {
            if (loadType == LoadType.REFRESH) {
                database.movieRemoteKeysRxDao().clearRemoteKeys()
                database.moviesRxDao().clearMovies()
            }

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (data.endOfPage) null else page + 1
            val keys = data.movies.map {
                Movies.MovieRemoteKeys(movieId = it.movieId, prevKey = prevKey, nextKey = nextKey)
            }
            database.movieRemoteKeysRxDao().insertAll(keys)
            database.moviesRxDao().insertAll(data.movies)
            database.setTransactionSuccessful()

        } finally {
            database.endTransaction()
        }

        return data
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, Movies.Movie>): Movies.MovieRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { repo ->
            database.movieRemoteKeysRxDao().remoteKeysByMovieId(repo.movieId)
        }
    }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, Movies.Movie>): Movies.MovieRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { movie ->
            database.movieRemoteKeysRxDao().remoteKeysByMovieId(movie.movieId)
        }
    }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Movies.Movie>): Movies.MovieRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { id ->
                database.movieRemoteKeysRxDao().remoteKeysByMovieId(id)
            }
        }
    }

    companion object {
        const val INVALID_PAGE = -1
    }
}
