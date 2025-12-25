@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.pineapple.app.ui.view

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pineapple.app.R
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.ui.components.AnimatedTonalToggleIconButton
import com.pineapple.app.ui.components.CommentCard
import com.pineapple.app.ui.components.MeasuredAsyncImage
import com.pineapple.app.ui.modal.CommentDetailSheet
import com.pineapple.app.ui.modal.PostOptionSheet
import com.pineapple.app.ui.theme.MediumCornerRadius
import com.pineapple.app.ui.theme.PineappleTheme
import com.pineapple.app.ui.viewmodel.BrowseViewModel
import com.pineapple.app.ui.viewmodel.PostViewModel
import com.pineapple.app.utilities.convertUnixToRelativeTime
import com.pineapple.app.utilities.prettyNumber
import com.pineapple.app.utilities.toPostData

@Composable
fun PostView(navController: NavController, postID: String) {
    val viewModel: PostViewModel = hiltViewModel()
    val browseViewModel: BrowseViewModel = hiltViewModel()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val postWithUser = viewModel.postState.collectAsState()
    val comments = viewModel.comments.collectAsLazyPagingItems()
    val isLoading = viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(postID) {
        viewModel.loadPost(postID)
    }

    PineappleTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
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
                            onClick = {
                                viewModel.showingMoreSheet = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_vert),
                                contentDescription = stringResource(R.string.ic_more_vert_cdesc)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = {
                SnackbarHost(viewModel.snackbarState)
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                AnimatedContent(isLoading.value, label = "Post Loading Animation") {
                    if (it) {
                        Box(Modifier.fillMaxSize()) {
                            LoadingIndicator(
                                Modifier
                                    .size(75.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    } else {
                        postWithUser.value?.let { post ->
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                item {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                                .background(MaterialTheme.colorScheme.surfaceContainerLow),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                post.user?.let {
                                                    AsyncImage(
                                                        model = it.snoovatarUrl?.ifBlank { null }
                                                            ?: it.iconUrl,
                                                        contentDescription = null,
                                                        placeholder = painterResource(R.drawable.generic_avatar),
                                                        modifier = Modifier
                                                            .padding(15.dp)
                                                            .clip(CircleShape)
                                                            .size(35.dp)
                                                    )
                                                }
                                                Column {
                                                    post.post.author?.let {
                                                        Text(
                                                            text = "u/$it",
                                                            style = MaterialTheme.typography.titleSmall
                                                        )
                                                    }
                                                    post.post.subreddit?.let {
                                                        Text(
                                                            text = "r/$it",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }
                                            Row(modifier = Modifier.padding(end = 15.dp)) {
                                                FilledTonalIconButton(
                                                    onClick = {
                                                        navController.navigate("${NavDestinationKey.CommunityView}/${post.post.subreddit}")
                                                    },
                                                    modifier = Modifier.width(33.dp)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_community),
                                                        contentDescription = stringResource(R.string.ic_community_cdesc)
                                                    )
                                                }
                                                FilledTonalIconButton(
                                                    onClick = {
                                                        navController.navigate("${NavDestinationKey.UserView}/${post.post.author}")
                                                    },
                                                    modifier = Modifier
                                                        .padding(start = 10.dp)
                                                        .width(33.dp)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_person),
                                                        contentDescription = stringResource(R.string.ic_person_cdesc)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = post.post.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(
                                                top = 15.dp,
                                                start = 15.dp,
                                                end = 15.dp
                                            )
                                        )

                                        val width = post.post.previewWidth?.toFloat() ?: 0f
                                        val height = post.post.previewHeight?.toFloat() ?: 0f
                                        val imageUrl =
                                            post.post.previewImageUrl?.replace("amp;", "")
                                                ?.ifEmpty { post.post.url }
                                        val computedAspectRatio = if (width > 0f && height > 0f) {
                                            (width / height).coerceIn(0.2f, 4f)
                                        } else null

                                        if (imageUrl !== null && imageUrl.isNotEmpty()) {
                                            var aspectRatio by rememberSaveable(post.post.id + "_ratio") {
                                                mutableStateOf<Float?>(computedAspectRatio)
                                            }
                                            aspectRatio?.let {
                                                MeasuredAsyncImage(
                                                    imageUrl = imageUrl,
                                                    aspectRatio = it,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 15.dp)
                                                        .padding(top = 15.dp)
                                                        .clip(MaterialTheme.shapes.medium)
                                                )
                                            }
                                        }

                                        if (!post.post.selftext.isNullOrEmpty()) {
                                            Text(
                                                text = post.post.selftext,
                                                modifier = Modifier.padding(15.dp),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp)
                                                .padding(top = if (post.post.selftext.isNullOrEmpty()) 15.dp else 0.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                                .background(MaterialTheme.colorScheme.surfaceContainerLow),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            var upvoteState by remember { mutableStateOf(post.post.likes == true) }
                                            var downvoteState by remember { mutableStateOf(post.post.likes == false) }
                                            var saveState by remember { mutableStateOf(post.post.saved == true) }

                                            Row(
                                                modifier = Modifier.padding(
                                                    start = 5.dp,
                                                    top = 5.dp,
                                                    bottom = 5.dp
                                                )
                                            ) {
                                                FilledTonalIconButton(
                                                    onClick = {
                                                        post.post.permalink.let { url ->
                                                            val sendIntent = Intent().apply {
                                                                action = Intent.ACTION_SEND
                                                                type = "text/plain"
                                                                putExtra(
                                                                    Intent.EXTRA_TEXT,
                                                                    "https://reddit.com$url"
                                                                )
                                                            }
                                                            val shareIntent = Intent.createChooser(
                                                                sendIntent,
                                                                "Share post"
                                                            )
                                                            context.startActivity(shareIntent)
                                                        }
                                                    },
                                                    shape = MaterialTheme.shapes.medium
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_share),
                                                        contentDescription = stringResource(R.string.ic_share_cdesc)
                                                    )
                                                }
                                                AnimatedTonalToggleIconButton(
                                                    checked = saveState,
                                                    onCheckedChange = { checked ->
                                                        if (viewModel.isUserless) {
                                                            viewModel.encourageUserAuthSnackbar()
                                                        } else {
                                                            saveState = checked
                                                            val id =
                                                                post.post.id.removePrefix("t3_")
                                                            browseViewModel.updatePostFavorite(
                                                                checked,
                                                                id
                                                            )
                                                        }
                                                    },
                                                    checkedIcon = painterResource(R.drawable.ic_bookmark_filled),
                                                    uncheckedIcon = painterResource(R.drawable.ic_bookmark),
                                                    contentDescription = stringResource(R.string.ic_bookmark_cdesc)
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(end = 10.dp)
                                            ) {
                                                AnimatedTonalToggleIconButton(
                                                    checked = downvoteState,
                                                    onCheckedChange = { checked ->
                                                        if (viewModel.isUserless) {
                                                            viewModel.encourageUserAuthSnackbar()
                                                        } else {
                                                            downvoteState = checked
                                                            if (upvoteState) {
                                                                upvoteState = false
                                                            }
                                                            val id =
                                                                post.post.id.removePrefix("t3_")
                                                            val dir = if (checked) -1 else 0
                                                            viewModel.updateVote(dir, id)
                                                        }
                                                    },
                                                    checkedIcon = painterResource(R.drawable.ic_downvote),
                                                    uncheckedIcon = painterResource(R.drawable.ic_downvote),
                                                    contentDescription = stringResource(R.string.ic_downvote_cdesc),
                                                    modifier = Modifier.width(33.dp),
                                                    uncheckedRadius = 30.dp,
                                                    checkedRadius = MediumCornerRadius
                                                )
                                                post.post.ups?.toInt()?.prettyNumber()?.let {
                                                    Text(
                                                        text = it,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        modifier = Modifier.padding(horizontal = 10.dp)
                                                    )
                                                }
                                                AnimatedTonalToggleIconButton(
                                                    checked = upvoteState,
                                                    onCheckedChange = { checked ->
                                                        if (viewModel.isUserless) {
                                                            viewModel.encourageUserAuthSnackbar()
                                                        } else {
                                                            upvoteState = checked
                                                            if (downvoteState) {
                                                                downvoteState = false
                                                            }
                                                            val id =
                                                                post.post.id.removePrefix("t3_")
                                                            val dir = if (checked) 1 else 0
                                                            viewModel.updateVote(dir, id)
                                                        }
                                                    },
                                                    checkedIcon = painterResource(R.drawable.ic_upvote),
                                                    uncheckedIcon = painterResource(R.drawable.ic_upvote),
                                                    contentDescription = stringResource(R.string.ic_upvote_cdesc),
                                                    modifier = Modifier.width(33.dp),
                                                    uncheckedRadius = 30.dp,
                                                    checkedRadius = MediumCornerRadius
                                                )
                                            }
                                        }
                                    }
                                }

                                itemsIndexed(comments.itemSnapshotList) { _, commentWithUser ->
                                    val author = commentWithUser?.comment?.author
                                    LaunchedEffect(author) {
                                        viewModel.fetchUserOnVisible(author)
                                    }

                                    val depth = commentWithUser?.comment?.depth ?: 0
                                    val indentPerLevel = 10.dp
                                    val barWidth = 2.dp

                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        if (depth > 0) {
                                            Row(
                                                modifier = Modifier
                                                    .matchParentSize()
                                                    .padding(start = 15.dp)
                                            ) {
                                                for (i in 0 until depth) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .width(barWidth)
                                                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                                    )
                                                    Box(modifier = Modifier.width(indentPerLevel - barWidth))
                                                }
                                            }
                                        }

                                        CommentCard(
                                            commentWithUser = commentWithUser,
                                            onMoreClick = {
                                                viewModel.apply {
                                                    commentToShowMoreSheet = commentWithUser
                                                    showingCommentMoreSheet = true
                                                }
                                            },
                                            onUpvote = { upvoted, onSuccess ->
                                                if (viewModel.isUserless) {
                                                    viewModel.encourageUserAuthSnackbar()
                                                } else {
                                                    onSuccess()
                                                    commentWithUser?.comment?.id?.removePrefix("t1_")?.let {
                                                        viewModel.updateVote(
                                                            postId = it,
                                                            direction = if (upvoted) 1 else 0
                                                        )
                                                    }
                                                }
                                            },
                                            onDownvote = { downvoted, onSuccess ->
                                                if (viewModel.isUserless) {
                                                    viewModel.encourageUserAuthSnackbar()
                                                } else {
                                                    onSuccess()
                                                    commentWithUser?.comment?.id?.removePrefix("t1_")?.let {
                                                        viewModel.updateVote(
                                                            postId = it,
                                                            direction = if (downvoted) -1 else 0
                                                        )
                                                    }
                                                }
                                            },
                                            modifier = Modifier.padding(
                                                start = 15.dp + (depth * indentPerLevel.value).dp,
                                                end = 15.dp,
                                                top = 10.dp,
                                                bottom = 10.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.showingCommentMoreSheet) {
            viewModel.commentToShowMoreSheet?.let {
                CommentDetailSheet(
                    commentWithUser = it,
                    onDismissRequest = {
                        viewModel.showingCommentMoreSheet = false
                    },
                    onDownvote = { downvoted, onSuccess ->
                        if (viewModel.isUserless) {
                            viewModel.apply {
                                showingCommentMoreSheet = false
                                encourageUserAuthSnackbar()
                            }
                        } else {
                            onSuccess()
                            it.comment.id.let {
                                viewModel.updateVote(
                                    postId = it,
                                    direction = if (downvoted) -1 else 0
                                )
                            }
                        }
                    },
                    onUpvote = { upvoted, onSuccess ->
                        if (viewModel.isUserless) {
                            viewModel.apply {
                                showingCommentMoreSheet = false
                                encourageUserAuthSnackbar()
                            }
                        } else {
                            onSuccess()
                            it.comment.id.removePrefix("t3_").let {
                                viewModel.updateVote(
                                    postId = it,
                                    direction = if (upvoted) 1 else 0
                                )
                            }
                        }
                    },
                    onSaveClick = { saved, onSuccess ->
                        if (viewModel.isUserless) {
                            viewModel.apply {
                                showingCommentMoreSheet = false
                                encourageUserAuthSnackbar()
                            }
                        } else {
                            val id = it.comment.id.removePrefix("t3_")
                            browseViewModel.updatePostFavorite(saved, id)
                            onSuccess()
                        }
                    },
                    onViewUserClick = {
                        viewModel.showingCommentMoreSheet = false
                        navController.navigate("${NavDestinationKey.UserView}/${it.comment.author}")
                    }
                )
            }
        }

        if (viewModel.showingMoreSheet) {
            postWithUser.value?.let {
                PostOptionSheet(
                    postData = it.post.toPostData(),
                    onDismissRequest = { viewModel.showingMoreSheet = false },
                    onViewUser = {
                        navController.navigate("${NavDestinationKey.UserView}/${it.post.author}")
                    },
                    onViewCommunity = {
                        navController.navigate("${NavDestinationKey.CommunityView}/${it.post.subreddit}")
                    },
                    onOpenExternal = {
                        Intent(Intent.ACTION_VIEW).apply {
                            this.data = "https://www.reddit.com${it.post.permalink}".toUri()
                            navController.context.startActivity(this)
                        }
                    },
                    onReport = {
                        Intent(Intent.ACTION_VIEW).apply {
                            this.data = ("https://www.reddit.com/report"
                                    + "?url=https://www.reddit.com${it.post.permalink}").toUri()
                            navController.context.startActivity(this)
                        }
                    }
                )
            }
        }
    }
}