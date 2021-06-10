package com.ramapitecusment.newsapi.services.database

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface ArticleDao {

//  ################################################################################################
//  ###################################     Articles      ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: ArticleEntity): Maybe<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllArticles(articles: List<ArticleEntity>): Maybe<List<Long>>

    @Update
    fun updateArticle(article: ArticleEntity): Maybe<Int>

    @Delete
    fun deleteArticle(article: ArticleEntity): Maybe<Int>

    @Query("DELETE FROM news_table")
    fun deleteAllArticles(): Completable

    @Query("SELECT * FROM news_table")
    fun getAllArticles(): Maybe<List<ArticleEntity>>

    @Query("SELECT * FROM news_table WHERE searchTag =:q")
    fun getArticlesBySearchTag(q: String): Maybe<List<ArticleEntity>>

//  ################################################################################################
//  ###################################   Top Headlines   ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopHeadline(topHeadline: ArticleTopHeadline): Maybe<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTopHeadlines(topHeadlines: List<ArticleTopHeadline>): Maybe<List<Long>>

    @Update
    fun updateTopHeadline(topHeadline: ArticleTopHeadline): Maybe<Int>

    @Delete
    fun deleteTopHeadline(topHeadline: ArticleTopHeadline): Maybe<Int>

    @Query("DELETE FROM top_headlines_table")
    fun deleteAllTopHeadlines(): Completable

    @Query("SELECT * FROM top_headlines_table")
    fun getAllTopHeadlines(): Maybe<List<ArticleTopHeadline>>

    @Query("SELECT * FROM top_headlines_table WHERE country =:country")
    fun getArticlesByTopHeadline(country: String): Maybe<List<ArticleTopHeadline>>


//  ################################################################################################
//  ###################################     Read Later    ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReadLater(readLater: ReadLater): Maybe<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReadLater(topHeadlines: List<ArticleTopHeadline>): Maybe<List<Long>>

    @Update
    fun updateReadLater(readLater: ReadLater): Maybe<Int>

    @Delete
    fun deleteReadLater(readLater: ReadLater): Maybe<Int>

    @Query("DELETE FROM read_later_table")
    fun deleteAllReadLater(): Completable

    @Query("SELECT * FROM read_later_table")
    fun getAllReadLater(): Maybe<List<ReadLater>>
}