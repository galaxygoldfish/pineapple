package com.pineapple.app.view

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pineapple.app.NavDestination
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
    sort: String,
    time: String = "hour"
) {
    val viewModel = LocalContext.current.getViewModel(PostListViewModel::class.java)
    val currentPosts = viewModel.posts(subreddit, sort, time).collectAsLazyPagingItems()
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (currentPosts.loadState.isLoading()) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 3.dp
                )
            }
        } else {
            LazyColumn {
                items(currentPosts) { item ->
                    TextPostCard(
                        postData = item!!.data,
                        onClick = {
                            val permalink = item.data.permalink.split("/")
                            val sub = permalink[2]
                            val uid = permalink[4]
                            val link = permalink[5]
                            navController.navigate("${NavDestination.PostDetailView}/$sub/$uid/$link")
                        }
                    )
                }
            }
        }
    }
}