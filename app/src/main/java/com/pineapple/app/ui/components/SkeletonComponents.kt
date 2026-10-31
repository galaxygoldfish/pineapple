package com.pineapple.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Placeholder that mirrors the layout of a [CommentCard] while comments are loading. Uses
 * [MaterialTheme.colorScheme] tokens and a gentle pulse animation.
 * @param modifier The modifier to be applied to the skeleton
 * @param lineWidthFractions Width of each body text line as a fraction of the card's width, which
 * also determines how many lines are drawn
 */
@Composable
fun CommentSkeleton(
    modifier: Modifier = Modifier,
    lineWidthFractions: List<Float> = listOf(1f, 0.85f, 0.6f)
) {
    val transition = rememberInfiniteTransition(label = "commentSkeleton")
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "commentSkeletonPulse"
    )
    val boneColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Column(modifier = modifier.alpha(pulse)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(boneColor)
            )
            Box(
                modifier = Modifier
                    .padding(start = 7.dp)
                    .width(90.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(boneColor)
            )
            Box(
                modifier = Modifier
                    .padding(start = 7.dp)
                    .width(28.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(boneColor)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 7.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(10.dp)
        ) {
            lineWidthFractions.forEach { fraction ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(boneColor)
                )
            }
        }
    }
}
