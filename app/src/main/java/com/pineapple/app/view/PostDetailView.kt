package com.pineapple.app.view

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.components.*
import com.pineapple.app.paging.RequestResult
import com.pineapple.app.paging.RequestStatus
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.model.reddit.PostListing
import com.pineapple.app.util.*
import com.pineapple.app.viewmodel.PostDetailViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Long.min
import java.net.URLEncoder

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
fun PostDetailView(
    navController: NavController,
    subreddit: String,
    uid: String,
    link: String
) {
    val context = LocalContext.current
    val viewModel = context.getViewModel(PostDetailViewModel::class.java)
    var postData by remember { mutableStateOf<PostData?>(null) }
    var commentData by remember { mutableStateOf<JSONArray?>(null) }
    val requestStatus = remember { mutableStateOf<RequestResult<Any>?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val postDetailContainerState = rememberLazyListState()

    viewModel.postData = Triple(subreddit, uid, link)
    rememberSystemUiController().setStatusBarColor(color = MaterialTheme.colorScheme.surfaceVariant)

    LaunchedEffect(true) {
        viewModel.postRequestFlow(context).collect { result ->
            requestStatus.value = result
            result.data?.apply {
                postData = Gson()
                    .fromJson(this[0].toString(), PostListing::class.java)
                    .data.children[0].data
                // Have to manually parse comment data because reddit decides to be "efficient" and
                // page comment results so there is no way of preventing Gson from throwing an exception
                // when reddit puts a paging object instead of a comment object 🙄🖐
                commentData = (this[1] as JSONObject)
                    .getJSONObject("data")
                    .getJSONArray("children")
            }
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            CommentReplyBottomSheet(viewModel, bottomSheetState)
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetState = bottomSheetState,
        scrimColor = Color.Black.copy(0.3F)
    ) {
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
            AnimatedVisibility(
                visible = requestStatus.value?.status == RequestStatus.LOADING,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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
            AnimatedVisibility(
                visible = requestStatus.value?.status == RequestStatus.SUCCESS,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding(),
                        start = it.calculateStartPadding(LayoutDirection.Ltr),
                        end = it.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = it.calculateBottomPadding()
                    ),
                    state = postDetailContainerState
                ) {
                    postData?.let { post ->
                        item {
                            HeaderBar(
                                post = post,
                                modifier = Modifier.animateEnterExit(enter = slideInVertically(
                                    animationSpec = spring(0.8F)
                                ) { it * 2 }
                                )
                            )
                        }
                        item {
                            FlairBar(
                                postData = post,
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .animateEnterExit(enter = slideInVertically(
                                        animationSpec = spring(0.8F)
                                    ) { it * 3 }
                                    )
                            )
                        }
                        item {
                            PostContentView(
                                post = post,
                                navController = navController,
                                viewModel = viewModel,
                                modifier = Modifier.animateEnterExit(enter = slideInVertically(
                                    animationSpec = spring(0.8F)
                                ) { it * 4 }
                                )
                            )
                        }
                    }
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            InteractionBar(postData = postData)
                            commentData?.let { commentDataList ->
                                CommentListView(
                                    commentData = commentDataList,
                                    viewModel = viewModel,
                                    bottomSheetState = bottomSheetState
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
fun HeaderBar(post: PostData, modifier: Modifier) {
    Column(modifier = modifier) {
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
            .padding(start = 15.dp, end = 15.dp, top = 20.dp)
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
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun CommentListView(
    commentData: JSONArray,
    viewModel: PostDetailViewModel,
    bottomSheetState: ModalBottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
    AnimatedVisibility(visible = commentData.length() > 0) {
        FlowColumn(modifier = Modifier.padding(top = 10.dp)) {
            repeat(commentData.length()) { index ->
                val item = commentData.getJSONObject(index)
                CommentBubble(
                    commentDataJson = item.getJSONObject("data"),
                    viewModel = viewModel,
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = spring(dampingRatio = 0.8F)
                        ) { it * (index + 6) }
                    ),
                    onExpandReplies = {
                        coroutineScope.launch {
                            viewModel.apply {
                                replyViewOriginalComment = item
                                replyViewCommentList = item.getJSONObject("data")
                                    .getJSONObject("replies")
                                    .getJSONObject("data")
                                    .getJSONArray("children")
                            }
                            bottomSheetState.show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostContentView(
    post: PostData,
    navController: NavController,
    viewModel: PostDetailViewModel,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        when {
            post.postHint == "link" -> {
                if (post.domain != "imgur.com") {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 15.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)),
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
                                    .data(
                                        post.preview?.images?.get(0)?.source?.url?.replace(
                                            "amp;",
                                            ""
                                        )
                                    )
                                    .crossfade(true)
                                    .fallback(R.drawable.ic_event_available)
                                    .build().data,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(65.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)),
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
                        top = 20.dp
                    )
                )
            }
            else -> {
                val mediaLink = when (post.postHint) {
                    "hosted:video" -> post.secureMedia!!.reddit_video.fallback_url.replace(
                        "amp;",
                        ""
                    )
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
                                start = 20.dp, end = 20.dp, top = 20.dp
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
                                            ratioHeight = post.secureMedia?.reddit_video?.height?.toInt()
                                                ?: 0,
                                            ratioWidth = post.secureMedia?.reddit_video?.width?.toInt()
                                                ?: 0,
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
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
fun CommentReplyBottomSheet(
    viewModel: PostDetailViewModel,
    bottomSheetState: ModalBottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
    val commentReplyScrollState = rememberScrollState()
    val headerBackground = animateColorAsState(
        if (bottomSheetState.isVisible) {
            if (commentReplyScrollState.value == 0) {
                MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
            } else {
                MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)
            }
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    )
    rememberSystemUiController().setStatusBarColor(headerBackground.value)
    Column {
        Column(
            modifier = Modifier.background(headerBackground.value)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.post_view_comment_reply_sheet_title),
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch { bottomSheetState.hide() }
                    },
                    modifier = Modifier.padding(end = 15.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.ic_close_content_desc),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            viewModel.replyViewOriginalComment?.let {
                CommentBubble(
                    commentDataJson = it.getJSONObject("data"),
                    viewModel = viewModel,
                    modifier = Modifier.padding(vertical = 15.dp),
                    allowExpandReplies = false,
                    specialComment = true
                )
            }
        }
        AnimatedVisibility(visible = viewModel.replyViewCommentList != null) {
            FlowColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(commentReplyScrollState)
            ) {
                viewModel.replyViewCommentList?.let { array ->
                    repeat(array.length()) { index ->
                        val item = array.getJSONObject(index)
                        CommentBubble(
                            commentDataJson = item.getJSONObject("data"),
                            viewModel = viewModel,
                            modifier = Modifier
                                .animateEnterExit(
                                    enter = slideInVertically(
                                        animationSpec = spring(dampingRatio = 0.8F)
                                    ) { it * (index + 6) }
                                )
                                .padding(bottom = if (index == array.length() - 1) 15.dp else 0.dp),
                            onExpandReplies = {
                                viewModel.apply {
                                    replyViewOriginalComment = item
                                    replyViewCommentList = item.getJSONObject("data")
                                        .getJSONObject("replies")
                                        .getJSONObject("data")
                                        .getJSONArray("children")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}