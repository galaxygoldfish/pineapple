package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.paging.RequestResult
import com.pineapple.app.network.NetworkServiceBuilder
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkService
import kotlinx.coroutines.flow.flow

class SubredditViewModel : ViewModel() {

    var currentSubreddit by mutableStateOf<String?>(null)
    var currentSortTime = mutableStateOf("hour")
    var currentSortType = mutableStateOf("hot")
    private val networkService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }

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