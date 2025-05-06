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

    /**
     * Cast vote on a given post (upvote, downvote, remove vote)
     * @param id - The fullname of a post (found in the name field)
     * @param context - The parent context
     * @param direction - 1 for upvote, -1 for downvote, 0 for remove vote
     */
    suspend fun castPostVote(id: String, context: Context, direction: Int) {
        RedditNetworkProvider(context).castVote(id, direction)
    }

}