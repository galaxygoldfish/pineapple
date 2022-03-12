package com.pineapple.app.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pineapple.app.components.TextPostCard
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.isLoading
import com.pineapple.app.viewmodel.PostListViewModel

@Composable
fun PostListView(
    navController: NavController,
    subreddit: String,
    sort: String
) {
    val viewModel = LocalContext.current.getViewModel(PostListViewModel::class.java)
    val currentPosts = viewModel.posts(subreddit, sort).collectAsLazyPagingItems()
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    SwipeRefresh(
        state = refreshState,
        onRefresh = {

        }
    ) {
        LazyColumn {
            items(currentPosts) { item ->
                TextPostCard(postData = item!!.data)
            }
        }
    }
}