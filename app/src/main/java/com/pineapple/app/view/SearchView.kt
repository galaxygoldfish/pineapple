package com.pineapple.app.view

import android.util.Log
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat
import androidx.navigation.NavController
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.components.Chip
import com.pineapple.app.components.SubredditListCard
import com.pineapple.app.components.TextOnlyTextField
import com.pineapple.app.components.TextPostCard
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.keyboardIsVisible
import com.pineapple.app.viewmodel.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun SearchView(navController: NavController) {
    val viewModel = LocalContext.current.getViewModel(SearchViewModel::class.java)
    val topSubredditList = remember { mutableStateListOf<SubredditItem>() }
    LaunchedEffect(true) {
        viewModel.requestSubredditFlow().collect {
            it.data?.let { data ->
                topSubredditList.apply {
                    clear()
                    addAll(data)
                }
            }
        }
    }
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
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                                if (abs(lastUpdateSearch - System.currentTimeMillis()) > 1000L) {
                                    updateSearchResults()
                                    lastUpdateSearch = System.currentTimeMillis()
                                }
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
            AnimatedVisibility(visible = viewModel.currentSearchQuery.text.isNotEmpty()) {
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
                        LazyColumn(
                            modifier = Modifier.padding(top = 15.dp)
                        ) {

                        }
                    }
                    // Posts
                    AnimatedVisibility(visible = viewModel.currentSearchFilter == 1) {
                        LazyColumn(
                            modifier = Modifier.padding(top = 15.dp)
                        ) {
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

                    }
                    // Users
                    AnimatedVisibility(visible = viewModel.currentSearchFilter == 3) {

                    }
                }
            }
            AnimatedVisibility(visible = viewModel.currentSearchQuery.text.isEmpty()) {
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
                    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                        itemsIndexed(topSubredditList) { _, item ->
                            SubredditListCard(
                                item = item,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}