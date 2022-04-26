package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.RequestResult
import com.pineapple.app.model.reddit.ListingItem
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.PostListing
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.Collections.addAll

class SearchViewModel : ViewModel() {

    private val networkService by lazy { NetworkServiceBuilder.apiService() }
    var currentSearchQuery by mutableStateOf(TextFieldValue())
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
                response.data.children?.let {
                    currentPostList.apply {
                        clear()
                        addAll(it)
                    }
                }
            }
        }
    }

}