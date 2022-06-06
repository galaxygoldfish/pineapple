package com.pineapple.app.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.R
import com.pineapple.app.components.FilterBottomSheet
import com.pineapple.app.util.getViewModel
import com.pineapple.app.util.surfaceColorAtElevation
import com.pineapple.app.viewmodel.HomePageViewModel
import kotlinx.coroutines.launch

object BottomNavDestinations {
    const val Home = "home"
    const val Search = "search"
    const val Chats = "chats"
    const val Account = "account"
}

@Composable
@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class)
fun HomePageView(navController: NavController) {
    val viewModel = LocalContext.current.getViewModel(HomePageViewModel::class.java)
    val bottomNavController = rememberNavController()
    val asynchronousScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
    rememberSystemUiController().setSystemBarsColor(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
    ModalBottomSheetLayout(
        sheetContent = {
            FilterBottomSheet(
                timePeriod = viewModel.currentSortTime,
                sortType = viewModel.currentSortType
            )
        },
        sheetState = bottomSheetState
    ) {
        Scaffold(
            topBar = {
                if (viewModel.selectedTabItem != 1) {
                    Surface(tonalElevation = 2.dp) {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.home_top_bar_title_default),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_menu),
                                        contentDescription = stringResource(id = R.string.ic_menu_content_desc)
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        asynchronousScope.launch { bottomSheetState.show() }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_discover_tune),
                                        contentDescription = stringResource(id = R.string.ic_discover_tune_content_desc)
                                    )
                                }
                            },
                            colors = smallTopAppBarColors(MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            },
            bottomBar = {
                data class NavItem(var text: Int, var icon: Int, var contentDesc: Int)
                val navbarItems = listOf(
                    NavItem(
                        R.string.home_bottom_bar_item_browse,
                        R.drawable.ic_dashboard, R.string.ic_dashboard_content_desc
                    ),
                    NavItem(
                        R.string.home_bottom_bar_item_search,
                        R.drawable.ic_search, R.string.ic_search_content_desc
                    ),
                    NavItem(
                        R.string.home_bottom_bar_item_chats,
                        R.drawable.ic_forum, R.string.ic_forum_content_desc
                    ),
                    NavItem(
                        R.string.home_bottom_bar_item_account,
                        R.drawable.ic_person, R.string.ic_person_content_desc
                    )
                )
                NavigationBar(tonalElevation = 2.dp) {
                    navbarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = viewModel.selectedTabItem == index,
                            label = {
                                Text(
                                    text = stringResource(id = item.text),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = stringResource(id = item.contentDesc)
                                )
                            },
                            onClick = {
                                viewModel.selectedTabItem = index
                                bottomNavController.navigate(
                                    when (index) {
                                        0 -> BottomNavDestinations.Home
                                        1 -> BottomNavDestinations.Search
                                        2 -> BottomNavDestinations.Chats
                                        3 -> BottomNavDestinations.Account
                                        else -> BottomNavDestinations.Home
                                    }
                                )
                            }
                        )
                    }
                }
            },
            content = {
                Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
                    NavHost(
                        navController = bottomNavController,
                        startDestination = BottomNavDestinations.Home,
                        modifier = Modifier.padding(bottom = 80.dp)
                    ) {
                        composable(BottomNavDestinations.Home) {
                            PostListView(
                                navController = navController,
                                subreddit = "all",
                                sort = "hot"
                            )
                        }
                        composable("${BottomNavDestinations.Home}/{sub}/{sort}") {
                            it.arguments?.let { args ->
                                PostListView(
                                    navController = navController,
                                    subreddit = args.getString("sub")!!,
                                    sort = args.getString("sort")!!
                                )
                            }
                        }
                        composable(BottomNavDestinations.Search) {
                            SearchView(navController)
                        }
                        // Chats
                        // Account
                    }
                }
            }
        )
    }
}