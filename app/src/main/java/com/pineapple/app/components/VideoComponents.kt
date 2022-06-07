package com.pineapple.app.components

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ComponentRegistry
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.OriginalSize
import coil.size.Size
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.pineapple.app.R

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

@Composable
fun GifVideoPlayer(
    modifier: Modifier = Modifier,
    url: String
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(data = url)
                .apply { size(Size.ORIGINAL) }
                .build(),
            imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}
