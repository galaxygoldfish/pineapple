package com.pineapple.app.view

import android.os.Bundle
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
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

@OptIn(ExperimentalAnimationApi::class)
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
                    modifier = Modifier.align(Alignment.Center).size(50.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        AnimatedVisibility(
            visible = currentPosts.itemSnapshotList.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn {
                itemsIndexed(currentPosts) { index, item ->
                    TextPostCard(
                        postData = item!!.data,
                        onClick = {
                            val permalink = item.data.permalink.split("/")
                            val sub = permalink[2]
                            val uid = permalink[4]
                            val link = permalink[5]
                            navController.navigate("${NavDestination.PostDetailView}/$sub/$uid/$link")
                        },
                        navController = navController,
                        modifier = Modifier.animateEnterExit(
                            enter = slideInVertically(animationSpec = spring(0.8F)) { it * (index + 1) }
                        )
                    )
                }
            }
        }
    }
}