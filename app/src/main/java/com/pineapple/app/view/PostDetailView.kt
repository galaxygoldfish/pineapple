package com.pineapple.app.view

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.R
import com.pineapple.app.model.RequestResult
import com.pineapple.app.model.RequestStatus
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.util.getViewModel
import com.pineapple.app.viewmodel.PostDetailViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostDetailView(
    navController: NavController,
    subreddit: String,
    uid: String,
    link: String
) {
    val viewModel = LocalContext.current.getViewModel(PostDetailViewModel::class.java)
    rememberSystemUiController().setStatusBarColor(color = MaterialTheme.colorScheme.surfaceVariant)
    var postData by remember { mutableStateOf<RequestResult<PostData>?>(null) }
    viewModel.postData = Triple(subreddit, uid, link)
    LaunchedEffect(true) {
        viewModel.postRequestFlow().collect { result ->
            postData = result
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
        postData?.let { request ->
            when (request.status) {
                RequestStatus.LOADING -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
                RequestStatus.SUCCESS -> {
                    request.data?.let { post ->
                        Column {
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.displaySmall,
                                modifier = Modifier.padding(start = 17.dp, top = 25.dp, end = 18.dp)
                            )
                            Row(modifier = Modifier.padding(top = 15.dp, start = 17.dp)) {
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
                            Text(
                                text = post.selftext,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}