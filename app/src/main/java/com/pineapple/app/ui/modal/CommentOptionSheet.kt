@file:OptIn(ExperimentalMaterial3Api::class)

package com.pineapple.app.ui.modal

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pineapple.app.R
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.ui.components.TonalActionSectionItem
import com.pineapple.app.ui.components.TonalActionSectionList
import com.pineapple.app.utilities.subredditName

/**
 * Modal bottom sheet displaying extended options, intended to be called from a comment card
 * @param comment The comment for which options are being displayed
 * @param onDismissRequest Callback when the sheet is dismissed
 * @param onViewCommunity Callback to view the community the comment was made in
 * @param onOpenExternal Callback to open the comment in an external browser
 * @param onReport Callback to report the comment
 * @param onViewUser Callback to view the comment author's profile. The option is hidden if null,
 * e.g. when already viewing the author's profile
 */
@Composable
fun CommentOptionSheet(
    comment: CommentEntity,
    onDismissRequest: () -> Unit,
    onViewCommunity: () -> Unit,
    onOpenExternal: () -> Unit,
    onReport: () -> Unit,
    onViewUser: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val subreddit = comment.subredditName()

    val items = buildList {
        if (subreddit != null) {
            add(
                TonalActionSectionItem(
                    text = "Go to r/$subreddit",
                    icon = painterResource(id = R.drawable.ic_community),
                    contentDescription = stringResource(R.string.ic_community_cdesc),
                    onCLick = onViewCommunity
                )
            )
        }
        if (onViewUser != null) {
            add(
                TonalActionSectionItem(
                    text = "View u/${comment.author}",
                    icon = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(R.string.ic_person_cdesc),
                    onCLick = onViewUser
                )
            )
        }
        add(
            TonalActionSectionItem(
                text = "Copy comment",
                icon = painterResource(id = R.drawable.ic_copy),
                contentDescription = stringResource(R.string.ic_copy_cdesc),
                onCLick = {
                    comment.body?.let { body ->
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Comment Body", body))
                    }
                    onDismissRequest()
                }
            )
        )
        add(
            TonalActionSectionItem(
                text = "Open in browser",
                icon = painterResource(id = R.drawable.ic_open_external),
                contentDescription = stringResource(R.string.ic_open_external_cdesc),
                onCLick = onOpenExternal
            )
        )
        add(
            TonalActionSectionItem(
                text = "Report comment",
                icon = painterResource(id = R.drawable.ic_flag),
                contentDescription = stringResource(R.string.ic_flag_cdesc),
                onCLick = onReport
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        TonalActionSectionList(
            items = items,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
        )
    }
}
