package com.pineapple.app.network.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pineapple.app.network.caching.entity.SearchResultEntity

@Dao
interface SearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<SearchResultEntity>)

    @Query("DELETE FROM search_results WHERE query = :q")
    suspend fun clearQuery(q: String)

    @Query("SELECT postId FROM search_results WHERE query = :q ORDER BY sortKey ASC")
    suspend fun getPostIdsForQuery(q: String): List<String>

}
