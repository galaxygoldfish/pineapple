package com.pineapple.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.network.RedditNetworkProvider

class HomePageViewModel : ViewModel() {

    var selectedTabItem by mutableStateOf(0)
    val currentSortType = mutableStateOf("Hot")
    val currentSortTime = mutableStateOf("All time")
    val popularSubreddits = mutableStateListOf<SubredditItem>()

    suspend fun refreshTopCommunities(context: Context) {
        popularSubreddits.apply {
            clear()
            addAll(RedditNetworkProvider(context).fetchTopSubreddits().data.children)
        }
    }

}