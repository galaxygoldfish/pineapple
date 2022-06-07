package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.network.GfycatNetworkService
import com.pineapple.app.paging.RequestResult
import com.pineapple.app.network.NetworkServiceBuilder
import com.pineapple.app.network.NetworkServiceBuilder.GFYCAT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.NetworkServiceBuilder.rawApiService
import com.pineapple.app.network.RedditNetworkService
import kotlinx.coroutines.flow.flow
import org.json.JSONArray

class PostDetailViewModel : ViewModel() {

    var postData by mutableStateOf<Triple<String, String, String>?>(null)
    val redditServiceRaw by lazy { rawApiService<RedditNetworkService>(REDDIT_BASE_URL) }
    val redditService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }
    val gfycatService by lazy { apiService<GfycatNetworkService>(GFYCAT_BASE_URL) }

    suspend fun postRequestFlow() = flow {
        emit(RequestResult.Loading(true))
        val response = postData?.let { redditServiceRaw.fetchPost(it.first, it.second, it.third) }
        if (response != null) {
            emit(RequestResult.Success(JSONArray(response)))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }
}