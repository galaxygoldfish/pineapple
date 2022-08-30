package com.pineapple.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.network.GfycatNetworkService
import com.pineapple.app.network.NetworkServiceBuilder.GFYCAT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkProvider
import com.pineapple.app.paging.RequestResult
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject

class PostDetailViewModel : ViewModel() {

    var postData by mutableStateOf<Triple<String, String, String>?>(null)
    var replyViewOriginalComment by mutableStateOf<JSONObject?>(null)
    var replyViewCommentList by mutableStateOf<JSONArray?>(null)
    val gfycatService by lazy { apiService<GfycatNetworkService>(GFYCAT_BASE_URL) }

    suspend fun postRequestFlow(context: Context) = flow {
        emit(RequestResult.Loading(true))
        val response = postData?.let {
            RedditNetworkProvider(context).fetchPost(it.first, it.second, it.third)
        }
        if (response != null) {
            emit(RequestResult.Success(JSONArray(response)))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }



}