package com.pineapple.app.view

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.SpannableString
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.components.*
import com.pineapple.app.model.gfycat.GfycatObject
import com.pineapple.app.paging.RequestResult
import com.pineapple.app.paging.RequestStatus
import com.pineapple.app.model.reddit.*
import com.pineapple.app.util.calculateRatioHeight
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.prettyNumber
import com.pineapple.app.viewmodel.PostDetailViewModel
import okhttp3.internal.notify
import java.lang.Long.min
import java.net.URLEncoder

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostDetailView(
    navController: NavController,
    subreddit: String,
    uid: String,
    link: String
) {
    val viewModel = LocalContext.current.getViewModel(PostDetailViewModel::class.java)
    var postData by remember { mutableStateOf<PostData?>(null) }
    var commentData by remember { mutableStateOf<List<CommentPreData>?>(null) }
    val requestStatus = remember { mutableStateOf<RequestResult<Any>?>(null) }

    viewModel.postData = Triple(subreddit, uid, link)
    rememberSystemUiController().setStatusBarColor(color = MaterialTheme.colorScheme.surfaceVariant)

    LaunchedEffect(true) {
        viewModel.postRequestFlow().collect { result ->
            requestStatus.value = result
            result.data?.apply {
                postData = Gson()
                    .fromJson(this[0].toString(), PostListing::class.java)
                    .data.children[0].data
                commentData = Gson()
                    .fromJson(this[1].toString(), CommentListing::class.java)
                    .data.children
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            when (requestStatus.value?.status) {
                RequestStatus.LOADING -> {
                    item {
                        val localConfig = LocalConfiguration.current
                        Box(
                            modifier = Modifier
                                .size(
                                    height = localConfig.screenHeightDp.dp,
                                    width = localConfig.screenWidthDp.dp
                                )
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(50.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                RequestStatus.SUCCESS -> {
                    postData?.let { post ->
                        item {
                            HeaderBar(post = post)
                        }
                        item {
                            FlairBar(
                                postData = post,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                        item {
                            PostContentView(
                                post = post,
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            InteractionBar(postData = postData)
                            commentData?.let { commentDataList ->
                                CommentListView(
                                    commentData = commentDataList,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderBar(post: PostData) {
    Column {
        Text(
            text = post.title,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    top = 25.dp,
                    end = 18.dp
                )
        )
        Row(modifier = Modifier.padding(top = 15.dp, start = 21.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .size(35.dp)
                    .padding(top = 5.dp)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = post.author,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "r/${post.subreddit}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionBar(postData: PostData?) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(end = 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(id = R.string.ic_share_content_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledTonalIconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bookmark),
                    contentDescription = stringResource(id = R.string.ic_bookmark_content_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 10.dp)
        ) {
            FilledTonalIconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thumb_up),
                    contentDescription = stringResource(id = R.string.ic_thumb_up_content_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = min(postData?.ups ?: 0L, Integer.MAX_VALUE.toLong())
                    .toInt()
                    .prettyNumber(),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(5.dp)
            )
            FilledTonalIconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thumb_down),
                    contentDescription = stringResource(id = R.string.ic_thumb_down_content_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CommentListView(commentData: List<CommentPreData>, viewModel: PostDetailViewModel) {
    FlowColumn(modifier = Modifier.padding(top = 10.dp)) {
        commentData.forEach { item ->
            CommentBubble(
                commentData = item.data,
                viewModel = viewModel
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostContentView(
    post: PostData,
    navController: NavController,
    viewModel: PostDetailViewModel
) {
    when {
        post.postHint == "link" -> {
            if (post.domain != "imgur.com") {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 15.dp),
                    onClick = {
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(post.url)
                            navController.context.startActivity(this)
                        }
                    }
                ) {
                    Row {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.preview?.images?.get(0)?.source?.url?.replace("amp;", ""))
                                .crossfade(true)
                                .fallback(R.drawable.ic_event_available)
                                .build().data,
                            contentDescription = null,
                            modifier = Modifier
                                .width(100.dp)
                                .height(65.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.FillHeight,
                        )
                        Column(
                            modifier = Modifier.padding(
                                vertical = 10.dp,
                                horizontal = 15.dp
                            )
                        ) {
                            Text(
                                text = post.domain,
                                style = MaterialTheme.typography.headlineSmall,
                                textDecoration = TextDecoration.Underline
                            )
                            Text(
                                text = stringResource(id = R.string.post_view_link_proceed_text),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
        post.selftext.isNotEmpty() -> {
            Text(
                text = post.selftext,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    start = 22.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp
                )
            )
        }
        else -> {
            val mediaLink = when (post.postHint) {
                "hosted:video" -> post.secureMedia!!.reddit_video.fallback_url.replace("amp;", "")
                "rich:video" -> post.url
                else -> {
                    post.preview?.images?.get(0)?.source?.url?.replace("amp;", "")
                        ?.ifEmpty { post.url }
                }
            }
            mediaLink?.let {
                MultiTypeMediaView(
                    mediaHint = post.postHint,
                    url = it,
                    richDomain = post.domain,
                    gfycatService = viewModel.gfycatService,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 20.dp,
                            horizontal = 20.dp
                        )
                        .height(
                            when (post.postHint) {
                                "image", "rich_video" -> {
                                    LocalContext.current.calculateRatioHeight(
                                        ratioWidth = post.thumbnailWidth.toInt(),
                                        ratioHeight = post.thumbnailHeight.toInt(),
                                        actualWidth = LocalConfiguration.current.screenWidthDp - 40
                                    )
                                }
                                else -> {
                                    LocalContext.current.calculateRatioHeight(
                                        ratioHeight = post.secureMedia?.reddit_video?.height?.toInt() ?: 0,
                                        ratioWidth = post.secureMedia?.reddit_video?.width?.toInt() ?: 0,
                                        actualWidth = LocalConfiguration.current.screenWidthDp - 40
                                    )
                                }
                            }
                        )
                        .clip(RoundedCornerShape(10.dp)),
                    playerControls = { player ->
                        VideoControls(
                            player = player,
                            onExpand = {
                                navController.navigate(
                                    "${NavDestination.MediaDetailView}/${post.postHint}/${
                                        URLEncoder.encode(mediaLink)
                                    }/${post.domain}/${URLEncoder.encode(post.title)}"
                                )
                            },
                            postTitle = post.title
                        )
                    },
                    expandToFullscreen = {
                        navController.navigate(
                            "${NavDestination.MediaDetailView}/${post.postHint}/${
                                URLEncoder.encode(mediaLink)
                            }/${post.domain}/${URLEncoder.encode(post.title)}"
                        )
                    }
                )
            }
        }
    }
}