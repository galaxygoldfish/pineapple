package com.pineapple.app.view

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.text.Html
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.pineapple.app.R
import com.pineapple.app.components.CommentBubble
import com.pineapple.app.components.FlairBar
import com.pineapple.app.model.RequestResult
import com.pineapple.app.model.RequestStatus
import com.pineapple.app.model.reddit.*
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.prettyNumber
import com.pineapple.app.viewmodel.PostDetailViewModel
import org.intellij.lang.annotations.JdkConstants
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Comment
import java.lang.Long.min

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
            requestStatus.let { request ->
                when (request.value?.status) {
                    RequestStatus.LOADING -> {
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    RequestStatus.SUCCESS -> {
                        postData?.let { post ->
                            item {
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
                                    FlairBar(
                                        postData = post,
                                        modifier = Modifier.padding(start = 20.dp)
                                    )
                                    when {
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
                                        post.urlOverriddenByDest.let {
                                            it.isNotEmpty() && !it.contains("png") &&
                                                    !it.contains("jpg") && !it.contains("v.redd.it")
                                        } -> {
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
                                        post.url.isNotEmpty() -> {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(post.url)
                                                    .crossfade(true)
                                                    .build().data,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 20.dp, vertical = 20.dp)
                                                    .clip(RoundedCornerShape(10.dp)),
                                                contentScale = ContentScale.FillWidth,
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(start = 20.dp, end = 18.dp)
                                            .border(
                                                width = 2.dp,
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    ) {
                                        Row {
                                            Text(
                                                text = String.format(
                                                    stringResource(id = R.string.post_view_comments_overview_format),
                                                    post.numComments
                                                        .toInt()
                                                        .prettyNumber()
                                                ),
                                                style = MaterialTheme.typography.titleSmall,
                                                modifier = Modifier.padding(start = 12.dp)
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(end = 10.dp)
                                        ) {
                                            IconButton(onClick = { /*TODO*/ }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_thumb_up),
                                                    contentDescription = stringResource(id = R.string.ic_thumb_up_content_desc),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                            Text(
                                                text = min(post.ups, Integer.MAX_VALUE.toLong())
                                                    .toInt()
                                                    .prettyNumber(),
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            IconButton(onClick = { /*TODO*/ }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_thumb_down),
                                                    contentDescription = stringResource(id = R.string.ic_thumb_down_content_desc),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                        }
                                    }
                                    commentData?.let { comments ->
                                        FlowColumn(modifier = Modifier.padding(top = 10.dp)) {
                                            comments.forEach { item ->
                                                CommentBubble(
                                                    commentData = item.data,
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
            }
        }
    }
}