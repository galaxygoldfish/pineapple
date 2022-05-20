package com.pineapple.app.components

import android.icu.number.IntegerWidth
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.model.reddit.CommentData
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.prettyNumber
import com.pineapple.app.viewmodel.PostDetailViewModel
import kotlin.math.min

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
fun TextPostCard(postData: PostData, onClick: () -> Unit) {
    PineappleTheme {
        Surface(
            tonalElevation = 0.dp,
            color = Color.Transparent
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.8F)),
                modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
                onClick = onClick
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(
                        text = postData.author,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "r/${postData.subreddit}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = postData.title,
                        maxLines = 3,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    FlairBar(postData = postData)
                }
                // val imageLink = postData.preview.images?.get(0)?.source?.url?.replace("amp;", "")?.ifEmpty { postData.url }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(postData.url)
                        .placeholder(R.drawable.placeholder_image)
                        .crossfade(true)
                        .build().data,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        PostCardIconButton(
                            icon = painterResource(id = R.drawable.ic_comments_bubble),
                            contentDescription = stringResource(id = R.string.ic_comments_bubble_content_desc),
                            text = postData.numComments.toInt().prettyNumber()
                        )
                        PostCardIconButton(
                            icon = painterResource(id = R.drawable.ic_thumbs_up),
                            contentDescription = stringResource(id = R.string.ic_thumbs_up_content_desc),
                            text = postData.ups.toInt().prettyNumber()
                        )
                    }
                    Row(modifier = Modifier.padding(end = 15.dp)) {
                        PostCardIconButton(
                            icon = painterResource(id = R.drawable.ic_share_glyph),
                            contentDescription = stringResource(id = R.string.ic_share_glyph_content_desc),
                            text = ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCardIconButton(
    icon: Painter,
    contentDescription: String,
    text: String,
    onClick: (() -> Unit) = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
            .clickable { onClick.invoke() }
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SubredditListCard(item: SubredditItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.4F))
            .clickable {
                navController.navigate("${NavDestination.SubredditView}/${item.data.title}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val communityIcon = item.data.iconUrl.replace(";", "").replace("amp", "")
        if (communityIcon.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(communityIcon)
                    .error(R.drawable.placeholder_image)
                    .crossfade(true)
                    .build().data,
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(25.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth,
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_reddit_logo),
                contentDescription = stringResource(id = R.string.ic_reddit_logo_content_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(12.dp) // Actual container padding
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .size(25.dp)
                    .padding(4.dp) // Reddit icon padding
            )
        }
        Text(
            text = item.data.displayNamePrefixed,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun CommentBubble(commentData: CommentData, viewModel: PostDetailViewModel) {
    commentData.body?.let { comment ->
        if (comment != "[removed]") {
            var currentTextLines by remember { mutableStateOf(Integer.MAX_VALUE) }
            var shouldShowExpansion by remember { mutableStateOf(false) }
            var userInformation by remember { mutableStateOf<UserAbout?>(null) }
            LaunchedEffect(key1 = true) {
                if (commentData.author != "[deleted]") {
                    userInformation = viewModel.networkService.fetchUserInfo(commentData.author).data
                }
            }
            AnimatedVisibility(visible = true) {
                Row(
                    modifier = Modifier
                        .width(LocalConfiguration.current.screenWidthDp.dp)
                        .padding(top = 17.dp)
                ) {
                    userInformation?.let { user ->
                        val url = user.snoovatar_img.toString().ifBlank {
                            user.icon_img
                        }
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .crossfade(true)
                                .build().data,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp, top = 2.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5F)),
                            contentScale = ContentScale.FillWidth,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(end = 17.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = comment,
                            modifier = Modifier.padding(10.dp),
                            maxLines = currentTextLines,
                            onTextLayout = { textLayoutResult ->
                                if (textLayoutResult.lineCount > 5 && !shouldShowExpansion) {
                                    shouldShowExpansion = true
                                    currentTextLines = 5
                                }
                            }
                        )
                        if (shouldShowExpansion) {
                            Text(
                                text = if (currentTextLines != Integer.MAX_VALUE) {
                                    stringResource(id = R.string.post_view_comments_expand_bubble)
                                } else {
                                    stringResource(id = R.string.post_view_comments_collapse_bubble)
                                },
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .padding(top = 2.dp, bottom = 10.dp, start = 10.dp)
                                    .clickable {
                                        currentTextLines.let {
                                            currentTextLines =
                                                if (it == Integer.MAX_VALUE) 5 else Integer.MAX_VALUE
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}