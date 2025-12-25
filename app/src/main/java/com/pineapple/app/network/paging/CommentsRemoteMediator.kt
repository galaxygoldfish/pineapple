package com.pineapple.app.network.paging

// minimal mediator for comments
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.network.model.reddit.CommentPreData
import com.pineapple.app.network.model.reddit.Listing

@OptIn(ExperimentalPagingApi::class)
class CommentsRemoteMediator(
    private val redditApi: RedditApi,
    private val db: AppDatabase,
    private val postId: String
) : RemoteMediator<Int, CommentWithUser>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, CommentWithUser>): MediatorResult {
        return try {
            // For comments we generally only support REFRESH loads (comments are hierarchical)
            if (loadType == LoadType.PREPEND) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = redditApi.fetchCommentsByPostId(postId)

            // defensively obtain children list from second element
            val commentChildren = run {
                val second = response.getOrNull(1)
                when (second) {
                    is Listing<*> -> (second.data.children as? List<*>) ?: emptyList()
                    is Map<*, *> -> ((second["data"] as? Map<*, *>)?.get("children") as? List<*>) ?: emptyList()
                    else -> emptyList<Any>()
                }
            }

            val startIndex = db.commentDao().maxSortKeyForPost(postId)?.plus(1) ?: 0

            val out = mutableListOf<CommentEntity>()
            var counter = startIndex

            // parse either direct CommentPreData or Map or Listing children
            commentChildren.forEach { itemAny ->
                when (itemAny) {
                    is CommentPreData -> {
                        if (itemAny.kind != "t1") return@forEach
                        val d = itemAny.data
                        val id = d.id.ifEmpty { return@forEach }
                        val author = d.author
                        val body = d.body
                        val bodyHtml = d.body_html
                        val ups = try { d.ups?.toInt() ?: 0 } catch (_: Throwable) { 0 }
                        val created = d.created_utc?.toLong()
                        val saved = d.saved
                        val likes = d.likes
                        val permalink = d.permalink
                        val sortKey = counter++
                        out.add(
                            CommentEntity(
                                id = "t1_$id",
                                postId = postId,
                                parentId = null,
                                author = author,
                                body = body,
                                bodyHtml = bodyHtml,
                                ups = ups,
                                sortKey = sortKey,
                                depth = 0,
                                createdUtc = created,
                                saved = saved,
                                likes = likes,
                                permalink = permalink
                            )
                        )
                    }
                    is Listing<*> -> {
                        val inner = itemAny.data.children
                        inner.forEach { cpAny ->
                            val cp = cpAny as? CommentPreData ?: return@forEach
                            if (cp.kind != "t1") return@forEach
                            val d = cp.data
                            val id = d.id.ifEmpty { return@forEach }
                            val author = d.author
                            val body = d.body
                            val bodyHtml = d.body_html
                            val ups = try { d.ups?.toInt() ?: 0 } catch (_: Throwable) { 0 }
                            val created = d.created_utc?.toLong()
                            val saved = d.saved
                            val likes = d.likes
                            val permalink = d.permalink
                            val sortKey = counter++
                            out.add(
                                CommentEntity(
                                    id = "t1_$id",
                                    postId = postId,
                                    parentId = null,
                                    author = author,
                                    body = body,
                                    bodyHtml = bodyHtml,
                                    ups = ups,
                                    sortKey = sortKey,
                                    depth = 0,
                                    createdUtc = created,
                                    saved = saved,
                                    likes = likes,
                                    permalink = permalink
                                )
                            )
                        }
                    }
                    is Map<*, *> -> {
                        val kind = itemAny["kind"] as? String
                        val data = itemAny["data"] as? Map<*, *>
                        if (kind != "t1" || data == null) return@forEach
                        val id = data["id"] as? String ?: return@forEach
                        val author = data["author"] as? String
                        val body = data["body"] as? String
                        val bodyHtml = data["body_html"] as? String ?: ""
                        val ups = try { ((data["ups"] as? Number)?.toLong() ?: 0L).toInt() } catch (_: Throwable) { 0 }
                        val created = (data["created_utc"] as? Number)?.toLong()
                        val saved = data["saved"] as? Boolean
                        val likes = data["likes"] as? Boolean
                        val permalink = data["permalink"] as? String
                        val sortKey = counter++
                        out.add(
                            CommentEntity(
                                id = "t1_$id",
                                postId = postId,
                                parentId = null,
                                author = author,
                                body = body,
                                bodyHtml = bodyHtml,
                                ups = ups,
                                sortKey = sortKey,
                                depth = 0,
                                createdUtc = created,
                                saved = saved,
                                likes = likes,
                                permalink = permalink
                            )
                        )
                    }
                    else -> {
                        // unknown shape - skip
                    }
                }
            }

            db.withTransaction {
                if (out.isNotEmpty()) db.commentDao().upsertAll(out)

                // Insert placeholder users for authors we don't have locally to avoid blocking UI
                val authorNames = out.mapNotNull { it.author }.distinct()
                val existingUsers = authorNames.mapNotNull { db.userDao().getUser(it) }.associateBy { it.name }
                val missing = authorNames.filter { it !in existingUsers }
                if (missing.isNotEmpty()) {
                    val placeholders = missing.map { name -> UserEntity(name = name, iconUrl = "", snoovatarUrl = "") }
                    db.userDao().insertAll(placeholders)

                    // NOTE: On-demand fetching model: do NOT prefetch user profiles here.
                    // Avatars and full user info should be fetched by the UI when the user row becomes visible.
                }
            }

            MediatorResult.Success(endOfPaginationReached = true)
        } catch (_: Exception) {
            MediatorResult.Error(Exception("comments load failed"))
        }
    }
}
