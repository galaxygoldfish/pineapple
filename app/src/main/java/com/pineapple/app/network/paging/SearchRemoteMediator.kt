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
import com.pineapple.app.network.caching.entity.SearchRemoteKeyEntity
import com.pineapple.app.network.caching.entity.SearchResultEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.PostWithUser

private const val SEARCH_SORT_OFFSET = 1_000_000

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val redditApi: RedditApi,
    private val db: AppDatabase,
    private val query: String,
    private val sort: String?,
    private val time: String?
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
                    if (lastItem == null) null
                    else db.searchRemoteKeyDao().remoteKeysPostId(query, lastItem.post.id)
                }
            }

            val response = redditApi.searchPosts(
                query = query,
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
                    // continue from max sortKey for search (relative)
                    db.searchResultDao().getPostIdsForQuery(query).size
                }
                else -> 0
            }

            val entities = posts.mapIndexed { index, item ->
                val d = item.data
                val apiIndex = startIndex + index

                val source = d.preview?.images?.firstOrNull()?.source
                val previewUrl = source?.url?.replace("amp;", "")

                // offset the sortKey so search-inserted posts don't collide with home feed ordering
                val postSortKey = apiIndex + SEARCH_SORT_OFFSET

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
                    previewWidth = source?.width,
                    previewHeight = source?.height,
                    sortKey = postSortKey,
                    saved = d.saved,
                    likes = d.likes,
                    selftext = d.selftext
                )
            }

            val after = response.data.after
            val keys = entities.map {
                SearchRemoteKeyEntity(query = query, postId = it.id, prevKey = null, nextKey = after)
            }

            val authorNames = entities.mapNotNull { it.author }.distinct()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // clear only search-specific tables to avoid wiping main feed cache
                    db.searchRemoteKeyDao().clearRemoteKeysForQuery(query)
                    db.searchResultDao().clearQuery(query)
                }
                db.postDao().insertAll(entities)

                // insert mapping rows for this query so we can page search results separately
                val mappings = entities.mapIndexed { index, post ->
                    SearchResultEntity(query = query, postId = post.id, sortKey = index + startIndex)
                }
                db.searchResultDao().insertAll(mappings)

                db.searchRemoteKeyDao().insertAll(keys)

                val existingUsers = authorNames.mapNotNull { db.userDao().getUser(it) }
                    .associateBy { it.name }

                val missingAuthors = authorNames.filter { it !in existingUsers }

                val newUsers = missingAuthors.mapNotNull { author ->
                    try {
                        val about = redditApi.fetchUserInfo(author)
                        UserEntity(name = about.data.name.toString(), iconUrl = about.data.icon_img, snoovatarUrl = about.data.snoovatar_img)
                    } catch (_: Exception) {
                        null
                    }
                }

                if (newUsers.isNotEmpty()) db.userDao().insertAll(newUsers)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
