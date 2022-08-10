package com.pineapple.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.components.FilterBottomSheet
import com.pineapple.app.components.drawerCustomShape
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
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    rememberSystemUiController().setSystemBarsColor(
        MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
    )
    LaunchedEffect(true) {
        viewModel.refreshTopCommunities()
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerShape = drawerCustomShape(
            localConfig = LocalConfiguration.current,
            context = LocalContext.current,
            widthRatio = 0.78F
        ),
        gesturesEnabled = true,
        drawerTonalElevation = 3.dp,
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(0.3F),
        drawerContent = {
            HomeNavigationDrawer(navController, viewModel)
        }
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                FilterBottomSheet(
                    timePeriod = viewModel.currentSortTime,
                    sortType = viewModel.currentSortType,
                    bottomSheetState = bottomSheetState
                )
            },
            sheetState = bottomSheetState,
            scrimColor = Color.Black.copy(0.3F),
            sheetShape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
        ) {
            Scaffold(
                topBar = {
                    if (viewModel.selectedTabItem != 1) {
                        Surface(tonalElevation = 1.dp) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.home_top_bar_title_default),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        asynchronousScope.launch { drawerState.open() }
                                    }) {
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
                                colors = smallTopAppBarColors(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
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
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                        tonalElevation = 0.dp
                    ) {
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
                                    sort = viewModel.currentSortType.value.toLowerCase(Locale.current),
                                    time = viewModel.currentSortTime.value,
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
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeNavigationDrawer(navController: NavController, viewModel: HomePageViewModel) {
    Column(modifier = Modifier.fillMaxWidth(0.78F)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pineapple_transparent),
                contentDescription = stringResource(id = R.string.ic_pineapple_transparent_content_desc),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 30.dp, start = 20.dp, bottom = 23.dp)
            )
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.home_nav_drawer_home_title)) },
                selected = true,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dashboard),
                        contentDescription = stringResource(id = R.string.ic_discover_tune_content_desc)
                    )
                },
                onClick = { },
                modifier = Modifier.padding(top = 5.dp)
            )
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.home_nav_drawer_settings_title)) },
                selected = false,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = stringResource(id = R.string.ic_settings_content_desc)
                    )
                },
                onClick = { navController.navigate(NavDestination.SettingsView) },
                modifier = Modifier.padding(top = 5.dp)
            )
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.home_nav_drawer_account_button)) },
                selected = false,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_account_circle),
                        contentDescription = stringResource(id = R.string.ic_account_circle_content_desc)
                    )
                },
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 5.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 20.dp, horizontal = 5.dp))
            Text(
                text = stringResource(id = R.string.home_nav_drawer_popular_communities_header),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
            )
            viewModel.popularSubreddits.forEach { item ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = item.data.displayNamePrefixed,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = false,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_atr_dots),
                            contentDescription = stringResource(id = R.string.ic_atr_dots_content_desc),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .size(25.dp)
                                .padding(4.dp) // Icon padding
                        )
                    },
                    onClick = {
                        navController.navigate("${NavDestination.SubredditView}/${
                            item.data.url.replace("r/", "").replace("/", "")
                        }")
                    },
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
        // If logged in then show user's joined communities, else show popular subreddits
    }
}