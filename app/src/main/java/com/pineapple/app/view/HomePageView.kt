package com.pineapple.app.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.R
import com.pineapple.app.util.getViewModel
import com.pineapple.app.viewmodel.HomePageViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomePageView(navController: NavController) {
    val viewModel = LocalContext.current.getViewModel(HomePageViewModel::class.java)
    rememberSystemUiController().setSystemBarsColor(MaterialTheme.colorScheme.surfaceVariant)
    Scaffold(
        topBar = {
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
                            painter = painterResource(id = R.drawable.ic_menu_icon),
                            contentDescription = stringResource(id = R.string.ic_menu_icon_content_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(MaterialTheme.colorScheme.surfaceVariant),
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter_config_icon),
                            contentDescription = stringResource(id = R.string.ic_filter_config_icon_content_desc)
                        )
                    }
                }
            )
        },
        bottomBar = {
            data class NavItem(var text: Int, var icon: Int, var contentDesc: Int)
            val navbarItems = listOf(
                NavItem(R.string.home_bottom_bar_item_browse,
                    R.drawable.ic_home_icon, R.string.ic_home_icon_content_desc),
                NavItem(R.string.home_bottom_bar_item_search,
                    R.drawable.ic_search_glyph, R.string.ic_search_glyph_content_desc),
                NavItem(R.string.home_bottom_bar_item_chats,
                    R.drawable.ic_chat_bubbles, R.string.ic_chat_bubbles_content_desc),
                NavItem(R.string.home_bottom_bar_item_account,
                    R.drawable.ic_user_circle, R.string.ic_user_circle_content_desc)
            )
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        onClick = { viewModel.selectedTabItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    ) {
    }
}