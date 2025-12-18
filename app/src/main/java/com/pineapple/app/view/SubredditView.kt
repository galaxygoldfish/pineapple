package com.pineapple.app.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.R
import com.pineapple.app.components.FilterBottomSheet
import com.pineapple.app.components.MDDocument
import com.pineapple.app.model.reddit.SubredditData
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.prettyNumber
import com.pineapple.app.viewmodel.SubredditViewModel
import kotlinx.coroutines.launch
import org.commonmark.node.Document
import org.commonmark.parser.Parser


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun SubredditView(navController: NavController, subreddit: String) {
    val viewModel: SubredditViewModel = viewModel()
    val context = LocalContext.current
    var subredditInfo by remember { mutableStateOf<SubredditData?>(null) }
    var currentBottomSheet by remember { mutableStateOf(0) }
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    viewModel.currentSubreddit = subreddit
    LaunchedEffect(true) {
        viewModel.fetchInformation(context).collect {
            subredditInfo = it.data
        }
    }

    PineappleTheme {
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp),
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            sheetContent = {
                when(currentBottomSheet) {
                    0 -> SubredditInfoBottomSheet(subredditInfo)
                    1 -> FilterBottomSheet(
                        timePeriod = viewModel.currentSortTime,
                        sortType = viewModel.currentSortType,
                        bottomSheetState = bottomSheetState
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            AnimatedVisibility(
                                visible = scrollState.firstVisibleItemIndex != 0,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = subredditInfo?.displayNamePrefixed.toString(),
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
                        actions = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        currentBottomSheet = 1
                                        bottomSheetState.show()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_discover_tune),
                                    contentDescription = stringResource(id = R.string.ic_discover_tune_content_desc)
                                )
                            }
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        currentBottomSheet = 0
                                        bottomSheetState.show()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_info),
                                    contentDescription = stringResource(id = R.string.ic_info_content_desc)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (scrollState.firstVisibleItemIndex != 0) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding(),
                            start = it.calculateStartPadding(LayoutDirection.Ltr),
                            end = it.calculateEndPadding(LayoutDirection.Ltr)
                        )
                ) {
                    PostListView(
                        navController = navController,
                        subreddit = subreddit,
                        sort = viewModel.currentSortType.value,
                        time = viewModel.currentSortTime.value,
                        scrollState = scrollState,
                        topHeaderItem = {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    if (subredditInfo?.iconUrl.toString().isNotEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(subredditInfo?.iconUrl.toString().replace("amp;", ""))
                                                .crossfade(true)
                                                .build().data,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(bottom = 20.dp)
                                                .size(100.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.FillWidth,
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_atr_dots),
                                            contentDescription = stringResource(R.string.ic_atr_dots_content_desc),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .padding(bottom = 20.dp) // Actual container padding
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .size(100.dp)
                                                .padding(top = 10.dp, bottom = 18.dp, start = 10.dp, end = 10.dp)
                                        )
                                    }
                                    Text(
                                        text = subredditInfo?.displayNamePrefixed.toString(),
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        text = String.format(
                                            stringResource(id = R.string.community_user_count_format),
                                            subredditInfo?.subscribers?.toInt()?.prettyNumber().toString()
                                        ),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                    Text(
                                        text = subredditInfo?.public_description.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                    FilledTonalButton(
                                        onClick = { /*TODO*/ },
                                        modifier = Modifier.padding(top = 10.dp),
                                        contentPadding = PaddingValues(start = 10.dp, end = 15.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_add),
                                            contentDescription = stringResource(id = R.string.ic_add_content_desc)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.community_join_button_text),
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SubredditInfoBottomSheet(subredditInfo: SubredditData?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .clip(RoundedCornerShape(5.dp))
                .size(width = 100.dp, height = 5.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {  }
        if (subredditInfo?.iconUrl?.isNotEmpty() == true) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(subredditInfo.iconUrl.replace("amp;", ""))
                    .crossfade(true)
                    .build().data,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth,
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_atr_dots),
                contentDescription = stringResource(R.string.ic_atr_dots_content_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .size(100.dp)
                    .padding(top = 10.dp, bottom = 18.dp, start = 10.dp, end = 10.dp)
            )
        }
        Text(
            text = subredditInfo?.title.toString(),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = String.format(
                stringResource(id = R.string.community_user_count_format),
                subredditInfo?.subscribers?.toInt()?.prettyNumber() ?: "0"
            ),
            style = MaterialTheme.typography.titleMedium
        )
        subredditInfo?.description?.let {
            Column(modifier = Modifier.padding(20.dp)) {
                MDDocument(document = Parser.builder().build().parse(it) as Document)
            }
        }
    }
}