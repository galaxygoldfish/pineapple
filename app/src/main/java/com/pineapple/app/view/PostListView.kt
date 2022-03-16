package com.pineapple.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pineapple.app.components.TextPostCard
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.isLoading
import com.pineapple.app.viewmodel.PostListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PostListView(
    navController: NavController,
    subreddit: String,
    sort: String
) {
    val viewModel = LocalContext.current.getViewModel(PostListViewModel::class.java)
    val currentPosts = viewModel.posts(subreddit, sort).collectAsLazyPagingItems()
    val refreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isRefreshingData)
    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            viewModel.apply {
                isRefreshingData = true
                currentPosts.refresh()
                if (!currentPosts.loadState.isLoading()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        isRefreshingData = false
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        LazyColumn {
            items(currentPosts) { item ->
                TextPostCard(postData = item!!.data)
            }
        }
    }
}