package com.pineapple.app.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.network.caching.entity.RemoteKeyEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.CommentWithUser

@OptIn(ExperimentalPagingApi::class)
class UserCommentsRemoteMediator(
    private val redditApi: RedditApi,
    private val db: AppDatabase,
    private val username: String
) : RemoteMediator<Int, CommentWithUser>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CommentWithUser>
    ): MediatorResult {
        return try {
            val pageKeyData = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        null
                    } else {
                        db.remoteKeyDao().remoteKeysPostId(lastItem.comment.id)?.nextKey
                    }
                }
            }

            val response = redditApi.fetchUserComments(
                user = username,
                after = pageKeyData,
                limit = state.config.pageSize
            )

            val comments = response.data.children
            val endOfPaginationReached = comments.isEmpty()
            
            val startIndex = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.APPEND -> {
                    (db.commentDao().maxSortKeyForPost("USER_$username") ?: 0) + 1
                }
                else -> 0
            }

            val entities = comments.mapIndexed { index, item ->
                val d = item.data
                val apiIndex = startIndex + index

                CommentEntity(
                    id = "t1_${d.id}",
                    postId = d.link_id ?: "t3_${d.permalink.split("/")[4]}", // Extracting post id from permalink if link_id is not present
                    parentId = null,
                    author = d.author,
                    body = d.body,
                    bodyHtml = d.body_html,
                    ups = d.ups?.toInt(),
                    sortKey = apiIndex,
                    createdUtc = (d.created_utc ?: 0.0).toLong(),
                    saved = d.saved,
                    likes = d.likes,
                    permalink = d.permalink,
                    linkTitle = d.link_title
                )
            }

            val after = response.data.after
            val keys = entities.map {
                RemoteKeyEntity(
                    postId = it.id,
                    prevKey = null,
                    nextKey = after
                )
            }

            db.withTransaction {
                db.commentDao().upsertAll(entities)
                db.remoteKeyDao().insertAll(keys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
