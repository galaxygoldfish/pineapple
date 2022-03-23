package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.RequestResult
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.flow

class PostDetailViewModel : ViewModel() {

    var postData by mutableStateOf<Triple<String, String, String>?>(null)
    private val networkService by lazy { NetworkServiceBuilder.apiService() }

    suspend fun postRequestFlow() = flow {
        emit(RequestResult.Loading(true))
        val response = postData?.let { networkService.fetchPost(it.first, it.second, it.third) }
        if (response != null && response.isNotEmpty()) {
            emit(RequestResult.Success(response[0].data.children[0].data))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }

}