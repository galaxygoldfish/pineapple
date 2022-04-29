package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.RequestResult
import com.pineapple.app.model.reddit.CommentData
import com.pineapple.app.model.reddit.ListingBase
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.model.reddit.PostListing
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.flow
import org.json.JSONArray

class PostDetailViewModel : ViewModel() {

    var postData by mutableStateOf<Triple<String, String, String>?>(null)

    private val networkService by lazy { NetworkServiceBuilder.rawApiService() }

    suspend fun postRequestFlow() = flow {
        emit(RequestResult.Loading(true))
        val response = postData?.let { networkService.fetchPost(it.first, it.second, it.third) }
        if (response != null) {
            emit(RequestResult.Success(JSONArray(response)))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }

}