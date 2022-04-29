package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.RequestResult
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.flow

class SubredditViewModel : ViewModel() {

    var currentSubreddit by mutableStateOf<String?>(null)
    private val networkService by lazy { NetworkServiceBuilder.apiService() }

    suspend fun fetchInformation() = flow {
        emit(RequestResult.Loading(true))
        val response = currentSubreddit?.let { networkService.fetchSubredditInfo(it) }
        if (response != null) {
            emit(RequestResult.Success(response.data))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }

}