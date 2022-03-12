package com.pineapple.app.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pineapple.app.components.TextPostCard
import com.pineapple.app.util.getViewModel
import com.pineapple.app.viewmodel.PostListViewModel

@Composable
fun PostListView(
    navController: NavController,
    subreddit: String,
    sort: String
) {
    val viewModel = LocalContext.current.getViewModel(PostListViewModel::class.java)
    val refreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isRefreshing)
    LaunchedEffect(true) {
        viewModel.performRequest(subreddit, sort)
    }
    SwipeRefresh(
        state = refreshState,
        onRefresh = { viewModel.performRequest(subreddit, sort) }
    ) {
        if (viewModel.data != null) {
            LazyColumn {
                itemsIndexed(viewModel.data!!.data.children) { _, item ->
                    TextPostCard(postData = item.data)
                }
            }
        }
    }
}