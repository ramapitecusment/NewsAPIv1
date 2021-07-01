package com.ramapitecusment.newsapi.services.database

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articles: List<Article>): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(article: Article): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateArticles(articles: List<Article>): Completable

    @Delete
    fun delete(article: Article): Completable

    @Query("DELETE FROM articles")
    fun delete(): Completable

    @Query("DELETE FROM articles WHERE searchTag !=:searchTag")
    fun deleteAllBySearchTag(searchTag: String): Completable

    @Query("DELETE FROM articles WHERE country !=:country")
    fun deleteAllByCountry(country: String): Completable

    @Query("DELETE FROM articles WHERE isReadLater =:isReadLater")
    fun deleteAllByReadLater(isReadLater: Int = 1): Completable

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flowable<List<Article>>

    @Query("SELECT * FROM articles WHERE searchTag =:searchTag")
    fun getArticlesBySearchTag(searchTag: String): Flowable<List<Article>>

    @Query("SELECT * FROM articles WHERE country =:country")
    fun getArticlesByCountry(country: String): Flowable<List<Article>>

    @Query("SELECT * FROM articles WHERE isReadLater =:isReadLater")
    fun getArticlesByReadLater(isReadLater: Int = 1): Flowable<List<Article>>

}