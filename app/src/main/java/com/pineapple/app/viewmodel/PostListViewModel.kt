package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.network.NetworkServiceBuilder
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkService
import com.pineapple.app.paging.NetworkPagingSource
import kotlinx.coroutines.flow.Flow

class PostListViewModel : ViewModel() {

    private val networkService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }
    var isRefreshingData by mutableStateOf(false)

    fun posts(name: String, sort: String, time: String) = Pager(PagingConfig(3)) {
        NetworkPagingSource(networkService, name, sort, time)
    }.flow

}