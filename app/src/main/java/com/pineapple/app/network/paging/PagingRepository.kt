package com.pineapple.app.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.model.cache.PostWithUser
import com.pineapple.app.network.model.cache.CommentWithUser
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class PagingRepository @Inject constructor(
    private val db: AppDatabase,
    private val redditApi: RedditApi
) {

    fun postsPager(
        subreddit: String,
        sort: String,
        time: String
    ): Pager<Int, PostWithUser> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            remoteMediator = PostsRemoteMediator(
                redditApi = redditApi,
                db = db,
                subreddit = subreddit,
                sort = sort,
                time = time
            ),
            pagingSourceFactory = { db.postDao().pagingSourceWithUser() }
        )
    }

    fun searchPostsPager(query: String, sort: String? = null, time: String? = null): Pager<Int, PostWithUser> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            remoteMediator = SearchRemoteMediator(
                redditApi = redditApi,
                db = db,
                query = query,
                sort = sort,
                time = time
            ),
            pagingSourceFactory = { db.postDao().pagingSourceForSearchQuery(query) }
        )
    }

    fun commentsPager(postId: String): Pager<Int, CommentWithUser> {
        return Pager<Int, CommentWithUser>(
             config = PagingConfig(
                 pageSize = 25,
                 enablePlaceholders = false
             ),
             remoteMediator = CommentsRemoteMediator(
                 redditApi = redditApi,
                 db = db,
                 postId = postId
             ),
             pagingSourceFactory = { db.commentDao().pagingSourceForPost(postId) }
         )
     }
}
