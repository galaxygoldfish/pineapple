package com.pineapple.app.network

import com.pineapple.app.BuildConfig
import com.pineapple.app.model.auth.AuthResponse
import com.pineapple.app.model.reddit.*
import okhttp3.Credentials
import retrofit2.http.*

interface RedditNetworkService {

    @FormUrlEncoded
    @POST("/api/v1/access_token")
    suspend fun authenticateUserless(
        @Header("Authorization") basicAuth: String = Credentials.basic(username = "dcE5CW6FaoNpWasp243QwQ", password = ""),
        @Field("grant_type") grantType: String = "https://oauth.reddit.com/grants/installed_client",
        @Field("device_id") deviceID: String = "DO_NOT_TRACK_THIS_DEVICE",
        @Field("User-Agent") userAgent: String = "android:com.pineapple.app:${BuildConfig.VERSION_NAME}"
    ) : AuthResponse

    @GET("r/{name}/{sort}")
    suspend fun fetchSubreddit(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Path("name") name: String,
        @Path("sort") sort: String,
        @Query("t") time: String,
        @Query("after") after: String? = null,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : PostListing

    @GET("r/{name}/comments/{id}/{post}")
    suspend fun fetchPost(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Path("name") subreddit: String,
        @Path("id") postID: String,
        @Path("post") post: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : String

    @GET("subreddits/popular")
    suspend fun fetchTopSubreddits(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<SubredditItem>

    @GET("users/popular")
    suspend fun fetchTopUsers(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<CondensedUserAboutListing>

    @GET("r/{subreddit}/about")
    suspend fun fetchSubredditInfo(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Path("subreddit") subreddit: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : SubredditInfo

    @GET("/user/{user}/about")
    suspend fun fetchUserInfo(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : UserAboutListing


    @GET("search")
    suspend fun searchPosts(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Query("q") query: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<PostItem>

    @GET("search")
    suspend fun searchCommunities(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Query("q") query: String,
        @Query("type") type: String = "sr",
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : Listing<SubredditItem>

    @GET("search")
    suspend fun searchUsers(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Query("q") query: String,
        @Query("type") type: String = "user",
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : ListingBase<UserAboutListing>

    @GET("u/{user}/submitted")
    suspend fun getUserPosts(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : ListingBase<PostItem>

    @GET("u/{user}/comments")
    suspend fun getUserComments(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Path("user") user: String,
        @Query("nsfw") nsfw: Int = 0,
        @Query("include_over_18") over18: String = "off",
        @Query("raw_json") rawJson: Int = 1
    ) : ListingBase<CommentPreDataNull>

}