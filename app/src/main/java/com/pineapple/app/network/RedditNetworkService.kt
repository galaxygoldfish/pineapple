package com.pineapple.app.network

import com.pineapple.app.model.reddit.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditNetworkService {

    @GET("r/{name}/{sort}")
    fun fetchSubreddit(
        @Path("name") name: String,
        @Path("sort") sort: String,
        @Query("time") time: String,
        @Query("after") after: String? = null,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : Call<PostListing>

    @GET("r/{name}/comments/{id}/{post}")
    suspend fun fetchPost(
        @Path("name") subreddit: String,
        @Path("id") postID: String,
        @Path("post") post: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : String

    @GET("subreddits/popular")
    suspend fun fetchTopSubreddits() : Listing<SubredditItem>

    @GET("users/popular")
    suspend fun fetchTopUsers() : Listing<UserItem>

    @GET("r/{subreddit}/about")
    suspend fun fetchSubredditInfo(
        @Path("subreddit") subreddit: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : SubredditInfo

    @GET("/user/{user}/about")
    suspend fun fetchUserInfo(
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : UserAboutListing

    // repeated functions below because compiler removes generic types in retrofit calls >._.<

    @GET("search")
    suspend fun searchPosts(
        @Query("q") query: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : Listing<PostItem>

    @GET("search")
    suspend fun searchCommunities(
        @Query("q") query: String,
        @Query("type") type: String = "sr",
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : Listing<SubredditItem>

    @GET("search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("type") type: String = "user",
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off"
    ) : ListingBase<UserItem>

}