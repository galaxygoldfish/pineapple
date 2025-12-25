package com.pineapple.app.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.network.model.reddit.CommentPreData
import com.pineapple.app.network.model.reddit.Listing
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalPagingApi::class)
class CommentsRemoteMediator(
    private val redditApi: RedditApi,
    private val db: AppDatabase,
    private val postId: String
) : RemoteMediator<Int, CommentWithUser>() {

    private val gson = Gson()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, CommentWithUser>): MediatorResult {
        return try {
            // For comments we generally only support REFRESH loads (comments are hierarchical)
            if (loadType == LoadType.PREPEND) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = redditApi.fetchCommentsByPostId(postId)

            // The first listing is the post itself (t3), the second is the comments (t1)
            val commentsListing = response.getOrNull(1) 
                ?: return MediatorResult.Success(endOfPaginationReached = true)
            
            // We expect the second item to be a Listing<CommentPreData>
            val children = commentsListing.data.children

            val startIndex = db.commentDao().maxSortKeyForPost(postId)?.plus(1) ?: 0
            val sortKeyCounter = AtomicInteger(startIndex)

            val out = mutableListOf<CommentEntity>()
            
            // IMPORTANT: Start depth at 0. Root comments are Depth 0.
            processComments(children, postId, null, 0, out, sortKeyCounter)

            db.withTransaction {
                if (out.isNotEmpty()) db.commentDao().upsertAll(out)

                // Insert placeholder users for authors we don't have locally to avoid blocking UI
                val authorNames = out.mapNotNull { it.author }.distinct()
                val existingUsers = authorNames.mapNotNull { db.userDao().getUser(it) }.associateBy { it.name }
                val missing = authorNames.filter { it !in existingUsers }
                if (missing.isNotEmpty()) {
                    val placeholders = missing.map { name -> UserEntity(name = name, iconUrl = "", snoovatarUrl = "") }
                    db.userDao().insertAll(placeholders)
                }
            }

            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun processComments(
        children: List<CommentPreData>,
        postId: String,
        parentId: String?,
        depth: Int,
        out: MutableList<CommentEntity>,
        sortKeyCounter: AtomicInteger
    ) {
        for (child in children) {
            if (child.kind == "t1") {
                val d = child.data
                val id = d.id
                if (id.isEmpty()) continue
                
                val entityId = "t1_$id"
                val author = d.author
                val body = d.body
                val bodyHtml = d.body_html
                val ups = try { d.ups?.toInt() ?: 0 } catch (_: Throwable) { 0 }
                val created = d.created_utc?.toLong()
                val saved = d.saved
                val likes = d.likes
                val permalink = d.permalink
                val sortKey = sortKeyCounter.getAndIncrement()

                // Parse replies to calculate count and recurse
                var replyChildren: List<CommentPreData> = emptyList()
                d.replies?.let { repliesElement ->
                    if (repliesElement is JsonObject) {
                        try {
                            val type = object : TypeToken<Listing<CommentPreData>>() {}.type
                            val listing = gson.fromJson<Listing<CommentPreData>>(repliesElement, type)
                            replyChildren = listing.data.children
                        } catch (e: Exception) {
                            // Ignore parsing errors for replies
                        }
                    }
                }
                
                val replyCount = replyChildren.count { it.kind == "t1" }

                out.add(
                    CommentEntity(
                        id = entityId,
                        postId = postId,
                        parentId = parentId,
                        author = author,
                        body = body,
                        bodyHtml = bodyHtml,
                        ups = ups,
                        sortKey = sortKey,
                        depth = depth,
                        replyCount = replyCount,
                        createdUtc = created,
                        saved = saved,
                        likes = likes,
                        permalink = permalink
                    )
                )

                if (replyChildren.isNotEmpty()) {
                    // Recurse: Ensure we increment depth
                    processComments(replyChildren, postId, entityId, depth + 1, out, sortKeyCounter)
                }
            }
        }
    }
}
