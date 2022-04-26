package com.pineapple.app.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
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
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.util.parseFlair
import java.io.File

@Composable
fun Chip(
    text: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )
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
fun ExoVideoPlayer(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember { getSimpleExoPlayer(context, url) }
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        factory = { context1 ->
            PlayerView(context1).apply {
                player = exoPlayer
            }
        },
    )
}

private fun getSimpleExoPlayer(context: Context, url: String) : ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )
        val internetVideoItem = MediaItem.fromUri(url)
        val internetVideoSource = ProgressiveMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(internetVideoItem)
        addMediaSource(internetVideoSource)
        prepare()
    }
}