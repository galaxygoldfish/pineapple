package com.pineapple.app.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.R
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.model.reddit.SubredditItem
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.parseFlair
import com.pineapple.app.util.prettyNumber

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
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.8F),
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
                    postData.linkFlairRichtext?.let { list ->
                        if (list.isNotEmpty()) {
                            list.parseFlair(isSystemInDarkTheme()).let { pair ->
                                Column(
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.8F)),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    BasicText(
                                        text = pair.first,
                                        style = MaterialTheme.typography.labelMedium,
                                        inlineContent = pair.second,
                                        modifier = Modifier.padding(
                                            vertical = 5.dp,
                                            horizontal = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
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
fun SubredditListCard(item: SubredditItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.4F)),
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