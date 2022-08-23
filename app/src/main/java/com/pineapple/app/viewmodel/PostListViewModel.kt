package com.pineapple.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.pineapple.app.network.RedditNetworkProvider
import com.pineapple.app.paging.NetworkPagingSource

class PostListViewModel : ViewModel() {

    var isRefreshingData by mutableStateOf(false)

    fun posts(name: String, sort: String, time: String, context: Context) = Pager(PagingConfig(3)) {
        NetworkPagingSource(RedditNetworkProvider(context), name, sort, time)
    }.flow

}