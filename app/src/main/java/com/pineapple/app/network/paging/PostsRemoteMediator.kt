package com.pineapple.app.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.caching.entity.PostEntity
import com.pineapple.app.network.caching.entity.RemoteKeyEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.PostWithUser

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator(
    private val redditApi: RedditApi,
    private val db: AppDatabase,
    private val subreddit: String,
    private val sort: String,
    private val time: String
) : RemoteMediator<Int, PostWithUser>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostWithUser>
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
                        db.remoteKeyDao().remoteKeysPostId(lastItem.post.id)?.nextKey
                    }
                }
            }

            val response = redditApi.fetchSubreddit(
                name = subreddit,
                sort = sort,
                time = time,
                after = pageKeyData,
                rawJson = 1,
                limit = state.config.pageSize
            )

            val posts = response.data.children
            val endOfPaginationReached = posts.isEmpty()
            val startIndex = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.APPEND -> {
                    db.postDao().maxSortKey() ?: 0
                }
                else -> 0
            }

            val entities = posts.mapIndexed { index, item ->
                val d = item.data
                val apiIndex = startIndex + index

                val source = d.preview?.images?.firstOrNull()?.source
                val previewUrl = source?.url?.replace("amp;", "")
                val previewWidth = source?.width
                val previewHeight = source?.height

                val normalizedId = d.name ?: "t3_${d.id}"

                PostEntity(
                    id = normalizedId,
                    title = d.title.orEmpty(),
                    author = d.author,
                    subreddit = d.subreddit,
                    createdUtc = d.createdUTC ?: 0L,
                    ups = d.ups?.toInt(),
                    thumbnail = d.thumbnail,
                    permalink = d.permalink.orEmpty(),
                    url = d.url,
                    previewImageUrl = previewUrl,
                    previewWidth = previewWidth,
                    previewHeight = previewHeight,
                    sortKey = apiIndex,
                    saved = d.saved,
                    likes = d.likes,
                    selftext = d.selftext
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

            val authorNames = entities.mapNotNull { it.author }.distinct()

            db.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clearRemoteKeys()
                    db.postDao().clearAll()
                    // optional: db.userDao().clearAll() if you want wipe
                }

                db.postDao().insertAll(entities)
                db.remoteKeyDao().insertAll(keys)

                val existingUsers = authorNames.mapNotNull { db.userDao().getUser(it) }
                    .associateBy { it.name }

                val missingAuthors = authorNames.filter { it !in existingUsers }

                val newUsers = missingAuthors.mapNotNull { author ->
                    try {
                        val about = redditApi.fetchUserInfo(author)
                        UserEntity(
                            name = about.data.name.toString(),
                            iconUrl = about.data.icon_img,
                            snoovatarUrl = about.data.snoovatar_img
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                if (newUsers.isNotEmpty()) {
                    db.userDao().insertAll(newUsers)
                }
            }


            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
