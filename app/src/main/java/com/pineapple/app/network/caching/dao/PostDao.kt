package com.pineapple.app.network.caching.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pineapple.app.network.caching.entity.PostEntity
import com.pineapple.app.network.model.cache.PostWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Transaction
    @Query("SELECT * FROM posts ORDER BY sortKey ASC")
    fun pagingSourceWithUser(): PagingSource<Int, PostWithUser>

    // PagingSource for search results: select posts by postId from search_results for query
    @Transaction
    @Query("SELECT * FROM posts WHERE id IN (SELECT postId FROM search_results WHERE query = :q) ORDER BY (SELECT sortKey FROM search_results WHERE query = :q AND postId = posts.id) ASC")
    fun pagingSourceForSearchQuery(q: String): PagingSource<Int, PostWithUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun countAll(): Int

    @Query("SELECT MAX(sortKey) FROM posts")
    suspend fun maxSortKey(): Int?

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPost(id: String): PostEntity?

    @Transaction
    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    fun getPostWithUserFlow(id: String): Flow<PostWithUser?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(posts: List<PostEntity>)


}
