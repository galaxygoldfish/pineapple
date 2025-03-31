package com.pineapple.app.view

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
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

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
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
        AnimatedVisibility(
            visible = currentPosts?.itemSnapshotList != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                state = scrollState ?: rememberLazyListState(),
                modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
            ) {
                item {
                    topHeaderItem.invoke()
                }
                stickyHeader {
                    AnimatedVisibility(
                        visible = currentPosts?.loadState?.isLoading() == true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                currentPosts?.let {
                    items(it.itemCount) { index->
                        val item = it.peek(index)
                        PostCard(
                            postData = item!!.data,
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