@file:OptIn(ExperimentalMaterial3Api::class)

package com.pineapple.app.ui.modal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pineapple.app.R
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.ui.components.TonalActionSectionItem
import com.pineapple.app.ui.components.TonalActionSectionList

/**
 * Modal bottom sheet displaying extended options, intended to be called from a post card
 * @param postData Data of the post for which options are being displayed
 * @param onDismissRequest Callback when the sheet is dismissed
 * @param onViewUser Callback to view the post author's profile
 * @param onViewCommunity Callback to view the post's community
 * @param onOpenExternal Callback to open the post in an external browser
 * @param onReport Callback to report the post
 */
@Composable
fun PostOptionSheet(
    postData: PostData,
    onDismissRequest: () -> Unit,
    onViewUser: () -> Unit,
    onViewCommunity: () -> Unit,
    onOpenExternal: () -> Unit,
    onReport: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        TonalActionSectionList(
            items = listOf(
                TonalActionSectionItem(
                    text = "Go to ${postData.subredditNamePrefixed}",
                    icon = painterResource(id = R.drawable.ic_community),
                    contentDescription = stringResource(R.string.ic_community_cdesc),
                    onCLick = onViewCommunity
                ),
                TonalActionSectionItem(
                    text = "View u/${postData.author}",
                    icon = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(R.string.ic_person_cdesc),
                    onCLick = onViewUser
                ),
                TonalActionSectionItem(
                    text = "Open in browser",
                    icon = painterResource(id = R.drawable.ic_open_external),
                    contentDescription = stringResource(R.string.ic_open_external_cdesc),
                    onCLick = onOpenExternal
                ),
                TonalActionSectionItem(
                    text = "Report post",
                    icon = painterResource(id = R.drawable.ic_flag),
                    contentDescription = stringResource(R.string.ic_flag_cdesc),
                    onCLick = onReport
                )
            ),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
        )
    }
}