package com.ramapitecusment.newsapi.services.database

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articles: List<Article>): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(article: Article): Completable

    @Delete
    fun delete(article: Article): Completable

    @Query("DELETE FROM articles")
    fun delete(): Completable

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flowable<List<Article>>

    @Query("SELECT * FROM articles WHERE searchTag =:searchTag")
    fun getArticlesBySearchTag(searchTag: String): Flowable<List<Article>>

    @Query("SELECT * FROM articles WHERE country =:country")
    fun getArticlesByCountry(country: String): Flowable<List<Article>>

    @Query("SELECT * FROM articles WHERE isReadLater =:isReadLater")
    fun getArticlesByReadLater(isReadLater: Int = 1): Flowable<List<Article>>

}