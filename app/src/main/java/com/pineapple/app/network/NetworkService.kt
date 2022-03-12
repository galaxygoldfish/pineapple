package com.pineapple.app.network

import com.pineapple.app.model.PostListing
import retrofit2.Call
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

}