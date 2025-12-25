@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.pineapple.app.ui.view

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pineapple.app.R
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.consts.PageDestinationKey
import com.pineapple.app.ui.modal.PostOptionSheet
import com.pineapple.app.ui.modal.SortPostSheet
import com.pineapple.app.ui.theme.PineappleTheme
import com.pineapple.app.ui.viewmodel.BrowseViewModel
import com.pineapple.app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeView(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val browseViewModel: BrowseViewModel = hiltViewModel()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val pagingItems = browseViewModel.pagedPosts.collectAsLazyPagingItems()
    val topSubreddits = viewModel.topSubreddits.collectAsState(initial = emptyList())
    val subscribedSubreddits = viewModel.subscribedSubreddits.collectAsState(initial = emptyList())

    PineappleTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.7F)) {
                   Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                       Icon(
                           painter = painterResource(R.drawable.ic_pineapple_logo),
                           contentDescription = null,
                           tint = MaterialTheme.colorScheme.primary,
                           modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                               .height(100.dp)
                       )
                       Column(
                           modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 30.dp)
                       ) {
                           NavigationDrawerItem(
                               label = {
                                   Text(stringResource(R.string.home_nav_home))
                               },
                               icon = {
                                   Icon(
                                       painter = painterResource(R.drawable.ic_browse),
                                       contentDescription = stringResource(R.string.ic_browse_cdesc)
                                   )
                               },
                               selected = viewModel.currentNavPage == PageDestinationKey.BROWSE,
                               onClick = {
                                   viewModel.currentNavPage = PageDestinationKey.BROWSE
                                   scope.launch {
                                       drawerState.close()
                                   }
                               }
                           )
                           NavigationDrawerItem(
                               label = {
                                   Text(stringResource(R.string.home_nav_account))
                               },
                               icon = {
                                   Icon(
                                       painter = painterResource(R.drawable.ic_person),
                                       contentDescription = stringResource(R.string.ic_person_cdesc)
                                   )
                               },
                               selected = viewModel.currentNavPage == PageDestinationKey.ACCOUNT,
                               onClick = {
                                   viewModel.currentNavPage = PageDestinationKey.ACCOUNT
                                   scope.launch {
                                       drawerState.close()
                                   }
                               },
                               modifier = Modifier.padding(top = 2.dp)
                           )
                           NavigationDrawerItem(
                               label = {
                                   Text(stringResource(R.string.home_drawer_settings))
                               },
                               icon = {
                                   Icon(
                                       painter = painterResource(R.drawable.ic_settings),
                                       contentDescription = stringResource(R.string.ic_settings_cdesc)
                                   )
                               },
                               selected = false,
                               onClick = {
                                   scope.launch {
                                       drawerState.close()
                                   }
                               },
                               modifier = Modifier.padding(top = 2.dp)
                           )
                       }
                       val isShowingPopular = viewModel.isUserless || subscribedSubreddits.value.isEmpty()
                       val subreddits = if (isShowingPopular) topSubreddits.value else subscribedSubreddits.value
                       Text(
                           text = if (isShowingPopular) {
                               stringResource(R.string.home_drawer_communities_uless)
                           } else {
                               stringResource(R.string.home_drawer_communities)
                           },
                           style = MaterialTheme.typography.titleSmall,
                           color = MaterialTheme.colorScheme.onSurfaceVariant,
                           modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                       )
                       Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                           subreddits.forEach { subreddit ->
                               NavigationDrawerItem(
                                   label = {
                                       Text("r/${subreddit.name}")
                                   },
                                   icon = {
                                       if (subreddit.iconUrl.isNotEmpty()) {
                                           AsyncImage(
                                               model = subreddit.iconUrl,
                                               contentDescription = null,
                                               modifier = Modifier.clip(CircleShape).size(25.dp)
                                           )
                                       } else {
                                           Box(
                                               modifier = Modifier.clip(CircleShape)
                                                   .size(25.dp)
                                                   .background(MaterialTheme.colorScheme.primaryContainer)
                                           ) {
                                               Icon(
                                                   painter = painterResource(R.drawable.ic_community),
                                                   contentDescription = stringResource(R.string.ic_community_cdesc),
                                                   modifier = Modifier.align(Alignment.Center)
                                                       .size(18.dp)
                                               )
                                           }
                                       }
                                   },
                                   selected = false,
                                   onClick = {
                                       scope.launch {
                                           drawerState.close()
                                       }
                                   },
                                   modifier = Modifier.padding(top = 2.dp)
                               )
                           }
                       }
                   }
                }
            }
        ) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = viewModel.currentNavPage == PageDestinationKey.BROWSE,
                            onClick = {
                                viewModel.currentNavPage = PageDestinationKey.BROWSE
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_browse),
                                    contentDescription = stringResource(R.string.ic_browse_cdesc)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.home_nav_home))
                            }
                        )
                        NavigationBarItem(
                            selected = viewModel.currentNavPage == PageDestinationKey.SEARCH,
                            onClick = {
                                viewModel.currentNavPage = PageDestinationKey.SEARCH
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search),
                                    contentDescription = stringResource(R.string.ic_search_cdesc)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.home_nav_search))
                            }
                        )
                        NavigationBarItem(
                            selected = viewModel.currentNavPage == PageDestinationKey.CHATS,
                            onClick = {
                                viewModel.currentNavPage = PageDestinationKey.CHATS
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_forum),
                                    contentDescription = stringResource(R.string.ic_chats_cdesc)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.home_nav_chats))
                            }
                        )
                        NavigationBarItem(
                            selected = viewModel.currentNavPage == PageDestinationKey.ACCOUNT,
                            onClick = {
                                viewModel.currentNavPage = PageDestinationKey.ACCOUNT
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_person),
                                    contentDescription = stringResource(R.string.ic_person_cdesc)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.home_nav_account))
                            }
                        )
                    }
                },
                topBar = {
                    AnimatedContent(
                        targetState = viewModel.currentNavPage != PageDestinationKey.SEARCH,
                        modifier = Modifier.fillMaxWidth()
                    ) { showAppBar ->
                        if (showAppBar) {
                            Column {
                                CenterAlignedTopAppBar(
                                    title = {
                                        AnimatedContent(targetState = viewModel.currentNavPage) { page ->
                                            when (page) {
                                                PageDestinationKey.BROWSE -> {
                                                    Text(stringResource(R.string.home_title))
                                                }
                                                PageDestinationKey.CHATS -> {
                                                    Text(stringResource(R.string.home_nav_chats))
                                                }
                                                PageDestinationKey.ACCOUNT -> {
                                                    Text(stringResource(R.string.home_nav_account))
                                                }
                                            }
                                        }
                                    },
                                    navigationIcon = {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    drawerState.open()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_menu),
                                                contentDescription = stringResource(R.string.ic_menu_cdesc)
                                            )
                                        }
                                    },
                                    actions = {
                                        AnimatedContent(
                                            targetState = viewModel.currentNavPage == PageDestinationKey.BROWSE
                                        ) { show ->
                                            if (show) {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.showPostFilterSheet = true
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_filter),
                                                        contentDescription = stringResource(R.string.ic_filter_cdesc)
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    scrollBehavior = scrollBehavior
                                )
                                AnimatedVisibility(
                                    visible = pagingItems.loadState.refresh is LoadState.Loading,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 5.dp)
                                ) {
                                    LinearProgressIndicator()
                                }
                            }
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(viewModel.snackbarState)
                },
                floatingActionButton = {
                    if (viewModel.currentNavPage == PageDestinationKey.BROWSE){
                        FloatingActionButton(
                            onClick = { },
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.size(65.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = stringResource(R.string.ic_plus_cdesc)
                            )
                        }
                    }
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) { paddingValues ->
                AnimatedContent(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    targetState = viewModel.currentNavPage
                ) { page ->
                    when (page) {
                        PageDestinationKey.BROWSE -> {
                            BrowsePage(
                                onRequestUserAuth = {
                                    viewModel.encourageUserAuthSnackbar()
                                },
                                onRequestPostDetailSheet = { postData ->
                                    viewModel.openPostOptionSheet(postData)
                                },
                                navController = navController
                            )
                        }
                        PageDestinationKey.SEARCH -> SearchPage(navController)
                        PageDestinationKey.CHATS -> ChatPage()
                        PageDestinationKey.ACCOUNT -> AccountPage()
                    }
                }
            }
        }

        if (viewModel.showPostFilterSheet) {
            SortPostSheet(
                onDismissRequest = { time, sort ->
                    viewModel.apply {
                        showPostFilterSheet = false
                        browseViewModel.updateFilters(sort, time)
                    }
                },
                currentSortSelection = browseViewModel.currentFilterSort,
                currentTimeSelection = browseViewModel.currentFilterTime
            )
        }

        if (viewModel.showPostOptionSheet) {
            viewModel.currentPostOptionData?.let { postData ->
                PostOptionSheet(
                    postData = postData,
                    onDismissRequest = {
                        viewModel.showPostOptionSheet = false
                    },
                    onViewUser = {
                        viewModel.showPostOptionSheet = false
                        navController.navigate("${NavDestinationKey.UserView}/${postData.author}")
                    },
                    onViewCommunity = {
                        viewModel.showPostOptionSheet = false
                        navController.navigate("${NavDestinationKey.CommunityView}/${postData.subreddit}")
                    },
                    onReport = {
                        Intent(Intent.ACTION_VIEW).apply {
                            this.data = ("https://www.reddit.com/report"
                                    + "?url=https://www.reddit.com${postData.permalink}").toUri()
                            navController.context.startActivity(this)
                        }
                    },
                    onOpenExternal = {
                        Intent(Intent.ACTION_VIEW).apply {
                            this.data = "https://www.reddit.com${postData.permalink}".toUri()
                            navController.context.startActivity(this)
                        }
                    }
                )
            }
        }
    }
}