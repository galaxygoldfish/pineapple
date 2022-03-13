package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pineapple.app.model.PostItem
import com.pineapple.app.network.NetworkPagingSource
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.Flow

class PostListViewModel : ViewModel() {

    private val networkService by lazy { NetworkServiceBuilder.apiService() }
    var isRefreshingData by mutableStateOf(false)

    fun posts(name: String, sort: String): Flow<PagingData<PostItem>> = Pager(
        PagingConfig(3)) {
            NetworkPagingSource(networkService, name, sort)
        }.flow

}