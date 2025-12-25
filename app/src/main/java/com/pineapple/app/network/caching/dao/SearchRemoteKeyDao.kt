package com.pineapple.app.network.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pineapple.app.network.caching.entity.SearchRemoteKeyEntity

@Dao
interface SearchRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<SearchRemoteKeyEntity>)

    @Query("SELECT nextKey FROM search_remote_keys WHERE query = :q AND postId = :postId LIMIT 1")
    suspend fun remoteKeysPostId(q: String, postId: String): String?

    @Query("DELETE FROM search_remote_keys WHERE query = :q")
    suspend fun clearRemoteKeysForQuery(q: String)

}
