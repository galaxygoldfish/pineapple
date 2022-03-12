package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import com.pineapple.app.model.PostItem
import com.pineapple.app.model.PostListing
import com.pineapple.app.network.NetworkPagingSource
import com.pineapple.app.network.NetworkRepository
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map

class PostListViewModel : ViewModel() {

    var data by mutableStateOf<PostListing?>(null)
    var isRefreshing by mutableStateOf(true)

    private val networkService by lazy { NetworkServiceBuilder.apiService() }


    fun posts(name: String, sort: String): Flow<PagingData<PostItem>> = Pager(PagingConfig(3)) {
        NetworkPagingSource(networkService, name, sort)
    }.flow


    fun performRequest(name: String, sort: String) : Flow<PagingData<PostItem>> {
        return NetworkRepository(networkService)
            .fetchSubreddit(name, sort)
            .cachedIn(viewModelScope)
    }
}