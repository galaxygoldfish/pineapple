package com.pineapple.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pineapple.app.R
import com.pineapple.app.components.SubredditListCard
import com.pineapple.app.components.TextOnlyTextField
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.util.getViewModel
import com.pineapple.app.viewmodel.SearchViewModel

@Composable
fun SearchView() {
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
            Text(
                text = stringResource(id = R.string.home_bottom_bar_item_search),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 30.dp, start = 21.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.7F)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_glyph),
                    contentDescription = stringResource(id = R.string.ic_search_glyph_content_desc),
                    modifier = Modifier.padding(15.dp)
                )
                TextOnlyTextField(
                    textFieldValue = viewModel.currentSearchQuery,
                    hint = stringResource(id = R.string.search_query_hint_text),
                    onValueChange = {
                        viewModel.currentSearchQuery = it
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trending_indicator),
                    contentDescription = stringResource(id = R.string.ic_trending_indicator_content_desc),
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
                    SubredditListCard(item = item)
                }
            }
        }
    }
}