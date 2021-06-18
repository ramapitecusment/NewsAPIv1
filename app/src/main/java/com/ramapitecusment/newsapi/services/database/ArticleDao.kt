package com.ramapitecusment.newsapi.services.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

@Dao
interface ArticleDao {

//  ################################################################################################
//  ###################################     Articles      ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: ArticleEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllArticles(articles: List<ArticleEntity>): Completable

    @Update
    fun updateArticle(article: ArticleEntity): Completable

    @Delete
    fun deleteArticle(article: ArticleEntity): Completable

    @Query("DELETE FROM news_table")
    fun deleteAllArticles(): Completable

    @Query("SELECT * FROM news_table")
    fun getAllArticles(): Flowable<List<ArticleEntity>>

    @Query("SELECT * FROM news_table WHERE searchTag =:q")
    fun getArticlesBySearchTag(q: String): Flowable<List<ArticleEntity>>

//  ################################################################################################
//  ###################################   Top Headlines   ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopHeadline(topHeadline: ArticleTopHeadline): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTopHeadlines(topHeadlines: List<ArticleTopHeadline>): Completable

    @Update
    fun updateTopHeadline(topHeadline: ArticleTopHeadline): Completable

    @Delete
    fun deleteTopHeadline(topHeadline: ArticleTopHeadline): Completable

    @Query("DELETE FROM top_headlines_table")
    fun deleteAllTopHeadlines(): Completable

    @Query("SELECT * FROM top_headlines_table")
    fun getAllTopHeadlines(): Flowable<List<ArticleTopHeadline>>

    @Query("SELECT * FROM top_headlines_table WHERE country =:country")
    fun getTopHeadlinesByCountry(country: String): Flowable<List<ArticleTopHeadline>>


//  ################################################################################################
//  ###################################     Read Later    ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReadLater(readLater: ReadLater): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReadLater(topHeadlines: List<ArticleTopHeadline>): Completable

    @Update
    fun updateReadLater(readLater: ReadLater): Completable

    @Delete
    fun deleteReadLater(readLater: ReadLater): Completable

    @Query("DELETE FROM read_later_table")
    fun deleteAllReadLater(): Completable

    @Query("SELECT * FROM read_later_table")
    fun getAllReadLater(): LiveData<List<ReadLater>>
}