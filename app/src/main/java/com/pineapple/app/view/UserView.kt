package com.pineapple.app.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.R
import com.pineapple.app.components.Chip
import com.pineapple.app.components.CommentInContext
import com.pineapple.app.components.PostCard
import com.pineapple.app.model.reddit.CommentPreDataNull
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.surfaceColorAtElevation
import com.pineapple.app.viewmodel.UserViewModel

@Composable
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
fun UserView(navController: NavController, user: String) {
    val viewModel: UserViewModel = viewModel()
    viewModel.initNetworkProvider(LocalContext.current)
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val lazyColumnState = rememberLazyListState()
    var currentUserInfo by remember { mutableStateOf<UserAbout?>(null) }
    LaunchedEffect(key1 = user) {
        currentUserInfo = viewModel.requestUserDetails(user)
        viewModel.updateUserContent(user)
    }
    rememberSystemUiController().apply {
        setStatusBarColor(
            if (lazyColumnState.firstVisibleItemIndex != 0) {
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            } else MaterialTheme.colorScheme.surface
        )
        setNavigationBarColor(MaterialTheme.colorScheme.surface)
    }
    PineappleTheme {
        ModalBottomSheetLayout(
            sheetContent = { Text("dd") },
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            sheetShape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp),
            scrimColor = Color.Black.copy(0.4F),
            sheetState = bottomSheetState
        ) {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        title = {
                            AnimatedVisibility(
                                visible = lazyColumnState.firstVisibleItemIndex != 0,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = currentUserInfo?.subreddit?.display_name_prefixed.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = if (lazyColumnState.firstVisibleItemIndex != 0) {
                                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            } else MaterialTheme.colorScheme.surface
                        )
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
                    AnimatedVisibility(visible = currentUserInfo != null) {
                        LazyColumn(state = lazyColumnState) {
                            item {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(
                                            currentUserInfo!!.snoovatar_img.toString().ifBlank {
                                                currentUserInfo!!.icon_img
                                            }
                                        )
                                        .crossfade(true)
                                        .build().data,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                        .size(100.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.FillWidth,
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 25.dp, vertical = 20.dp)
                                ) {
                                    Text(
                                        text = currentUserInfo!!.subreddit.display_name_prefixed,
                                        style = MaterialTheme.typography.titleLarge + TextStyle(
                                            fontSize = 28.sp
                                        ),
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = currentUserInfo!!.subreddit.public_description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                            }
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (lazyColumnState.firstVisibleItemIndex != 0) {
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                                            } else MaterialTheme.colorScheme.surface
                                        )
                                        .padding(start = 20.dp, end = 10.dp, bottom = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    Chip(
                                        text = stringResource(id = R.string.user_view_tab_post_title),
                                        selected = viewModel.currentlySelectedTab == 0,
                                        icon = painterResource(id = R.drawable.ic_dashboard),
                                        contentDescription = stringResource(id = R.string.ic_dashboard_content_desc),
                                        onClick = { viewModel.currentlySelectedTab = 0 }
                                    )
                                    Chip(
                                        text = stringResource(id = R.string.user_view_tab_comment_title),
                                        selected = viewModel.currentlySelectedTab == 1,
                                        icon = painterResource(id = R.drawable.ic_forum),
                                        contentDescription = stringResource(id = R.string.ic_forum_content_desc),
                                        onClick = { viewModel.currentlySelectedTab = 1 }
                                    )
                                    Chip(
                                        text = stringResource(id = R.string.user_view_tab_about_title),
                                        selected = viewModel.currentlySelectedTab == 2,
                                        icon = painterResource(id = R.drawable.ic_info),
                                        contentDescription = stringResource(id = R.string.ic_info_content_desc),
                                        onClick = { viewModel.currentlySelectedTab = 2 }
                                    )
                                }
                            }
                            if (viewModel.currentlySelectedTab < 2) {
                                itemsIndexed(
                                    when (viewModel.currentlySelectedTab) {
                                        0 -> viewModel.userPostList
                                        1 -> viewModel.userCommentList
                                        else -> viewModel.userPostList
                                    }
                                ) { _, item ->
                                    when (viewModel.currentlySelectedTab) {
                                        0 -> {
                                            PostCard(
                                                postData = (item as PostItem).data,
                                                onClick = { /*TODO*/ },
                                                navController = navController
                                            )
                                        }
                                        1 -> {
                                            CommentInContext(
                                                commentData = (item as CommentPreDataNull).data,
                                                navController = navController
                                            )
                                        }
                                    }
                                }
                            } else {
                                item {
                                    Column() {
                                        Card(

                                        ) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}