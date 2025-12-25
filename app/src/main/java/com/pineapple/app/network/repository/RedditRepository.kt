package com.pineapple.app.network.repository

import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.network.caching.entity.PostEntity
import com.pineapple.app.network.caching.entity.SubredditEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.PostWithUser
import com.pineapple.app.network.model.reddit.SubredditData
import com.pineapple.app.network.model.reddit.UserAbout
import com.pineapple.app.network.model.reddit.CommentPreData
import com.pineapple.app.utilities.toSubredditEntity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pineapple.app.network.model.reddit.Listing
import com.google.gson.JsonObject


@Singleton
class RedditRepository @Inject constructor(
    private val redditApi: RedditApi,
    private val mmkv: MMKV,
    db: AppDatabase
) {

    private val subredditDao = db.subredditDao()
    private val postDao = db.postDao()
    private val userDao = db.userDao()
    private val commentDao = db.commentDao()
    private val gson = Gson()

    fun observePostWithUser(postId: String): Flow<PostWithUser?> =
        postDao.getPostWithUserFlow("t3_$postId")

    /**
     * Refresh replies for a given comment by fetching the post's comments and extracting replies
     * that belong to the supplied commentId. Inserts reply CommentEntity rows with parentId set.
     * Returns the number of replies parsed/inserted, or -1 on error.
     */
    suspend fun refreshRepliesForComment(postId: String, commentId: String): Int = withContext(Dispatchers.IO) {
        try {
            val response = redditApi.fetchCommentsByPostId(postId)

            // defensively extract children list from the second element of the response
            val commentChildren = run {
                val second = response.getOrNull(1)
                when (second) {
                    is com.pineapple.app.network.model.reddit.Listing<*> -> (second.data.children as? List<*>) ?: emptyList()
                    is Map<*, *> -> ((second["data"] as? Map<*, *>)?.get("children") as? List<*>) ?: emptyList()
                    else -> emptyList<Any>()
                }
            }

            // compute a starting sortKey once to avoid suspending calls during traversal
            var sortCounter = commentDao.maxSortKeyForPost(postId)?.plus(1) ?: 0

            // traverse the commentChildren and collect all nested replies that are direct children of the desired comment
            val out = mutableListOf<CommentEntity>()

            fun traverseAndCollect(item: Any?, parentFullname: String?) {
                if (item == null) return
                when (item) {
                    is com.pineapple.app.network.model.reddit.ListingItem<*> -> {
                        val inner = item.children
                        inner.forEach { cpAny -> traverseAndCollect(cpAny, parentFullname) }
                    }
                    is CommentPreData -> {
                        val d = item.data
                        val thisFull = "t1_${d.id}"
                        // Process nested replies if present
                        var replyChildren: List<CommentPreData> = emptyList()
                        d.replies?.let { je ->
                            if (je.isJsonObject) {
                                try {
                                    val type = object : TypeToken<Listing<CommentPreData>>() {}.type
                                    val listing = gson.fromJson<Listing<CommentPreData>>(je, type)
                                    replyChildren = listing.data.children
                                    replyChildren.forEach {
                                        traverseAndCollect(it, thisFull)
                                    }
                                } catch (e: Exception) {
                                    // ignore
                                }
                            }
                        }
                    }
                    is Map<*, *> -> {
                        val data = item["data"] as? Map<*, *>
                        if (data == null) return
                        val id = data["id"] as? String ?: return
                        val thisFull = "t1_$id"
                        val parentIdRaw = data["parent_id"] as? String
                        val parentFull = parentIdRaw
                        
                        // Parse replies to calculate count
                        var replyCount = 0
                        val repliesAny = data["replies"]
                         if (repliesAny is Map<*, *>) {
                             val rdata = (repliesAny["data"] as? Map<*, *>)?.get("children") as? List<*>
                             replyCount = rdata?.size ?: 0
                         }
                        
                        // if the parent matches the target comment's fullname (t1_$commentId), collect this as a reply
                        if (parentFull == "t1_$commentId") {
                            val author = data["author"] as? String
                            val body = data["body"] as? String
                            val bodyHtml = data["body_html"] as? String
                            val ups = try { ((data["ups"] as? Number)?.toLong() ?: 0L).toInt() } catch (_: Throwable) { 0 }
                            val created = (data["created_utc"] as? Number)?.toLong()
                            val saved = data["saved"] as? Boolean
                            val likes = data["likes"] as? Boolean
                            val permalink = data["permalink"] as? String
                            val sortKey = sortCounter++
                            out.add(
                                CommentEntity(
                                    id = thisFull,
                                    postId = postId,
                                    parentId = "t1_$commentId",
                                    author = author,
                                    body = body,
                                    bodyHtml = bodyHtml,
                                    ups = ups,
                                    sortKey = sortKey,
                                    depth = 1,
                                    replyCount = replyCount,
                                    createdUtc = created,
                                    saved = saved,
                                    likes = likes,
                                    permalink = permalink
                                )
                            )
                        }

                        // traverse nested replies if present
                        if (repliesAny is Map<*, *>) {
                            val rdata = (repliesAny["data"] as? Map<*, *>)?.get("children") as? List<*>
                            rdata?.forEach { traverseAndCollect(it, thisFull) }
                        }
                    }
                    is JsonElement -> {
                        if (item.isJsonObject) {
                            val obj = item.asJsonObject
                            val dataObj = obj.getAsJsonObject("data")
                            val id = dataObj.get("id")?.asString ?: return
                            val parentRaw = dataObj.get("parent_id")?.asString
                            
                            var replyCount = 0
                            val repliesElement = dataObj.get("replies")
                            if (repliesElement != null && repliesElement.isJsonObject) {
                                 val repliesObj = repliesElement.asJsonObject
                                 val repliesData = repliesObj.getAsJsonObject("data")
                                 val repliesChildren = repliesData?.getAsJsonArray("children")
                                 replyCount = repliesChildren?.size() ?: 0
                            }
                            
                            if (parentRaw == "t1_$commentId") {
                                val author = dataObj.get("author")?.asString
                                val body = dataObj.get("body")?.asString
                                val bodyHtml = dataObj.get("body_html")?.asString
                                val ups = try { dataObj.get("ups")?.asInt ?: 0 } catch (_: Throwable) { 0 }
                                val created = dataObj.get("created_utc")?.asLong
                                val saved = dataObj.get("saved")?.asBoolean
                                val likes = dataObj.get("likes")?.asBoolean
                                val permalink = dataObj.get("permalink")?.asString
                                val sortKey = sortCounter++
                                out.add(
                                    CommentEntity(
                                        id = "t1_$id",
                                        postId = postId,
                                        parentId = "t1_$commentId",
                                        author = author,
                                        body = body,
                                        bodyHtml = bodyHtml,
                                        ups = ups,
                                        sortKey = sortKey,
                                        depth = 1,
                                        replyCount = replyCount,
                                        createdUtc = created,
                                        saved = saved,
                                        likes = likes,
                                        permalink = permalink
                                    )
                                )
                            }

                            // traverse nested children listing if present
                            val data = obj.getAsJsonObject("data")
                            val children = data?.getAsJsonArray("children")
                            children?.forEach { traverseAndCollect(it, "t1_$id") }
                        }
                    }
                    else -> {
                        // unknown shape - ignore
                    }
                }
            }

            commentChildren.forEach { traverseAndCollect(it, null) }

            if (out.isNotEmpty()) {
                commentDao.upsertAll(out)
            }

            // return number of replies parsed/inserted
            return@withContext out.size

        } catch (_: Exception) {
            // swallow
        }

        return@withContext -1
    }

    suspend fun refreshPostAndAuthor(postId: String) {

        // Try to find cached post first
        val cached = postDao.getPost("t3_$postId")

        // Decide how to call the API to get fresh data
        val raw = try {
            if (cached == null) {
                // If we don't have subreddit/slug info, fetch by id endpoint
                redditApi.fetchPostById(postId)
            } else {
                val subreddit = cached.subreddit ?: ""
                val splitPerma = cached.permalink.split("/")
                val slug = splitPerma.getOrNull(splitPerma.size - 2) ?: ""
                redditApi.fetchPost(
                    subreddit = subreddit,
                    postID = postId,
                    post = slug
                )
            }
        } catch (_: Exception) {
            // If API fails and we have cached data, don't crash — just return
            if (cached == null) return else null
        }

        if (raw == null) return

        // Parse response into your PostEntity and update Room
        val postListing = raw.firstOrNull() ?: return
        val freshPostData = postListing.data.children.firstOrNull()?.children?.firstOrNull() ?: return

        // Determine a sortKey: preserve cached if present, otherwise append to end
        val sortKey = cached?.sortKey ?: ((postDao.maxSortKey() ?: 0) + 1)

        val freshEntity = PostEntity(
            id = freshPostData.name ?: "t3_$postId",
            title = freshPostData.title.orEmpty(),
            author = freshPostData.author,
            subreddit = freshPostData.subreddit,
            createdUtc = (freshPostData.createdUTC ?: 0.0).toLong(),
            ups = freshPostData.ups?.toInt() ?: cached?.ups?.toInt(),
            thumbnail = freshPostData.thumbnail ?: cached?.thumbnail,
            permalink = freshPostData.permalink ?: cached?.permalink ?: "",
            url = freshPostData.url ?: cached?.url,
            previewImageUrl = freshPostData.preview?.images?.firstOrNull()?.source?.url
                ?.replace("amp;", "") ?: cached?.previewImageUrl,
            previewWidth = freshPostData.preview?.images?.firstOrNull()?.source?.width
                ?: cached?.previewWidth,
            previewHeight = freshPostData.preview?.images?.firstOrNull()?.source?.height
                ?: cached?.previewHeight,
            sortKey = sortKey,
            saved = freshPostData.saved ?: cached?.saved,
            likes = freshPostData.likes ?: cached?.likes,
            selftext = freshPostData.selftext ?: cached?.selftext
        )

        postDao.upsert(freshEntity)

        // 2) Refresh author info
        val authorName = freshEntity.author ?: return
        val userAbout = try {
            redditApi.fetchUserInfo(user = authorName)
        } catch (_: Exception) {
            null
        }
        // Map to UserEntity and upsert into userDao
        if (userAbout != null) {
            val about = userAbout.data
            val userEntity = UserEntity(
                name = about.name ?: authorName,
                iconUrl = about.icon_img ?: "",
                snoovatarUrl = about.snoovatar_img ?: ""
            )
            userDao.insertAll(listOf(userEntity))
        }
    }

    // New: Update bookmark state via API and persist to cache
    suspend fun savePost(postIdNoPrefix: String) {
        val fullId = "t3_$postIdNoPrefix"
        try {
            redditApi.savePost(fullId)
        } catch (_: Exception) {
            // ignore API errors for now
        }

        val cached = postDao.getPost(fullId)
        if (cached != null) {
            val updated = cached.copy(saved = true)
            postDao.upsert(updated)
        }
    }

    suspend fun unsavePost(postIdNoPrefix: String) {
        val fullId = "t3_$postIdNoPrefix"
        try {
            redditApi.unsavePost(fullId)
        } catch (_: Exception) {
            // ignore API errors for now
        }

        val cached = postDao.getPost(fullId)
        if (cached != null) {
            val updated = cached.copy(saved = false)
            postDao.upsert(updated)
        }
    }

   suspend fun castVoteAndCache(postIdNoPrefix: String, direction: Int, prefix: String = "t3_") {
        val fullId = "$prefix$postIdNoPrefix"
        try {
            redditApi.castVote(fullId, direction)
        } catch (_: Exception) {
            // ignore API errors
        }

        // determine target dao/entity based on fullname prefix
        when {
            fullId.startsWith("t3_") -> {
                val cached = postDao.getPost(fullId) ?: return
                val prevLikes = cached.likes
                val prevValue = when (prevLikes) {
                    true -> 1
                    false -> -1
                    null -> 0
                }
                val newValue = when (direction) {
                    1 -> 1
                    -1 -> -1
                    else -> 0
                }
                val delta = newValue - prevValue
                val newUps = (cached.ups ?: 0) + delta
                val newLikes = when (direction) {
                    1 -> true
                    -1 -> false
                    else -> null
                }
                val updated = cached.copy(likes = newLikes, ups = newUps)
                postDao.upsert(updated)
            }

            fullId.startsWith("t1_") -> {
                val cached = commentDao.getComment(fullId) ?: return
                val prevLikes = cached.comment.likes
                val prevValue = when (prevLikes) {
                    true -> 1
                    false -> -1
                    null -> 0
                }
                val newValue = when (direction) {
                    1 -> 1
                    -1 -> -1
                    else -> 0
                }
                val delta = newValue - prevValue
                val newUps = (cached.comment.ups ?: 0) + delta
                val newLikes = when (direction) {
                    1 -> true
                    -1 -> false
                    else -> null
                }
                val updated = cached.comment.copy(likes = newLikes, ups = newUps)
                commentDao.upsert(updated)
            }
        }
    }

    fun observePopularSubreddits(): Flow<List<SubredditEntity>> =
        subredditDao.getPopularSubreddits()

    fun observeSubscribedSubreddits(): Flow<List<SubredditEntity>> =
        subredditDao.getSubscribedSubreddits()

    suspend fun refreshPopularSubreddits(force: Boolean = false) {
        if (!force && !shouldRefreshPopularSubreddits()) return

        val listing = redditApi.fetchTopSubreddits(limit = 50)
        val entities = listing.data.children.map { it.toSubredditEntity(isSubscribed = false) }
        subredditDao.upsertAll(entities)
        mmkv.putLong("popular_subreddits_last_fetch", System.currentTimeMillis())
    }

    suspend fun refreshSubscribedSubreddits(force: Boolean = false) {

        if (!force && !shouldRefreshSubscribedSubreddits()) return

        val listing = redditApi.fetchSubscribedSubreddits(limit = 100)
        val entities = listing.data.children.map { it.toSubredditEntity(isSubscribed = true) }
        subredditDao.markAllUnsubscribed()
        subredditDao.upsertAll(entities)
        mmkv.putLong("subscribed_subreddits_last_fetch", System.currentTimeMillis())
    }

    private fun shouldRefreshPopularSubreddits(): Boolean {
        val last = mmkv.getLong("popular_subreddits_last_fetch", 0L)
        return System.currentTimeMillis() - last > 3 * 60 * 60 * 1000 // 3h
    }

    private fun shouldRefreshSubscribedSubreddits(): Boolean {
        val last = mmkv.getLong("subscribed_subreddits_last_fetch", 0L)
        return System.currentTimeMillis() - last > 30 * 60 * 1000 // 30min, up to you
    }

    // Suggest top communities: return the SubredditData model directly
    suspend fun suggestCommunities(query: String, limit: Int = 3): List<SubredditData> {
        return try {
            val resp = redditApi.searchCommunities(query = query, limit = limit)
            resp.data.children.take(limit).map { it.data }
        } catch (_: Exception) {
            emptyList()
        }
    }

    // Suggest top users: return the UserAbout model directly
    suspend fun suggestUsers(query: String, limit: Int = 3): List<UserAbout> {
        return try {
            val resp = redditApi.searchUsers(query = query, limit = limit)
            resp.data.children.take(limit).map { it.data }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun refreshCommentsForPost(postId: String) {
        try {
            val response = redditApi.fetchCommentsByPostId(postId)

            // defensively extract children list from the second element of the response
            val commentChildren = run {
                val second = response.getOrNull(1)
                when (second) {
                    is com.pineapple.app.network.model.reddit.Listing<*> -> (second.data.children as? List<*>) ?: emptyList()
                    is Map<*, *> -> ((second["data"] as? Map<*, *>)?.get("children") as? List<*>) ?: emptyList()
                    else -> emptyList<Any>()
                }
            }

            val startIndex = commentDao.maxSortKeyForPost(postId)?.plus(1) ?: 0

            val out = mutableListOf<CommentEntity>()
            var counter = startIndex

            // parse a variety of possible shapes for individual comment items
            commentChildren.forEach { itemAny ->
                when (itemAny) {
                    is com.pineapple.app.network.model.reddit.ListingItem<*> -> {
                        val inner = itemAny.children
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
                            
                            // Parse replies to calculate count
                            var replyCount = 0
                            d.replies?.let { je ->
                                if (je.isJsonObject) {
                                    try {
                                        val type = object : TypeToken<Listing<CommentPreData>>() {}.type
                                        val listing = gson.fromJson<Listing<CommentPreData>>(je, type)
                                        val replyChildren = listing.data.children
                                        replyCount = replyChildren.count { it.kind == "t1" }
                                    } catch (e: Exception) {
                                        // ignore
                                    }
                                }
                            }
                            
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
                                    replyCount = replyCount,
                                    createdUtc = created,
                                    saved = saved,
                                    likes = likes,
                                    permalink = permalink
                                )
                            )
                        }
                    }
                    is CommentPreData -> {
                        val cp = itemAny
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
                        
                        // Parse replies to calculate count
                        var replyCount = 0
                        d.replies?.let { je ->
                            if (je.isJsonObject) {
                                try {
                                    val type = object : TypeToken<Listing<CommentPreData>>() {}.type
                                    val listing = gson.fromJson<Listing<CommentPreData>>(je, type)
                                    val replyChildren = listing.data.children
                                    replyCount = replyChildren.count { it.kind == "t1" }
                                } catch (e: Exception) {
                                    // ignore
                                }
                            }
                        }

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
                                replyCount = replyCount,
                                createdUtc = created,
                                saved = saved,
                                likes = likes,
                                permalink = permalink
                            )
                        )
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
                        
                        // Parse replies to calculate count
                        var replyCount = 0
                        val repliesAny = data["replies"]
                         if (repliesAny is Map<*, *>) {
                             val rdata = (repliesAny["data"] as? Map<*, *>)?.get("children") as? List<*>
                             replyCount = rdata?.size ?: 0
                         }

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
                                replyCount = replyCount,
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

            // persist
            if (out.isNotEmpty()) {
                commentDao.upsertAll(out)
            }

            // Insert placeholder users to avoid fetching each user synchronously (improves performance)
            val authorNames = out.mapNotNull { it.author }.distinct()
            val existingUsers = authorNames.mapNotNull { userDao.getUser(it) }.associateBy { it.name }
            val missing = authorNames.filter { it !in existingUsers }

            if (missing.isNotEmpty()) {
                val placeholders = missing.map { name ->
                    UserEntity(name = name, iconUrl = "", snoovatarUrl = "")
                }
                userDao.insertAll(placeholders)

                // Removed background prefetch of up to 20 profiles.
                // Adopting on-demand fetch: UI components should request full user info (avatars) when rows are visible.
            }

        } catch (_: Exception) {
            // swallow - minimal behavior
        }
    }

    /**
     * Fetch a user's about info from the API and cache it locally.
     * This is intended for on-demand fetching (e.g. when a comment row becomes visible).
     */
    suspend fun fetchAndCacheUser(username: String) {
        if (username.isBlank()) return
        // if user already exists and has an icon, skip
        val existing = userDao.getUser(username)
        if (existing != null && !existing.iconUrl.isNullOrEmpty()) return

        try {
            val about = redditApi.fetchUserInfo(username)
            val u = about.data
            val userEntity = UserEntity(
                name = u.name ?: username,
                iconUrl = u.icon_img ?: "",
                snoovatarUrl = u.snoovatar_img ?: ""
            )
            userDao.insertAll(listOf(userEntity))
        } catch (_: Exception) {
            // minimal: ignore failures; UI can retry or show placeholder
        }
    }

    fun observeRepliesForComment(parentCommentFullId: String) =
        commentDao.getRepliesForCommentFlow(parentCommentFullId)
}
