package com.pineapple.app.network.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pineapple.app.network.caching.entity.SubredditEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubredditDao {

    @Query("SELECT * FROM subreddits ORDER BY subscribers DESC")
    fun getPopularSubreddits(): Flow<List<SubredditEntity>>

    @Query("SELECT * FROM subreddits WHERE isSubscribed = 1 ORDER BY name COLLATE NOCASE ASC")
    fun getSubscribedSubreddits(): Flow<List<SubredditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(subreddits: List<SubredditEntity>)

    @Query("UPDATE subreddits SET isSubscribed = 0")
    suspend fun markAllUnsubscribed()

}
