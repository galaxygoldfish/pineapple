package com.pineapple.app.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.pineapple.app.R
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.util.parseFlair
import com.pineapple.app.util.surfaceColorAtElevation
import java.io.File

@Composable
fun Chip(
    text: String,
    icon: Painter? = null,
    contentDescription: String? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    unselectedBackground: Color = MaterialTheme.colorScheme.surface
) {
    Row(
        modifier = modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    unselectedBackground
                }
            )
            .clickable { onClick.invoke() }
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(7.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = contentDescription,
                modifier = Modifier.padding(8.dp)
                    .size(20.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}

@Composable
fun FlairBar(postData: PostData, modifier: Modifier = Modifier) {
    postData.linkFlairRichtext?.let { list ->
        if (list.isNotEmpty()) {
            list.parseFlair(isSystemInDarkTheme()).let { pair ->
                Column(
                    modifier = modifier
                        .padding(top = 15.dp)
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

@Composable
fun AvatarPlaceholderIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_avatar_placeholder),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .size(35.dp)
            .padding(top = 8.dp)
    )
}