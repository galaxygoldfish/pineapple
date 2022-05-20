package com.pineapple.app.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.pineapple.app.model.reddit.SubredditData
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.getViewModel
import com.pineapple.app.viewmodel.SubredditViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SubredditView(navController: NavController, subreddit: String) {
    val viewModel = navController.context.getViewModel(SubredditViewModel::class.java)
    var subredditInfo by remember { mutableStateOf<SubredditData?>(null) }
    viewModel.currentSubreddit = subreddit
    LaunchedEffect(true) {
        viewModel.fetchInformation().collect {
            subredditInfo = it.data
        }
    }
    PineappleTheme {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(subredditInfo?.title.toString())
                    }
                )
            }
        ) {
            it.calculateBottomPadding()
        }
    }
}

