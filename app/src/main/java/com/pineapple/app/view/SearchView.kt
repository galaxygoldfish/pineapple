package com.pineapple.app.view

import android.text.method.TextKeyListener.clear
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.common.collect.Multimaps.index
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.components.Chip
import com.pineapple.app.components.SmallListCard
import com.pineapple.app.components.TextOnlyTextField
import com.pineapple.app.components.TextPostCard
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.isLoading
import com.pineapple.app.viewmodel.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SearchView(navController: NavController) {
    val viewModel = LocalContext.current.getViewModel(SearchViewModel::class.java)
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            AnimatedVisibility(visible = viewModel.currentSearchQuery.text.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.home_bottom_bar_item_search),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 30.dp, start = 21.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.7F)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = stringResource(id = R.string.ic_search_content_desc),
                        modifier = Modifier.padding(15.dp)
                    )
                    TextOnlyTextField(
                        textFieldValue = viewModel.currentSearchQuery,
                        hint = stringResource(id = R.string.search_query_hint_text),
                        onValueChange = {
                            viewModel.apply {
                                currentSearchQuery = it
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (it.text.length > 2) updateSearchResults()
                                }
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
                if (viewModel.currentSearchQuery.text.length >= 2) {
                    IconButton(
                        onClick = { viewModel.currentSearchQuery = TextFieldValue() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = stringResource(id = R.string.ic_close_content_desc)
                        )
                    }
                }
            }
            AnimatedVisibility(visible = viewModel.currentSearchQuery.text.length >= 2) {
                val categories = listOf(
                    R.string.search_category_all,
                    R.string.search_category_post,
                    R.string.search_category_community,
                    R.string.search_category_users
                )
                val icons = listOf(
                    R.drawable.ic_auto_awesome, R.drawable.ic_article,
                    R.drawable.ic_atr_dots, R.drawable.ic_group
                )
                Column {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        itemsIndexed(categories) { index, item ->
                            Chip(
                                text = stringResource(id = item),
                                icon = painterResource(id = icons[index]),
                                selected = viewModel.currentSearchFilter == index,
                                onClick = { viewModel.currentSearchFilter = index }
                            )
                        }
                    }
                    // All
                    AnimatedVisibility(visible = viewModel.currentSearchFilter == 0) {
                        LaunchedEffect(true) {
                            launch { viewModel.updateSearchResults() }
                        }
                        AllResultSearchView(navController, viewModel)
                    }
                    // Posts
                    AnimatedVisibility(visible = viewModel.currentSearchFilter == 1) {
                        LaunchedEffect(true) {
                            launch { viewModel.updateSearchResults() }
                        }
                        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
                            itemsIndexed(viewModel.currentPostList) { _, item ->
                                TextPostCard(
                                    postData = item.data,
                                    navController = navController,
                                    onClick = {
                                        val permalink = item.data.permalink.split("/")
                                        val sub = permalink[2]
                                        val uid = permalink[4]
                                        val link = permalink[5]
                                        navController.navigate("${NavDestination.PostDetailView}/$sub/$uid/$link")
                                    }
                                )
                            }
                        }
                    }
                    // Communities
                    AnimatedVisibility(visible = viewModel.currentSearchFilter == 2) {
                        LaunchedEffect(true) {
                            launch { viewModel.updateSearchResults() }
                        }
                        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
                            itemsIndexed(viewModel.currentSubredditList) { _, item ->
                                SmallListCard(
                                    text = item.data.displayNamePrefixed,
                                    iconUrl = item.data.iconUrl.replace(";", "").replace("amp", ""),
                                    onClick = {
                                        navController.navigate("${NavDestination.SubredditView}/${
                                            item.data.url.replace("r/", "").replace("/", "")
                                        }")
                                    }
                                )
                            }
                        }
                    }
                    // Users
                    AnimatedVisibility(visible = viewModel.currentSearchFilter == 3) {
                        LaunchedEffect(true) {
                            launch { viewModel.updateSearchResults() }
                        }
                        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
                            itemsIndexed(viewModel.currentUserList) { _, item ->
                                SmallListCard(
                                    text = item.data.name ?: "",
                                    iconUrl = item.data.snoovatar_img ?: item.data.icon_img ?: "",
                                    userIcon = true,
                                    onClick = {  }
                                )
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = viewModel.currentSearchQuery.text.isEmpty()) {
                PopularContentView(viewModel, navController)
            }
        }
    }
}

@Composable
fun StickyHeaderStyle(text: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, top = 15.dp, bottom = 10.dp)
        )
    }
}

@Composable
fun SearchChangeButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.padding(start = 15.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 10.dp)
        )
        Text(text = text)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
