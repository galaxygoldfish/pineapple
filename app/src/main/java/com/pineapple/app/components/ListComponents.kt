package com.pineapple.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.model.reddit.CommentDataNull
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.network.GfycatNetworkService
import com.pineapple.app.network.NetworkServiceBuilder.GFYCAT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkProvider
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.calculateRatioHeight
import com.pineapple.app.util.convertUnixToRelativeTime
import com.pineapple.app.util.prettyNumber
import com.pineapple.app.util.surfaceColorAtElevation
import com.pineapple.app.viewmodel.PostDetailViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PostCard(
    postData: PostData,
    onClick: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val gfycatNetworkService = remember { apiService<GfycatNetworkService>(GFYCAT_BASE_URL) }
    PineappleTheme {
        Surface(
            tonalElevation = 0.dp,
            color = Color.Transparent
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.5.dp)
                ),
                modifier = modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
                onClick = onClick
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        AvatarPlaceholderIcon(
                            modifier = Modifier
                                .padding(top = 15.dp, start = 15.dp, end = 10.dp)
                                .clickable {
                                    navController.navigate("${NavDestination.UserView}/${postData.author}")
                                }
                        )
                        Column(
                            modifier = Modifier.padding(top = 18.dp)
                        ) {
                            Text(
                                text = postData.author,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "r/${postData.subreddit}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Text(
                        text = postData.createdUTC.convertUnixToRelativeTime(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(end = 15.dp)
                    )
                }
                Text(
                    text = postData.title,
                    maxLines = 3,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp)
                )
                FlairBar(
                    postData = postData,
                    modifier = Modifier.padding(start = 15.dp)
                )
                val mediaLink = when (postData.postHint) {
                    "hosted:video" -> postData.secureMedia!!.reddit_video.fallback_url.replace(
                        "amp;",
                        ""
                    )
                    "rich:video" -> postData.url
                    else -> {
                        postData.preview?.images?.get(0)?.source?.url?.replace("amp;", "")
                            ?.ifEmpty { postData.url }
                    }
                }
                mediaLink?.let {
                    MultiTypeMediaView(
                        mediaHint = postData.postHint,
                        url = it,
                        gfycatService = gfycatNetworkService,
                        richDomain = postData.domain,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                            .height(
                                when (postData.postHint) {
                                    "image", "rich_video" -> {
                                        LocalContext.current.calculateRatioHeight(
                                            ratioWidth = postData.thumbnailWidth.toInt(),
                                            ratioHeight = postData.thumbnailHeight.toInt(),
                                            actualWidth = LocalConfiguration.current.screenWidthDp - 44
                                        )
                                    }
                                    else -> {
                                        LocalContext.current.calculateRatioHeight(
                                            ratioHeight = postData.secureMedia?.reddit_video?.height?.toInt()
                                                ?: 0,
                                            ratioWidth = postData.secureMedia?.reddit_video?.width?.toInt()
                                                ?: 0,
                                            actualWidth = LocalConfiguration.current.screenWidthDp - 44
                                        )
                                    }
                                }
                            )
                            .clip(RoundedCornerShape(10.dp)),
                        playerControls = { player ->
                            VideoControls(
                                player = player,
                                onExpand = {
                                    navController.navigate(
                                        "${NavDestination.MediaDetailView}/${postData.postHint}/${
                                            URLEncoder.encode(mediaLink)
                                        }/${postData.domain}/${URLEncoder.encode(postData.title)}"
                                    )
                                },
                                postTitle = postData.title
                            )
                        },
                        expandToFullscreen = {
                            navController.navigate(
                                "${NavDestination.MediaDetailView}/${postData.postHint}/${
                                    URLEncoder.encode(mediaLink)
                                }/${postData.domain}/${URLEncoder.encode(postData.title)}"
                            )
                        },
                        previewUrl = postData.preview?.images
                            ?.get(0)
                            ?.source?.url
                            ?.replace("amp;", "") ?: ""
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 15.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledTonalIconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.size(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = stringResource(id = R.string.ic_share_content_desc),
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .size(20.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.size(35.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_thumb_up),
                                contentDescription = stringResource(id = R.string.ic_thumb_up_content_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = postData.ups
                                .toInt()
                                .prettyNumber(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(10.dp)
                        )
                        FilledTonalIconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.size(35.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_thumb_down),
                                contentDescription = stringResource(id = R.string.ic_thumb_down_content_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallListCard(
    text: String,
    iconUrl: String,
    onClick: () -> Unit,
    userIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.4F)
        ),
        onClick = { onClick.invoke() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (iconUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iconUrl)
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
                    painter = painterResource(
                        id = if (userIcon) {
                            R.drawable.ic_person
                        } else {
                            R.drawable.ic_atr_dots
                        }
                    ),
                    contentDescription = stringResource(
                        id = if (userIcon) {
                            R.string.ic_person_content_desc
                        } else {
                            R.string.ic_atr_dots_content_desc
                        }
                    ),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(12.dp) // Actual container padding
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .size(25.dp)
                        .padding(4.dp) // Icon padding
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun CommentBubble(
    commentDataJson: JSONObject? = null,
    commentData: CommentDataNull? = null,
    viewModel: PostDetailViewModel,
    onExpandReplies: ((JSONArray) -> Unit) = { },
    modifier: Modifier,
    allowExpandReplies: Boolean = true,
    specialComment: Boolean = false
) {
    var body = ""
    var author = ""
    var replies = JSONArray()
    val context = LocalContext.current
    try {
        commentDataJson?.apply {
            body = getString("body")
            author = getString("author")
            replies = getJSONObject("replies")
                .getJSONObject("data")
                .getJSONArray("children")
        }
        commentData?.apply {
            body = this.body.toString()
            author = this.author
        }
    } catch (_: JSONException) {

    }

    Column(modifier) {
        body.let { comment ->
            if (comment != "[removed]") {
                var currentTextLines by remember { mutableStateOf(Integer.MAX_VALUE) }
                var shouldShowExpansion by remember { mutableStateOf(false) }
                var userInformation by remember { mutableStateOf<UserAbout?>(null) }
                LaunchedEffect(key1 = author) {
                    if (author != "[deleted]" && author.isNotBlank()) {
                        userInformation = RedditNetworkProvider(context).fetchUserInfo(author).data
                    }
                }
                AnimatedVisibility(visible = true) {
                    userInformation?.let { user ->
                        Row(
                            modifier = Modifier
                                .width(LocalConfiguration.current.screenWidthDp.dp)
                                .padding(top = 10.dp)
                        ) {
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
                            Column {
                                Text(
                                    text = user.name ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 2.dp, bottom = 5.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column(
                                    modifier = Modifier
                                        .padding(end = 17.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (!specialComment) {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            } else Color.Transparent
                                        )
                                        .border(
                                            width = if (specialComment) 2.dp else 0.dp,
                                            color = if (specialComment) {
                                                MaterialTheme.colorScheme.outline
                                            } else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
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
                                        },
                                        color = MaterialTheme.colorScheme.onBackground
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
                                                            if (it == Integer.MAX_VALUE) {
                                                                5
                                                            } else {
                                                                Integer.MAX_VALUE
                                                            }
                                                    }
                                                },
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (replies.length() > 0 && allowExpandReplies) {
                                TextButton(onClick = { onExpandReplies(replies) }) {
                                        Text(
                                            text =  stringResource(id = R.string.post_view_comments_expand_replies),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_arrow_forward_slim),
                                            contentDescription = stringResource(id = R.string.ic_arrow_forward_slim_content_desc),
                                            modifier = Modifier
                                                .padding(start = 10.dp)
                                                .size(15.dp)
                                        )
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CommentInContext(
    commentData: CommentDataNull,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.5.dp)
        ),
        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
        onClick = {
            val permalink = commentData.permalink.split("/")
            val sub = permalink[2]
            val uid = permalink[4]
            val link = permalink[5]
            navController.navigate("${NavDestination.PostDetailView}/$sub/$uid/$link")
        }
    ) {
        Text(
            text = "\"${commentData.link_title.toString()}\"",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(15.dp)
        )
        CommentBubble(
            viewModel = viewModel(),
            modifier = Modifier.padding(bottom = 15.dp),
            commentData = commentData,
            allowExpandReplies = false,
            specialComment = true
        )
    }
}