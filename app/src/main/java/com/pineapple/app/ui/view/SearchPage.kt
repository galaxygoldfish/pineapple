@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.pineapple.app.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.pineapple.app.R
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.ui.components.TonalActionSectionItem
import com.pineapple.app.ui.components.TonalActionSectionList
import com.pineapple.app.ui.components.PostCard
import com.pineapple.app.ui.theme.PineappleTheme
import com.pineapple.app.ui.viewmodel.SearchViewModel
import com.pineapple.app.utilities.toPostData
import com.pineapple.app.utilities.toUserAboutListing
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun SearchPage(navController: NavController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val context = LocalContext.current
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val subredditSuggestions by viewModel.subredditSuggestions.collectAsState()
    val userSuggestions by viewModel.userSuggestions.collectAsState()
    val motionScheme = MaterialTheme.motionScheme
    val searchFieldPadding by animateDpAsState(
        targetValue = if (viewModel.expandedSearchField) 0.dp else 15.dp,
        animationSpec = motionScheme.fastSpatialSpec(),
        label = "search_padding"
    )

    PineappleTheme {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = searchFieldPadding.coerceAtLeast(0.dp),
                        start = searchFieldPadding.coerceAtLeast(0.dp),
                        end = searchFieldPadding.coerceAtLeast(0.dp)
                    )
            ) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = viewModel.searchFieldValue,
                            // when the user types and clears the field, clear the active search results
                            onQueryChange = { newText ->
                                viewModel.updateQueryText(newText)
                                if (newText.isBlank()) {
                                    viewModel.clearSearchQuery()
                                }
                            },
                            onSearch = {
                                viewModel.submitSearch()
                            },
                            expanded = viewModel.expandedSearchField,
                            onExpandedChange = { viewModel.expandedSearchField = it },
                            placeholder = {
                                Text(stringResource(R.string.search_placeholder))
                            },
                            leadingIcon = {
                                AnimatedContent(viewModel.expandedSearchField) { expanded ->
                                    if (expanded) {
                                        IconButton(
                                            onClick = {
                                               viewModel.expandedSearchField = false
                                                viewModel.updateQueryText("")
                                                viewModel.clearSearchQuery()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_back),
                                                contentDescription = stringResource(R.string.ic_back_cdesc)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_search),
                                            contentDescription = stringResource(R.string.ic_search_cdesc)
                                        )
                                    }
                                }
                            },
                            trailingIcon = {
                                AnimatedContent(
                                    targetState = viewModel.searchFieldValue.isNotEmpty(),
                                    transitionSpec = {
                                        scaleIn(motionScheme.fastSpatialSpec())
                                            .togetherWith(scaleOut(motionScheme.fastSpatialSpec()))
                                    }
                                ) { show ->
                                    if (show) {
                                        IconButton(
                                            onClick = {
                                                viewModel.updateQueryText("")
                                                viewModel.clearSearchQuery()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_close),
                                                contentDescription = stringResource(R.string.ic_close_cdesc)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    expanded = viewModel.expandedSearchField,
                    onExpandedChange = { viewModel.expandedSearchField = it },
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        AnimatedContent(
                            targetState = subredditSuggestions.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        ) { suggestions ->
                            if (suggestions) {
                                Column {
                                    val items = subredditSuggestions.map { sd ->
                                        val name = sd.displayName
                                        val iconUrl = sd.iconUrl

                                        val painter: Painter =
                                            if (iconUrl.isBlank() || iconUrl.contains(
                                                    "default",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                painterResource(R.drawable.generic_community)
                                            } else {
                                                rememberAsyncImagePainter(
                                                    ImageRequest.Builder(context)
                                                        .data(iconUrl)
                                                        .diskCachePolicy(CachePolicy.ENABLED)
                                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                                        .build()
                                                )
                                            }

                                        TonalActionSectionItem(
                                            text = "r/$name",
                                            icon = painter,
                                            contentDescription = "Subreddit $name",
                                            onCLick = {
                                                viewModel.updateQueryText(name)
                                                viewModel.submitSearch()
                                            },
                                            shouldTintIcon = false
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.search_communities),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(
                                            top = 15.dp,
                                            start = 15.dp,
                                            bottom = 10.dp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TonalActionSectionList(
                                        items = items,
                                        modifier = Modifier.padding(horizontal = 15.dp),
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        listItemContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                }
                            }
                        }

                        AnimatedContent(
                            targetState = userSuggestions.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        ) { suggestions ->
                            if (suggestions) {
                                Column {
                                    val items = userSuggestions.map { ua ->
                                        val name = ua.name ?: ""
                                        val iconUrl = ua.icon_img ?: ua.snoovatar_img
                                        val isDefaultIcon = ua.subreddit?.is_default_icon ?: false

                                        val painter = if (isDefaultIcon || iconUrl.isNullOrBlank()) {
                                            painterResource(R.drawable.generic_avatar)
                                        } else {
                                            rememberAsyncImagePainter(
                                                ImageRequest.Builder(context)
                                                    .data(iconUrl)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                                    .build()
                                            )
                                        }

                                        TonalActionSectionItem(
                                            text = "u/$name",
                                            icon = painter,
                                            contentDescription = "User $name",
                                            onCLick = {
                                                viewModel.updateQueryText(name)
                                                viewModel.submitSearch()
                                            },
                                            shouldTintIcon = false
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.search_users),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(
                                            top = 15.dp,
                                            start = 15.dp,
                                            bottom = 10.dp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TonalActionSectionList(
                                        items = items,
                                        modifier = Modifier.padding(horizontal = 15.dp),
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        listItemContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                }
                            }
                        }
                    }
                )
            }

            AnimatedContent(searchResults) { result ->
                if (result.loadState.refresh is LoadState.Loading
                    && viewModel.searchFieldValue.isNotEmpty()
                ) {
                    Box(Modifier.fillMaxSize()) {
                        LoadingIndicator(Modifier.size(75.dp).align(Alignment.Center))
                    }
                } else if (result.itemCount != 0) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        state = viewModel.postListState
                    ) {
                        itemsIndexed(result.itemSnapshotList.items) { index, item ->
                            PostCard(
                                postData = item.post.toPostData(),
                                modifier = Modifier.padding(top = 10.dp),
                                userInfo = item.user?.toUserAboutListing(),
                                onClick = {
                                    // pass id without the t3_ prefix (PostView expects raw id)
                                    val rawId = item.post.id.removePrefix("t3_")
                                    navController.navigate("${NavDestinationKey.PostView}/$rawId")
                                },
                                onMoreClick = { },
                                onSaveClick = { _, _ -> },
                                onUpvote = { _, _ -> },
                                onDownvote = { _, _ -> },
                            )
                        }
                    }
                }
            }
        }
    }
}