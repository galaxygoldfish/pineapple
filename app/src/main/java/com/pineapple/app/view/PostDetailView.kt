package com.pineapple.app.view

import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
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
    var postData by remember { mutableStateOf<RequestResult<PostData>?>(null) }
    viewModel.postData = Triple(subreddit, uid, link)
    LaunchedEffect(true) {
        viewModel.postRequestFlow().collect { result ->
            postData = result
        }
    }
    Scaffold(
        topBar = {

        }
    ) {
        postData?.let { request ->
            when (request.status) {
                RequestStatus.LOADING -> {
                    Text("Loading")
                }
                RequestStatus.SUCCESS -> {
                    request.data?.let { post ->
                        Log.e("Tag", post.author + post.permalink)
                    }
                }
            }
        }
    }
}