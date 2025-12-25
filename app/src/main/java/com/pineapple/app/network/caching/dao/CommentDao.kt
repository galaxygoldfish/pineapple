package com.pineapple.app.network.caching.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.network.model.cache.CommentWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(comments: List<CommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(comment: CommentEntity)

    @Transaction
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY sortKey ASC")
    fun pagingSourceForPost(postId: String): PagingSource<Int, CommentWithUser>

    @Query("SELECT MAX(sortKey) FROM comments WHERE postId = :postId")
    suspend fun maxSortKeyForPost(postId: String): Int?

    @Transaction
    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getComment(commentId: String): CommentWithUser?

    // Replies handling: fetch replies whose parentId matches a given comment id
    @Transaction
    @Query("SELECT * FROM comments WHERE parentId = :parentId ORDER BY sortKey ASC")
    fun getRepliesForCommentFlow(parentId: String): Flow<List<CommentWithUser>>

    @Query("SELECT COUNT(*) FROM comments WHERE parentId = :parentId")
    suspend fun countRepliesForComment(parentId: String): Int

}
