package com.pineapple.app.network

import com.pineapple.app.model.reddit.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {

    @GET("r/{name}/{sort}")
    fun fetchSubreddit(
        @Path("name") name: String,
        @Path("sort") sort: String,
        @Query("after") after: String? = null
    ) : Call<PostListing>

    @GET("r/{name}/comments/{id}/{post}")
    suspend fun fetchPost(
        @Path("name") subreddit: String,
        @Path("id") postID: String,
        @Path("post") post: String
    ) : String

    @GET("subreddits/popular")
    suspend fun fetchTopSubreddits() : Listing<SubredditItem>

    @GET("r/{subreddit}/info.json")
    suspend fun fetchSubredditInfo(
        @Path("subreddit") subreddit: String
    ) : SubredditInfo

    @GET("/user/{user}/about")
    suspend fun fetchUserInfo(
        @Path("user") user: String
    ) : UserAboutListing

    @GET("search")
    suspend fun searchPosts(@Query("q") query: String) : Listing<PostItem>

}