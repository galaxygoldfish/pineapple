@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.pineapple.app.ui.components

import android.content.Intent
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pineapple.app.R
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.network.model.reddit.UserAboutListing
import com.pineapple.app.ui.theme.FullCornerRadius
import com.pineapple.app.ui.theme.MediumCornerRadius
import com.pineapple.app.utilities.convertUnixToRelativeTime
import com.pineapple.app.utilities.prettyNumber

/**
 * A compact card representing a post to be used in list views
 * @param postData The data of the post to be displayed
 * @param modifier The modifier to be applied to the card
 * @param userInfo User info used to display the author's avatar
 * @param onClick Lambda to be invoked when the card is clicked
 * @param onMoreClick Lambda to be invoked when the more options button is clicked
 * @param onSaveClick Lambda to be invoked when the save button is clicked
 * @param onUpvote Lambda to be invoked when the upvote button is clicked
 * @param onDownvote Lambda to be invoked when the downvote button is clicked
 */
@Composable
fun PostCard(
    postData: PostData,
    modifier: Modifier = Modifier,
    userInfo: UserAboutListing? = null,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    onSaveClick: (Boolean, () -> Unit) -> Unit,
    onUpvote: (Boolean, () -> Unit) -> Unit,
    onDownvote: (Boolean, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var bookmarkedState by rememberSaveable { mutableStateOf(postData.saved) }
    var upvoteState by rememberSaveable { mutableStateOf(postData.likes == true) }
    var downvoteState by rememberSaveable { mutableStateOf(postData.likes == false) }

    val imageData = postData.preview?.images?.get(0)?.source
    val width = imageData?.width?.toFloat() ?: 0f
    val height = imageData?.height?.toFloat() ?: 0f
    val imageUrl = imageData?.url?.replace("amp;", "")?.ifEmpty { postData.url }
    val computedAspectRatio = if (width > 0f && height > 0f) {
        (width / height).coerceIn(0.2f, 4f)
    } else null

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                enabled = true,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onMoreClick()
                },
                onClick = onClick
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 13.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                userInfo?.let {
                    AsyncImage(
                        model = it.data.snoovatar_img?.ifBlank { null } ?: it.data.icon_img,
                        contentDescription = null,
                        modifier = Modifier.clip(CircleShape)
                            .size(35.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    postData.author?.let {
                        Text(
                            text = "u/$it",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    postData.subredditNamePrefixed?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            postData.createdUTC?.let {
                Text(
                    text = it.convertUnixToRelativeTime(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        postData.title?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 13.dp, end = 13.dp, bottom = 5.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (imageUrl !== null && imageUrl.isNotEmpty()) {
            var aspectRatio by rememberSaveable(postData.id + "_ratio") {
                mutableStateOf<Float?>(computedAspectRatio)
            }
            aspectRatio?.let {
                MeasuredAsyncImage(
                    imageUrl = imageUrl,
                    aspectRatio = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 13.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 10.dp)
        ) {
            Row {
                FilledTonalIconButton(
                    onClick = { onMoreClick() },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .width(33.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert),
                        contentDescription = stringResource(R.string.ic_more_vert_cdesc)
                    )
                }
                FilledTonalIconButton(
                    onClick = {
                        postData.permalink?.let { url ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://reddit.com$url")
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share post")
                            context.startActivity(shareIntent)
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = stringResource(R.string.ic_share_cdesc)
                    )
                }
                AnimatedTonalToggleIconButton(
                    checked = bookmarkedState == true,
                    onCheckedChange = { onSaveClick(it) { bookmarkedState = it } },
                    checkedIcon = painterResource(R.drawable.ic_bookmark_filled),
                    uncheckedIcon = painterResource(R.drawable.ic_bookmark),
                    contentDescription = stringResource(R.string.ic_bookmark_cdesc)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedTonalToggleIconButton(
                    checked = downvoteState,
                    onCheckedChange = {
                        onDownvote(it) {
                            downvoteState = it
                            upvoteState = false
                        }
                    },
                    checkedIcon = painterResource(R.drawable.ic_downvote),
                    uncheckedIcon = painterResource(R.drawable.ic_downvote),
                    contentDescription = stringResource(R.string.ic_downvote_cdesc),
                    modifier = Modifier.width(33.dp),
                    uncheckedRadius = 30.dp,
                    checkedRadius = MediumCornerRadius
                )
                postData.ups?.toInt()?.prettyNumber()?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
                AnimatedTonalToggleIconButton(
                    checked = upvoteState,
                    onCheckedChange = {
                        onUpvote(it) {
                            upvoteState = it
                            downvoteState = false
                        }
                    },
                    checkedIcon = painterResource(R.drawable.ic_upvote),
                    uncheckedIcon = painterResource(R.drawable.ic_upvote),
                    contentDescription = stringResource(R.string.ic_upvote_cdesc),
                    modifier = Modifier.width(33.dp),
                    uncheckedRadius = 30.dp,
                    checkedRadius = MediumCornerRadius
                )
            }
        }
    }
}

@Composable
fun CommentCard(
    commentWithUser: CommentWithUser?,
    modifier: Modifier = Modifier,
    showingTrailingButtons: Boolean = true,
    onMoreClick: () -> Unit = { },
    onUpvote: (Boolean, () -> Unit) -> Unit = { _, _, -> },
    onDownvote: (Boolean, () -> Unit) -> Unit = { _, _, -> },
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    val author = commentWithUser?.comment?.author
    var downvoteState by rememberSaveable { mutableStateOf(commentWithUser?.comment?.likes == false) }
    var upvoteState by rememberSaveable { mutableStateOf(commentWithUser?.comment?.likes == true) }

    Column(modifier) {
        Row {
            commentWithUser?.user?.let {
                AsyncImage(
                    model = it.snoovatarUrl?.ifBlank { null }
                        ?: it.iconUrl,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.generic_avatar),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(17.dp)
                )
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Text(
                            text = author?.let { "u/$it" } ?: "u/[deleted]",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(start = 7.dp)
                                .align(Alignment.CenterVertically)
                        )
                        commentWithUser?.comment?.createdUtc?.let { created ->
                            val rel = try {
                                created.convertUnixToRelativeTime()
                            } catch (_: Exception) {
                                ""
                            }
                            if (rel.isNotEmpty()) {
                                Text(
                                    text = rel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(
                                        start = 7.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.padding(top = 7.dp)) {
            var cardHeightPx by remember { mutableIntStateOf(0) }
            val density = androidx.compose.ui.platform.LocalDensity.current
            val requiredPx = with(density) { (40.dp + 5.dp + 40.dp).toPx().toInt() }

            Box(modifier = Modifier.weight(1f, false)) {
                Card(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                        .combinedClickable(
                            enabled = true,
                            onClick = { },
                            onLongClick = onMoreClick
                        ).onSizeChanged { cardHeightPx = it.height },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = containerColor
                    )
                ) {
                    Text(
                        text = commentWithUser?.comment?.body.toString()
                            .trimIndent().trimStart(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (showingTrailingButtons) {
                if (cardHeightPx >= requiredPx) {
                    Column(
                        modifier = Modifier.padding(start = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        AnimatedTonalToggleIconButton(
                            checked = upvoteState,
                            onCheckedChange = { checked ->
                                onUpvote(checked) {
                                    upvoteState = checked
                                    if (checked) {
                                        downvoteState = false
                                    }
                                }
                            },
                            checkedIcon = painterResource(R.drawable.ic_upvote),
                            uncheckedIcon = painterResource(R.drawable.ic_upvote),
                            contentDescription = stringResource(R.string.ic_upvote_cdesc),
                            modifier = Modifier.size(30.dp, 40.dp),
                            uncheckedRadius = MediumCornerRadius,
                            checkedRadius = FullCornerRadius,
                            colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkedContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        AnimatedTonalToggleIconButton(
                            checked = downvoteState,
                            onCheckedChange = { checked ->
                                onDownvote(checked) {
                                    downvoteState = checked
                                    if (checked) {
                                        upvoteState = false
                                    }
                                }
                            },
                            checkedIcon = painterResource(R.drawable.ic_downvote),
                            uncheckedIcon = painterResource(R.drawable.ic_downvote),
                            contentDescription = stringResource(R.string.ic_downvote_cdesc),
                            modifier = Modifier.size(30.dp, 40.dp),
                            uncheckedRadius = MediumCornerRadius,
                            checkedRadius = FullCornerRadius,
                            colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkedContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                } else {
                    FilledTonalIconButton(
                        onClick = { onMoreClick() },
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(30.dp, 40.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_more_vert),
                            contentDescription = stringResource(R.string.ic_more_vert_cdesc)
                        )
                    }
                }
            }
        }
    }
}