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
        @Query("t") time: String,
        @Query("after") after: String? = null,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : Call<PostListing>

    @GET("r/{name}/comments/{id}/{post}")
    suspend fun fetchPost(
        @Path("name") subreddit: String,
        @Path("id") postID: String,
        @Path("post") post: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : String

    @GET("subreddits/popular")
    suspend fun fetchTopSubreddits(
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<SubredditItem>

    @GET("users/popular")
    suspend fun fetchTopUsers(
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<CondensedUserAboutListing>

    @GET("r/{subreddit}/about")
    suspend fun fetchSubredditInfo(
        @Path("subreddit") subreddit: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : SubredditInfo

    @GET("/user/{user}/about")
    suspend fun fetchUserInfo(
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : UserAboutListing


    @GET("search")
    suspend fun searchPosts(
        @Query("q") query: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<PostItem>

    @GET("search")
    suspend fun searchCommunities(
        @Query("q") query: String,
        @Query("type") type: String = "sr",
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<SubredditItem>

    @GET("search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("type") type: String = "user",
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : ListingBase<UserAboutListing>

    @GET("u/{user}/submitted")
    suspend fun getUserPosts(
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : ListingBase<PostItem>

    @GET("u/{user}/comments")
    suspend fun getUserComments(
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : ListingBase<CommentPreDataNull>

}