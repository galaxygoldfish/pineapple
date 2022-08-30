package com.pineapple.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.network.RedditNetworkProvider
import com.pineapple.app.paging.RequestResult
import kotlinx.coroutines.flow.flow

class SubredditViewModel : ViewModel() {

    var currentSubreddit by mutableStateOf<String?>(null)
    var currentSortTime = mutableStateOf("hour")
    var currentSortType = mutableStateOf("hot")

    suspend fun fetchInformation(context: Context) = flow {
        emit(RequestResult.Loading(true))
        val response = currentSubreddit?.let {
            RedditNetworkProvider(context).fetchSubredditInfo(it)
        }
        if (response != null) {
            emit(RequestResult.Success(response.data))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }

}