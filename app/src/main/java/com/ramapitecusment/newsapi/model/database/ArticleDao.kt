package com.ramapitecusment.newsapi.model.database

import androidx.room.*
import io.reactivex.rxjava3.core.Maybe

@Dao
interface ArticleDao {

//  ################################################################################################
//  ###################################     Articles      ##########################################
//  ################################################################################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article): Maybe<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllArticles(articles: List<Article>): Maybe<List<Long>>

    @Update
    fun updateArticle(article: Article): Maybe<Int>

    @Delete
    fun deleteArticle(article: Article): Maybe<Int>

    @Query("DELETE FROM news_table")
    fun deleteAllArticles(): Maybe<Long>

    @Query("SELECT * FROM news_table")
    fun getAllArticles()

    @Query("SELECT * FROM news_table WHERE searchTag =:q")
    fun getArticlesBySearchTag(q: String)

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
    fun deleteAllTopHeadlines(): Maybe<Long>

    @Query("SELECT * FROM top_headlines_table")
    fun getAllTopHeadlines()

    @Query("SELECT * FROM top_headlines_table WHERE country =:country")
    fun getArticlesByTopHeadline(country: String)


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
    fun deleteAllReadLater(): Maybe<Long>

    @Query("SELECT * FROM read_later_table")
    fun getAllReadLater()
}