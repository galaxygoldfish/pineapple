package com.pineapple.app.network.api

import com.pineapple.app.network.model.reddit.CommentPreData
import com.pineapple.app.network.model.reddit.CondensedUserAboutListing
import com.pineapple.app.network.model.reddit.Listing
import com.pineapple.app.network.model.reddit.ListingBase
import com.pineapple.app.network.model.reddit.ListingItem
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.network.model.reddit.PostListing
import com.pineapple.app.network.model.reddit.SubredditItem
import com.pineapple.app.network.model.reddit.UserAboutListing
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Qualifier

/**
 * Interface with the Reddit API endpoints giving access to app content
 */
interface RedditApi {

    /**
     * Fetch posts from a specific subreddit with sorting and time filters
     * @param name The name of the subreddit (without the r/ prefix)
     * @param sort The sorting method (use [com.pineapple.app.consts.PostFilterSort]
     * @param time The time filter (use [com.pineapple.app.consts.PostFilterTime])
     * @param after The ID of the last post from the previous fetch for pagination
     * @param rawJson Whether to get raw JSON (1) or not (0)
     * @param limit The number of posts to fetch (default is 5)
     * @return A [PostListing] containing the fetched posts
     */
    @GET("r/{name}/{sort}")
    suspend fun fetchSubreddit(
        @Path("name") name: String,
        @Path("sort") sort: String,
        @Query("t") time: String,
        @Query("after") after: String? = null,
        @Query("raw_json") rawJson: Int = 1,
        @Query("limit") limit: Int = 5
    ): PostListing

    /**
     * Search posts globally (or within a subreddit if using /r/{subreddit}/search endpoint)
     * @param query The search query string
     * @param sort Sorting for the search (relevance, hot, new, top, comments)
     * @param time Time filter (use [com.pineapple.app.consts.PostFilterTime])
     * @param after Pagination token
     * @param rawJson Whether to get raw JSON (1) or not (0)
     * @param limit Number of results to fetch
     */
    @GET("search")
    suspend fun searchPosts(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("t") time: String? = null,
        @Query("after") after: String? = null,
        @Query("raw_json") rawJson: Int = 1,
        @Query("limit") limit: Int = 25
    ): PostListing

    /**
     * Fetch a specific post and its comments
     * @param subreddit The name of the subreddit (without the r/ prefix)
     * @param postID The ID of the post
     * @param post The post's slug (title in URL-friendly format)
     * @param rawJson Whether to get raw JSON (1) or not (0)
     * @return A [String] containing the raw JSON response
     */
    @GET("r/{name}/comments/{id}/{post}")
    suspend fun fetchPost(
        @Path("name") subreddit: String,
        @Path("id") postID: String,
        @Path("post") post: String,
        @Query("raw_json") rawJson: Int = 1
    ): List<Listing<ListingItem<PostData>>>

    /**
     * Fetch a specific post and its comments using only the post id (no subreddit)
     * This hits /comments/{id} which returns the post listing and is useful when we don't have the subreddit/slug cached
     */
    @GET("comments/{id}")
    suspend fun fetchPostById(
        @Path("id") postID: String,
        @Query("raw_json") rawJson: Int = 1
    ): List<Listing<ListingItem<PostData>>>

    /**
     * Fetch the subreddits the authenticated user is subscribed to
     * @return A [Listing] of [SubredditItem] representing the subscribed subreddits
     */
    @GET("/subreddits/mine/subscriber")
    suspend fun fetchSubscribedSubreddits(
        @Query("raw_json") rawJson: Int = 1,
        @Query("after") after: String? = null,
        @Query("limit") limit: Int = 6
    ): Listing<SubredditItem>

    /**
     * Fetch the current trending subreddits
     * @return A [Listing] of [SubredditItem] representing the top subreddits
     */
    @GET("subreddits/popular")
    suspend fun fetchTopSubreddits(
        @Query("raw_json") rawJson: Int = 1,
        @Query("after") after: String? = null,
        @Query("limit") limit: Int = 5
    ): Listing<SubredditItem>

    /**
     * Fetch the current popular users
     * @return A [Listing] of [CondensedUserAboutListing] representing the top users
     */
    @GET("users/popular")
    suspend fun fetchTopUsers(
        @Query("raw_json") rawJson: Int = 1
    ): Listing<CondensedUserAboutListing>

    /**
     * Fetch detailed information about a specific user
     * @param user The username of the user
     * @param rawJson Whether to get raw JSON (1) or not (0)
     * @return A [UserAboutListing] containing the user's information
     */
    @GET("/user/{user}/about")
    suspend fun fetchUserInfo(
        @Path("user") user: String,
        @Query("raw_json") rawJson: Int = 1
    ) : UserAboutListing

    /**
     * Cast an upvote, downvote, or remove vote on a post or comment
     * @param id The full name of the post or comment (with the t* prefix)
     * @param dir The direction of the vote: 1 (upvote), -1 (downvote), 0 (remove vote)
     */
    @POST("/api/vote")
    suspend fun castVote(
        @Query("id") id: String,
        @Query("dir") dir: Int
    )

    /**
     * Save a post or comment to the user's saved items
     * @param id The full name of the post or comment (with the t*_ prefix)
     */
    @POST("/api/save")
    suspend fun savePost(
        @Query("id") id: String
    )

    /**
     * Unsave a post or comment from the user's saved items
     * @param id The full name of the post or comment (with the t*_ prefix)
     */
    @POST("/api/unsave")
    suspend fun unsavePost(
        @Query("id") id: String
    )

    /**
     * Search for communities (subreddits) by query. Returns Listing<SubredditItem>
     */
    @GET("search")
    suspend fun searchCommunities(
        @Query("q") query: String,
        @Query("type") type: String = "sr",
        @Query("raw_json") rawJson: Int = 1,
        @Query("limit") limit: Int = 6
    ): Listing<SubredditItem>

    /**
     * Search for users by query. Returns ListingBase<UserAboutListing>
     */
    @GET("search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("type") type: String = "user",
        @Query("raw_json") rawJson: Int = 1,
        @Query("limit") limit: Int = 6
    ): ListingBase<UserAboutListing>

    /**
     * Fetch comments for a post id (comments/{id}), returns the same structure as fetchPost
     */
    @GET("comments/{id}")
    suspend fun fetchCommentsByPostId(
        @Path("id") postID: String,
        @Query("raw_json") rawJson: Int = 1
    ): List<Listing<CommentPreData>>


}

/**
 * Qualifier annotation for Retrofit instance used for authenticated requests
 * (to differentiate between this and [RedditTokenApi] in dependency injection)
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthRetrofit