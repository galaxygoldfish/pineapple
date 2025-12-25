@file:OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)

package com.pineapple.app.ui.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pineapple.app.network.model.cache.PostWithUser
import com.pineapple.app.network.model.reddit.SubredditData
import com.pineapple.app.network.model.reddit.UserAbout
import com.pineapple.app.network.paging.PagingRepository
import com.pineapple.app.network.repository.RedditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val pagingRepository: PagingRepository,
    private val redditRepository: RedditRepository
) : ViewModel() {

    var expandedSearchField by mutableStateOf(false)
    var searchFieldValue by mutableStateOf("")

    private val queryState = MutableStateFlow("")

    val postListState = LazyListState()

    // Suggestions: use model types for clarity
    val subredditSuggestions = MutableStateFlow<List<SubredditData>>(emptyList())
    val userSuggestions = MutableStateFlow<List<UserAbout>>(emptyList())

    init {
        // fetch suggestions when queryState changes with a short debounce
        viewModelScope.launch {
            queryState
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        subredditSuggestions.value = emptyList()
                        userSuggestions.value = emptyList()
                    } else {
                        // fetch suggestions (no caching)
                        try {
                            val subs = redditRepository.suggestCommunities(query, limit = 3)
                            subredditSuggestions.value = subs
                        } catch (_: Exception) {
                            subredditSuggestions.value = emptyList()
                        }
                        try {
                            val users = redditRepository.suggestUsers(query, limit = 3)
                            userSuggestions.value = users
                        } catch (_: Exception) {
                            userSuggestions.value = emptyList()
                        }
                    }
                }
        }
    }

    val searchResults = queryState
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty<PostWithUser>())
            } else {
                pagingRepository.searchPostsPager(query = query).flow
            }
        }
        .cachedIn(viewModelScope)

    // Called when user types in the search field - updates visible text and active query
    fun updateQueryText(newText: String) {
        searchFieldValue = newText
        queryState.value = newText
    }

    fun submitSearch() {
        expandedSearchField = false
        queryState.value = searchFieldValue
        postListState.requestScrollToItem(0)
    }

    // Clear the active search query
    fun clearSearchQuery() {
        queryState.value = ""
    }

}