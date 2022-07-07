package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.network.NetworkServiceBuilder
import com.pineapple.app.network.RedditNetworkService

class HomePageViewModel : ViewModel() {

    var selectedTabItem by mutableStateOf(0)
    val currentSortType = mutableStateOf("Hot")
    val currentSortTime = mutableStateOf("All time")
    val popularSubreddits = mutableStateListOf<SubredditItem>()

    private val networkService by lazy {
        NetworkServiceBuilder.apiService<RedditNetworkService>(
            NetworkServiceBuilder.REDDIT_BASE_URL
        )
    }

    suspend fun refreshTopCommunities() {
        popularSubreddits.apply {
            clear()
            addAll(networkService.fetchTopSubreddits().data.children)
        }
    }

}