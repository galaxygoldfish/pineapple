package com.pineapple.app.view

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pineapple.app.NavDestination
import com.pineapple.app.components.PostCard
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.isLoading
import com.pineapple.app.viewmodel.PostListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PostListView(
    navController: NavController,
    subreddit: String,
    sort: String,
    time: String = "hour",
    scrollState: LazyListState? = null,
    topHeaderItem: (@Composable () -> Unit) = { }
) {
    val context = LocalContext.current
    val viewModel = context.getViewModel(PostListViewModel::class.java)
    var currentPostFlow by remember { mutableStateOf<Flow<PagingData<PostItem>>?>(null) }
    var currentPosts by remember { mutableStateOf<LazyPagingItems<PostItem>?>(null) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isRefreshingData)

    LaunchedEffect(key1 = subreddit, key2 = sort, key3 = time) {
        currentPostFlow = viewModel.posts(subreddit, sort, time, context)
    }
    currentPostFlow?.let { currentPosts = it.collectAsLazyPagingItems() }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            viewModel.apply {
                isRefreshingData = true
                currentPosts?.refresh()
                if (currentPosts?.loadState?.isLoading() == false) {
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
        if (currentPosts?.loadState?.isLoading() == true) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        AnimatedVisibility(
            visible = currentPosts?.itemSnapshotList?.isNotEmpty() == true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(state = scrollState ?: rememberLazyListState()) {
                item {
                    topHeaderItem.invoke()
                }
                currentPosts?.let {
                    itemsIndexed(it) { index, item ->
                        PostCard(
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
}