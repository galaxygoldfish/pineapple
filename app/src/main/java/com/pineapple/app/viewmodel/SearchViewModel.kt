package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.paging.RequestResult
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.network.NetworkServiceBuilder
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkService
import kotlinx.coroutines.flow.flow

class SearchViewModel : ViewModel() {

    private val networkService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }
    var currentSearchQuery by mutableStateOf(TextFieldValue())
    var lastUpdateSearch by mutableStateOf(System.currentTimeMillis())
    var currentSearchFilter by mutableStateOf(0)
    var currentPostList = mutableStateListOf<PostItem>()

    suspend fun requestSubredditFlow() = flow {
        emit(RequestResult.Loading(true))
        val response = networkService.fetchTopSubreddits()
        if (response.data.children.isNotEmpty()) {
            emit(RequestResult.Success(response.data.children))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }

    suspend fun updateSearchResults() {
        when (currentSearchFilter) {
            0 -> {
                val response = networkService.searchPosts(currentSearchQuery.text)
                response.data.children.let {
                    currentPostList.apply {
                        clear()
                        addAll(it)
                    }
                }
            }
        }
    }

}