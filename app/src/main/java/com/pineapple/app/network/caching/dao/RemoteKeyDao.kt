package com.pineapple.app.network.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pineapple.app.network.caching.entity.RemoteKeyEntity


@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM remote_keys WHERE postId = :id")
    suspend fun remoteKeysPostId(id: String): RemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeyEntity>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}