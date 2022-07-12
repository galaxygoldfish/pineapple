package com.pineapple.app.view

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Log
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.DefaultTranslationY
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ireward.htmlcompose.HtmlText
import com.pineapple.app.R
import com.pineapple.app.components.FilterBottomSheet
import com.pineapple.app.components.RoundedStarShape
import com.pineapple.app.model.reddit.SubredditData
import com.pineapple.app.model.reddit.SubredditInfo
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.prettyNumber
import com.pineapple.app.util.surfaceColorAtElevation
import com.pineapple.app.viewmodel.SubredditViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun SubredditView(navController: NavController, subreddit: String) {
    val viewModel: SubredditViewModel = viewModel()
    var subredditInfo by remember { mutableStateOf<SubredditData?>(null) }
    var currentBottomSheet by remember { mutableStateOf(0) }
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
       rememberSplineBasedDecay()
    )
    viewModel.currentSubreddit = subreddit
    LaunchedEffect(true) {
        viewModel.fetchInformation().collect {
            subredditInfo = it.data
        }
    }
    PineappleTheme {
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                when(currentBottomSheet) {
                    0 -> SubredditInfoBottomSheet(subredditInfo)
                    1 -> FilterBottomSheet(
                        timePeriod = viewModel.currentSortTime,
                        sortType = viewModel.currentSortType,
                        bottomSheetState = bottomSheetState
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = subredditInfo?.title.toString()
                                )
                                Text(
                                    text = String.format(
                                        stringResource(id = R.string.community_user_count_format),
                                        subredditInfo?.subscribers?.toInt()?.prettyNumber() ?: "0"
                                    )
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_back),
                                    contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc)
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        currentBottomSheet = 1
                                        bottomSheetState.show()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_discover_tune),
                                    contentDescription = stringResource(id = R.string.ic_discover_tune_content_desc)
                                )
                            }
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        currentBottomSheet = 0
                                        bottomSheetState.show()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_info),
                                    contentDescription = stringResource(id = R.string.ic_info_content_desc)
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding(),
                        start = it.calculateStartPadding(LayoutDirection.Ltr),
                        end = it.calculateEndPadding(LayoutDirection.Ltr)
                    )
                ) {
                    PostListView(
                        navController = navController,
                        subreddit = subreddit,
                        sort = viewModel.currentSortType.value,
                        time = viewModel.currentSortTime.value
                    )
                }
            }
        }
    }
}

@Composable
fun SubredditInfoBottomSheet(subredditInfo: SubredditData?) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .clip(RoundedCornerShape(5.dp))
                .size(width = 100.dp, height = 5.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) { }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(subredditInfo?.iconUrl)
                .crossfade(true)
                .build().data,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedStarShape(sides = 9))
                .size(100.dp)
                .padding(top = 15.dp)
        )
        Text(
            text = subredditInfo?.title.toString(),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = String.format(
                stringResource(id = R.string.community_user_count_format),
                subredditInfo?.subscribers?.toInt()?.prettyNumber() ?: "0"
            ),
            style = MaterialTheme.typography.titleMedium
        )
        HtmlText(
            text = subredditInfo?.descriptionHtml.toString(),
            style = MaterialTheme.typography.bodyMedium,
            linkClicked = { link ->
                context.startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(link)
                    }
                )
            }
        )
    }
}