package com.pineapple.app.network

import com.pineapple.app.model.gfycat.GfycatObject
import retrofit2.http.GET
import retrofit2.http.Path

interface GfycatNetworkService {

    @GET("{gfyName}")
    suspend fun fetchGif(
        @Path("gfyName") gfyName: String
    ) : GfycatObject

}