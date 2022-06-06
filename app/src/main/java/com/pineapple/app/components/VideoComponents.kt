package com.pineapple.app.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
fun ExoVideoPlayer(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            StyledPlayerView(context).apply {
                player = ExoPlayer.Builder(context).build().apply {
                    val dataSourceFactory = DefaultDataSource.Factory(context)
                    val internetVideoItem = MediaItem.fromUri(url)
                    val internetVideoSource = ProgressiveMediaSource
                        .Factory(dataSourceFactory)
                        .createMediaSource(internetVideoItem)
                    addMediaSource(internetVideoSource)
                    prepare()
                }
            }
        },
    )
}
