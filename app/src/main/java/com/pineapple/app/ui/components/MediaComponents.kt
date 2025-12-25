package com.pineapple.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.pineapple.app.R

/**
 * Wrapper to the AsyncImage composable intended for images that are used in scrolling
 * layouts, which keeps a fixed size even before the image is loaded to eliminate jank
 * caused by changing layout sizes.
 * @param imageUrl The URL of the image to load.
 * @param aspectRatio The aspect ratio (width / height) to use for the image.
 * @param modifier The modifier to be applied to the image.
 * @param contentDescription The content description for the image.
 */
@Composable
fun MeasuredAsyncImage(
    imageUrl: String,
    aspectRatio: Float?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    )
    val ratio = aspectRatio ?: (16f / 9f)

    // Keep the AnimatedContent transition, but apply the external modifier to the inner Box
    AnimatedContent(
        targetState = painter.state,
        transitionSpec = {
            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(150))
        },
        contentAlignment = Alignment.TopCenter,
        label = "ImageLoad"
    ) { state ->
        // Apply the caller-provided modifier to the measured container so size is stable
        Box(
            modifier = modifier
                .aspectRatio(ratio),
            contentAlignment = Alignment.TopCenter
        ) {
            when (state.collectAsState().value) {
                is AsyncImagePainter.State.Loading -> {
                    // Placeholder image that fills the container
                    AsyncImage(
                        model = R.drawable.async_image_placeholder,
                        contentDescription = contentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is AsyncImagePainter.State.Success -> {
                    // Loaded image fills the container immediately
                    Image(
                        painter = painter,
                        contentDescription = contentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    )
                }
            }
        }
    }
}