fun AllResultSearchView(
    navController: NavController,
    viewModel: SearchViewModel
) {
    fun checkSearchStatus() = viewModel.currentPostList.isNotEmpty()
            && viewModel.currentUserList.isNotEmpty()
            && viewModel.currentSubredditList.isNotEmpty()
    if (!checkSearchStatus()) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center).size(50.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
    AnimatedVisibility(
        visible = checkSearchStatus(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
            stickyHeader {
                StickyHeaderStyle(
                    text = stringResource(id = R.string.search_category_community),
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(animationSpec = spring(0.8F)) { it }
                    )
                )
            }
            repeat((viewModel.currentSubredditList.size - 1).coerceAtMost(3)) { index ->
                viewModel.currentSubredditList[index].let { item ->
                    item {
                        SmallListCard(
                            text = item.data.displayNamePrefixed,
                            iconUrl = item.data.iconUrl.replace(";", "")
                                .replace("amp", ""),
                            onClick = {
                                navController.navigate(
                                    "${NavDestination.SubredditView}/${
                                        item.data.url.replace("r/", "")
                                            .replace("/", "")
                                    }"
                                )
                            },
                            modifier = Modifier.animateEnterExit(
                                enter = slideInVertically(animationSpec = spring(0.8F)) { it * (index + 1) }
                            )
                        )
                    }
                }
            }
            item {
                SearchChangeButton(
                    text = stringResource(id = R.string.search_view_all_communities),
                    icon = painterResource(id = R.drawable.ic_atr_dots),
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(animationSpec = spring(0.8F)) { it * 5 }
                    ),
                    onClick = { viewModel.currentSearchFilter = 2 }
                )
            }
            stickyHeader {
                StickyHeaderStyle(
                    text = stringResource(id = R.string.search_category_users),
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(animationSpec = spring(0.8F)) { it * 6 }
                    ),
                )
            }
            repeat((viewModel.currentUserList.size - 1).coerceAtMost(3)) { index ->
                viewModel.currentUserList[index].let { item ->
                    item {
                        SmallListCard(
                            text = item.data.name ?: "",
                            iconUrl = item.data.snoovatar_img ?: item.data.icon_img
                            ?: "",
                            onClick = { },
                            userIcon = true,
                            modifier = Modifier.animateEnterExit(
                                enter = slideInVertically(animationSpec = spring(0.8F)) { it * (index + 7) }
                            ),
                        )
                    }
                }
            }
            item {
                SearchChangeButton(
                    text = stringResource(id = R.string.search_view_all_users),
                    icon = painterResource(id = R.drawable.ic_group),
                    onClick = { viewModel.currentSearchFilter = 3 },
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(animationSpec = spring(0.8F)) { it * 10 }
                    )
                )
            }
            stickyHeader {
                StickyHeaderStyle(
                    text = stringResource(id = R.string.search_category_post),
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(animationSpec = spring(0.8F)) { it * 11 }
                    )
                )
            }
            repeat((viewModel.currentPostList.size - 1).coerceAtMost(3)) { index ->
                viewModel.currentPostList[index].let { item ->
                    item {
                        TextPostCard(
                            postData = item.data,
                            navController = navController,
                            onClick = {
                                val permalink = item.data.permalink.split("/")
                                val sub = permalink[2]
                                val uid = permalink[4]
                                val link = permalink[5]
                                navController.navigate("${NavDestination.PostDetailView}/$sub/$uid/$link")
                            },
                            modifier = Modifier.animateEnterExit(
                                enter = slideInVertically(animationSpec = spring(0.8F)) { it * (index + 10) }
                            )
                        )
                    }
                }
            }
            item {
                SearchChangeButton(
                    text = stringResource(id = R.string.search_view_all_posts),
                    icon = painterResource(id = R.drawable.ic_article),
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(animationSpec = spring(0.8F)) { it * 13 }
                    ),
                    onClick = { viewModel.currentSearchFilter = 1 }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PopularContentView(
    viewModel: SearchViewModel,
    navController: NavController
) {
    var refreshingSubredditData by remember { mutableStateOf(false) }
    LaunchedEffect(refreshingSubredditData) {
        viewModel.requestSubreddits()
    }
    Column {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.ic_trending_up),
                contentDescription = stringResource(id = R.string.ic_trending_up_content_desc),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(start = 21.dp, top = 5.dp)
            )
            Text(
                text = stringResource(id = R.string.search_trending_subreddit_header),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 10.dp, top = 5.dp)
            )
        }
        if (viewModel.topSubredditList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = viewModel.topSubredditList.isEmpty()),
            onRefresh = {
                viewModel.topSubredditList.clear()
                refreshingSubredditData = !refreshingSubredditData
            }
        ) {
            AnimatedVisibility(
                visible = viewModel.topSubredditList.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                    itemsIndexed(viewModel.topSubredditList) { index, item ->
                        SmallListCard(
                            text = item.data.displayNamePrefixed,
                            iconUrl = item.data.iconUrl.replace(";", "").replace("amp", ""),
                            onClick = {
                                navController.navigate(
                                    "${NavDestination.SubredditView}/${
                                        item.data.url.replace("r/", "").replace("/", "")
                                    }"
                                )
                            },
                            modifier = Modifier.animateEnterExit(
                                enter = slideInVertically(animationSpec = spring(0.5F)) { it * (index + 1) }
                            )
                        )
                    }
                }
            }
        }
    }
}