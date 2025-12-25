package com.pineapple.app.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.ui.components.PostCard
import com.pineapple.app.ui.viewmodel.BrowseViewModel
import com.pineapple.app.utilities.toPostData
import com.pineapple.app.utilities.toUserAboutListing

@Composable
fun BrowsePage(
    onRequestUserAuth: () -> Unit,
    onRequestPostDetailSheet: (PostData) -> Unit,
    navController: NavController
) {
    val viewModel: BrowseViewModel = hiltViewModel()
    val pagingItems = viewModel.pagedPosts.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pagingItems.loadState.refresh) {
        val refresh = pagingItems.loadState.refresh
        if (refresh !is LoadState.Loading && viewModel.shouldScrollToTopAfterRefresh) {
            viewModel.postListState.animateScrollToItem(0)
        }
    }

    PullToRefreshBox(
        onRefresh = {
            pagingItems.refresh()
        },
        isRefreshing = false,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = viewModel.postListState
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = { index ->
                        pagingItems[index]?.post?.id ?: index
                    }
                ) { index ->
                    val item = pagingItems[index] ?: return@items
                    val postData = item.post.toPostData()
                    val userInfo = item.user?.toUserAboutListing()

                    PostCard(
                        postData = postData,
                        modifier = Modifier.padding(
                            vertical = 5.dp,
                            horizontal = 10.dp
                        ),
                        userInfo = userInfo,
                        onClick = {
                            viewModel.shouldScrollToTopAfterRefresh = false
                            android.util.Log.e("BrowsePage", "navigating to post detail for id=${postData.id}")
                            navController.navigate("${NavDestinationKey.PostView}/${postData.id}")
                        },
                        onMoreClick = {
                            onRequestPostDetailSheet(postData)
                        },
                        onSaveClick = { newState, onSuccess ->
                            if (!viewModel.isUserless) {
                                postData.id?.let {
                                    onSuccess()
                                    viewModel.updatePostFavorite(newState, it)
                                }
                            } else {
                                onRequestUserAuth()
                            }
                        },
                        onUpvote = { intention, onSuccess ->
                            if (!viewModel.isUserless) {
                                postData.id?.let { postID ->
                                    onSuccess()
                                    viewModel.updatePostVote(
                                        postId = postID,
                                        direction = if (intention) 1 else 0
                                    )
                                }
                            } else {
                                onRequestUserAuth()
                            }
                        },
                        onDownvote = { intention, onSuccess ->
                            if (!viewModel.isUserless) {
                                postData.id?.let { postID ->
                                    onSuccess()
                                    viewModel.updatePostVote(
                                        postId = postID,
                                        direction = if (intention) -1 else 0
                                    )
                                }
                            } else {
                                onRequestUserAuth()
                            }
                        }
                    )
                }
            }
        }
    }
}