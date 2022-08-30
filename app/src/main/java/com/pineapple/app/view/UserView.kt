package com.pineapple.app.view

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.pineapple.app.R
import com.pineapple.app.components.Chip
import com.pineapple.app.components.CommentInContext
import com.pineapple.app.components.PostCard
import com.pineapple.app.components.RoundedStarShape
import com.pineapple.app.model.reddit.CommentPreDataNull
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class
)
fun UserView(navController: NavController, user: String) {
    val viewModel: UserViewModel = viewModel()
    viewModel.initNetworkProvider(LocalContext.current)
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val topAppBarBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var currentUserInfo by remember { mutableStateOf<UserAbout?>(null) }
    LaunchedEffect(key1 = user) {
        viewModel.updateUserContent(user)
        currentUserInfo = viewModel.requestUserDetails(user)
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
                                visible = remember {
                                    derivedStateOf { lazyColumnState.firstVisibleItemIndex }
                                }.value != 0,
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
                        scrollBehavior = topAppBarBehavior
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (currentUserInfo == null) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Column(
                        modifier = Modifier.padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding(),
                            start = it.calculateStartPadding(LayoutDirection.Ltr),
                            end = it.calculateEndPadding(LayoutDirection.Ltr)
                        )
                    ) {
                        AnimatedVisibility(
                            visible = currentUserInfo != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            LazyColumn(
                                state = lazyColumnState,
                                modifier = Modifier.nestedScroll(topAppBarBehavior.nestedScrollConnection)
                            ) {
                                item {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(
                                                currentUserInfo!!.snoovatar_img.toString()
                                                    .ifBlank {
                                                        currentUserInfo!!.icon_img
                                                    }
                                            )
                                            .crossfade(true)
                                            .build().data,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 20.dp)
                                            .size(100.dp)
                                            .clip(CircleShape)
                                            .animateEnterExit(
                                                enter = slideInVertically(
                                                    animationSpec = spring(
                                                        0.8F
                                                    )
                                                ) { spec -> spec * 2 }
                                            ),
                                        contentScale = ContentScale.FillWidth,
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                            .animateEnterExit(
                                                enter = slideInVertically(
                                                    animationSpec = spring(
                                                        0.8F
                                                    )
                                                ) { spec -> spec * 3 }
                                            )
                                    ) {
                                        Text(
                                            text = currentUserInfo!!.subreddit.display_name_prefixed,
                                            style = MaterialTheme.typography.titleLarge,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = currentUserInfo!!.name.toString(),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        currentUserInfo!!.subreddit.public_description.let { desc ->
                                            if (desc.isNotBlank()) {
                                                Text(
                                                    text = desc,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(
                                                        bottom = 20.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                stickyHeader {
                                    TabRow(
                                        selectedTabIndex = viewModel.currentlySelectedTab,
                                        containerColor = animateColorAsState(
                                            if (lazyColumnState.firstVisibleItemIndex != 0) {
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                                            } else MaterialTheme.colorScheme.surface
                                        ).value,
                                        tabs = {
                                            Tab(
                                                selected = viewModel.currentlySelectedTab == 0,
                                                onClick = {
                                                    viewModel.apply {
                                                        currentlySelectedTab = 0
                                                        val tempList = mutableListOf<PostItem>()
                                                        tempList.addAll(userPostList)
                                                        userPostList.clear()
                                                        userPostList.addAll(tempList)
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.user_view_tab_post_title),
                                                    modifier = Modifier.padding(15.dp)
                                                )
                                            }
                                            Tab(
                                                selected = viewModel.currentlySelectedTab == 1,
                                                onClick = {
                                                    viewModel.apply {
                                                        currentlySelectedTab = 1
                                                        val tempList = mutableListOf<CommentPreDataNull>()
                                                        tempList.addAll(userCommentList)
                                                        userCommentList.clear()
                                                        userCommentList.addAll(tempList)
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.user_view_tab_comment_title),
                                                    modifier = Modifier.padding(15.dp)
                                                )
                                            }
                                            Tab(
                                                selected = viewModel.currentlySelectedTab == 2,
                                                onClick = { viewModel.currentlySelectedTab = 2 }
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.user_view_tab_about_title),
                                                    modifier = Modifier.padding(15.dp)
                                                )
                                            }
                                        },
                                        divider = {},
                                        indicator = {
                                            Box(
                                                Modifier
                                                    .tabIndicatorOffset(it[viewModel.currentlySelectedTab])
                                                    .padding(horizontal = 30.dp)
                                                    .padding(bottom = 5.dp)
                                                    .clip(CircleShape)
                                                    .fillMaxWidth()
                                                    .height(3.dp)
                                                    .background(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    )
                                }
                                when (viewModel.currentlySelectedTab) {
                                    0 -> {
                                        itemsIndexed(viewModel.userPostList) { index, item ->
                                            PostCard(
                                                postData = item.data,
                                                navController = navController,
                                                modifier = Modifier.animateEnterExit(
                                                    enter = slideInVertically(
                                                        animationSpec = spring(
                                                            0.8F
                                                        )
                                                    ) { spec -> spec * (index + 4) }
                                                )
                                            )
                                        }
                                    }
                                    1 -> {
                                        itemsIndexed(viewModel.userCommentList) { index, item ->
                                            CommentInContext(
                                                commentData = item.data,
                                                navController = navController,
                                                modifier = Modifier.animateEnterExit(
                                                    enter = slideInVertically(
                                                        animationSpec = spring(
                                                            0.8F
                                                        )
                                                    ) { spec -> spec * (index + 4) }
                                                )
                                            )
                                        }
                                    }
                                }
                                if (viewModel.currentlySelectedTab == 2) {
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
}