@file:OptIn(ExperimentalMaterial3Api::class)

package com.pineapple.app.ui.modal

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pineapple.app.R
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.ui.components.AnimatedTonalToggleIconButton
import com.pineapple.app.ui.components.CommentCard
import com.pineapple.app.ui.theme.MediumCornerRadius
import com.pineapple.app.utilities.prettyNumber

@Composable
fun CommentDetailSheet(
    commentWithUser: CommentWithUser,
    onDismissRequest: () -> Unit,
    onDownvote: (Boolean, () -> Unit) -> Unit,
    onUpvote: (Boolean, () -> Unit) -> Unit,
    onSaveClick: (Boolean, () -> Unit) -> Unit,
    onViewUserClick: () -> Unit
) {
    var upvoteState by remember { mutableStateOf(commentWithUser.comment.likes == true) }
    var downvoteState by remember { mutableStateOf(commentWithUser.comment.likes == false) }
    var bookmarkedState by remember { mutableStateOf(commentWithUser.comment.saved) }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column {
            CommentCard(
                commentWithUser = commentWithUser,
                showingTrailingButtons = false,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.padding(horizontal = 15.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 12.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    FilledTonalIconButton(
                        onClick = {
                            commentWithUser.comment.permalink?.let { url ->
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
                    FilledTonalIconButton(
                        onClick = onViewUserClick,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = stringResource(R.string.ic_person_cdesc)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = {
                            commentWithUser.comment.body?.let { bodyText ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Comment Body",
                                    bodyText
                                )
                                clipboard.setPrimaryClip(clip)
                            }
                            // Maybe only this for devices that don't have the clipboard popup
                            // Toast.makeText(context, R.string.post_copied_comment, Toast.LENGTH_SHORT).show()
                            onDismissRequest()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_copy),
                            contentDescription = stringResource(R.string.ic_copy_cdesc)
                        )
                    }
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
                    commentWithUser.comment.ups?.toInt()?.prettyNumber()?.let {
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
}