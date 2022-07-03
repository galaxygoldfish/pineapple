package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.paging.RequestResult
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.model.reddit.UserItem
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkService
import kotlinx.coroutines.flow.flow

class SearchViewModel : ViewModel() {

    private val networkService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }

    var currentSearchQuery by mutableStateOf(TextFieldValue())
    var currentSearchFilter by mutableStateOf(0)
    var currentPostList = mutableStateListOf<PostItem>()
    var currentSubredditList = mutableStateListOf<SubredditItem>()
    var currentUserList = mutableStateListOf<UserItem>()

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
            1 -> {
                networkService.searchPosts(currentSearchQuery.text)
                    .data.children
                    .processToList(currentPostList)
            }
            2 -> {
                networkService.searchCommunities(currentSearchQuery.text)
                    .data.children
                    .processToList(currentSubredditList)
            }
            3 -> {
                networkService.searchUsers(currentSearchQuery.text)
                    .data.children
                    .processToList(currentUserList)
            }
        }
    }

    private fun <T> List<T>.processToList(list: SnapshotStateList<T>) {
        val elements = this
        list.apply {
            clear()
            addAll(elements)
        }
    }

}