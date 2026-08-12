package com.pineapple.app.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pineapple.app.R
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.ui.components.CommentCard
import com.pineapple.app.ui.components.PostCard
import com.pineapple.app.ui.modal.CommentDetailSheet
import com.pineapple.app.ui.modal.PostOptionSheet
import com.pineapple.app.ui.theme.PineappleTheme
import com.pineapple.app.ui.viewmodel.UserViewModel
import com.pineapple.app.utilities.getBestUserImage
import com.pineapple.app.utilities.toPostData
import com.pineapple.app.utilities.toUserAboutListing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserView(navController: NavController, user: String) {
    val viewModel: UserViewModel = hiltViewModel()
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var selectedIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posts", "Comments")
    
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    var selectedPost by remember { mutableStateOf<PostData?>(null) }
    var selectedComment by remember { mutableStateOf<CommentWithUser?>(null) }
    var showPostOptions by remember { mutableStateOf(false) }
    var showCommentOptions by remember { mutableStateOf(false) }
    
    LaunchedEffect(user) {
        viewModel.loadUser(user)
    }

    val userState = viewModel.user.collectAsState()
    val userData = userState.value
    val postItems = viewModel.posts.collectAsLazyPagingItems()
    val commentItems = viewModel.comments.collectAsLazyPagingItems()

    val isHeaderStuck by remember {
        derivedStateOf { listState.firstVisibleItemIndex >= 1 }
    }

    val onRefresh = {
        isRefreshing = true
        viewModel.loadUser(user)
        postItems.refresh()
        commentItems.refresh()
        isRefreshing = false
    }

    val headerColor by animateColorAsState(
        targetValue = if (isHeaderStuck) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "HeaderColorAnimation"
    )

    val context = LocalContext.current
    PineappleTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = isHeaderStuck,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = (userData?.displayName ?: userData?.name ?: "").removePrefix("u_"),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = stringResource(R.string.ic_back_cdesc)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_vert),
                                contentDescription = stringResource(R.string.ic_more_vert_cdesc)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            }
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                        ) {
                            if (viewModel.isLoading && userData == null) {
                                CircularProgressIndicator()
                            } else if (userData != null) {
                                AsyncImage(
                                    model = getBestUserImage(userData.snoovatarUrl, userData.iconUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                )
                                Text(
                                    text = (userData.displayName ?: userData.name).removePrefix("u_"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 15.dp)
                                )
                                Text(
                                    text = "u/${userData.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                userData.createdUtc?.let { created ->
                                    val date = remember(created) {
                                        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
                                            .withZone(ZoneId.systemDefault())
                                            .format(Instant.ofEpochSecond(created))
                                    }
                                    Text(
                                        text = "Joined $date · ${userData.totalKarma} karma",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = headerColor
                        ) {
                            ButtonGroup(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                overflowIndicator = {},
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tabs.forEachIndexed { index, label ->
                                    toggleableItem(
                                        checked = selectedIndex == index,
                                        onCheckedChange = { selectedIndex = index },
                                        label = label,
                                        weight = 1f
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Spacer
                    }

                    if (tabs[selectedIndex] == "Posts") {
                        items(
                            count = postItems.itemCount,
                            key = { index -> postItems[index]?.post?.id ?: index }
                        ) { index ->
                            val item = postItems[index] ?: return@items
                            val postData = item.post.toPostData()
                            PostCard(
                                postData = postData,
                                userInfo = item.user?.toUserAboutListing(),
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                                    .combinedClickable(
                                        onClick = {
                                            navController.navigate("${NavDestinationKey.PostView}/${item.post.id.removePrefix("t3_")}")
                                        },
                                        onLongClick = {
                                            selectedPost = postData
                                            showPostOptions = true
                                        }
                                    ),
                                onClick = {
                                    navController.navigate("${NavDestinationKey.PostView}/${item.post.id.removePrefix("t3_")}")
                                },
                                onMoreClick = {
                                    selectedPost = postData
                                    showPostOptions = true
                                },
                                onSaveClick = { newState, onSuccess ->
                                    postData.id?.let {
                                        onSuccess()
                                        viewModel.updatePostFavorite(newState, it)
                                    }
                                },
                                onUpvote = { intention, onSuccess ->
                                    postData.id?.let { postID ->
                                        onSuccess()
                                        viewModel.updatePostVote(
                                            postId = postID,
                                            direction = if (intention) 1 else 0
                                        )
                                    }
                                },
                                onDownvote = { intention, onSuccess ->
                                    postData.id?.let { postID ->
                                        onSuccess()
                                        viewModel.updatePostVote(
                                            postId = postID,
                                            direction = if (intention) -1 else 0
                                        )
                                    }
                                }
                            )
                        }
                    } else if (tabs[selectedIndex] == "Comments") {
                        items(
                            count = commentItems.itemCount,
                            key = { index -> commentItems[index]?.comment?.id ?: index }
                        ) { index ->
                            val item = commentItems[index] ?: return@items
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = item.comment.linkTitle ?: "Post",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(start = 12.dp, bottom = 4.dp)
                                        .clickable {
                                            navController.navigate("${NavDestinationKey.PostView}/${item.comment.postId.removePrefix("t3_")}")
                                        }
                                )
                                CommentCard(
                                    commentWithUser = item,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    modifier = Modifier.combinedClickable(
                                        onClick = {
                                            navController.navigate("${NavDestinationKey.PostView}/${item.comment.postId.removePrefix("t3_")}")
                                        },
                                        onLongClick = {
                                            selectedComment = item
                                            showCommentOptions = true
                                        }
                                    ),
                                    onMoreClick = {
                                        selectedComment = item
                                        showCommentOptions = true
                                    },
                                    onUpvote = { intention, onSuccess ->
                                        onSuccess()
                                        viewModel.updatePostVote(item.comment.id, if (intention) 1 else 0)
                                    },
                                    onDownvote = { intention, onSuccess ->
                                        onSuccess()
                                        viewModel.updatePostVote(item.comment.id, if (intention) -1 else 0)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPostOptions && selectedPost != null) {
        PostOptionSheet(
            postData = selectedPost!!,
            onDismissRequest = { showPostOptions = false },
            onViewUser = {
                showPostOptions = false
                navController.navigate("${NavDestinationKey.UserView}/${selectedPost!!.author}")
            },
            onViewCommunity = {
                showPostOptions = false
                navController.navigate("${NavDestinationKey.CommunityView}/${selectedPost!!.subreddit}")
            },
            onOpenExternal = {
                showPostOptions = false
                Intent(Intent.ACTION_VIEW).apply {
                    this.data = "https://www.reddit.com${selectedPost!!.permalink}".toUri()
                    context.startActivity(this)
                }
            },
            onReport = {
                showPostOptions = false
                Intent(Intent.ACTION_VIEW).apply {
                    this.data = ("https://www.reddit.com/report"
                            + "?url=https://www.reddit.com${selectedPost!!.permalink}").toUri()
                    context.startActivity(this)
                }
            }
        )
    }
    if (showCommentOptions && selectedComment != null) {
        CommentDetailSheet(
            commentWithUser = selectedComment!!,
            onDismissRequest = { showCommentOptions = false },
            onUpvote = { intention, onSuccess ->
                onSuccess()
                viewModel.updatePostVote(selectedComment!!.comment.id, if (intention) 1 else 0)
            },
            onDownvote = { intention, onSuccess ->
                onSuccess()
                viewModel.updatePostVote(selectedComment!!.comment.id, if (intention) -1 else 0)
            },
            onSaveClick = { newState, onSuccess ->
                onSuccess()
                viewModel.updatePostFavorite(newState, selectedComment!!.comment.id)
            },
            onViewUserClick = {
                showCommentOptions = false
                navController.navigate("${NavDestinationKey.UserView}/${selectedComment!!.comment.author}")
            }
        )
    }
}
