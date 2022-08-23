package com.pineapple.app.components

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pineapple.app.R
import com.pineapple.app.model.reddit.PostData
import com.pineapple.app.util.parseFlair
import com.pineapple.app.util.toPx
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun SheetHandle() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .clip(RoundedCornerShape(5.dp))
                .size(width = 100.dp, height = 5.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .align(Alignment.CenterHorizontally)
        ) { }
    }
}

@Composable
fun drawerCustomShape(
    localConfig: Configuration,
    context: Context,
    widthRatio: Float = 0.8F
) = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val screenWidthPixels = localConfig.screenWidthDp.toPx(context)
        val screenHeightPixels = localConfig.screenHeightDp.toPx(context) + 5
        return Outline.Rounded(
            RoundRect(
                left = 0f,
                top = 0f,
                right = screenWidthPixels * widthRatio,
                bottom = screenHeightPixels.toFloat(),
                topRightCornerRadius = CornerRadius(40F, 40F),
                bottomRightCornerRadius = CornerRadius(40F, 40F)
            )
        )
    }
}

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
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 10.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
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


// https://github.com/pz64/RoundedStarShape-JetPack-Compose/blob/main/compose-polygonshape/src/main/java/com/pz64/shape/RoundedStarShape.kt
class RoundedStarShape(
    private val sides: Int,
    private val curve: Double = 0.09,
    private val rotation: Float = 0f,
    iterations: Int = 360,
) : Shape {

    private companion object {
        const val TWO_PI = 2 * PI
    }

    private val steps = (TWO_PI) / min(iterations, 360)
    private val rotationDegree = (PI / 180) * rotation

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(Path().apply {
        val r = min(size.height, size.width) * 0.4 * mapRange(1.0, 0.0, 0.5, 1.0, curve)
        val xCenter = size.width * .5f
        val yCenter = size.height * .5f
        moveTo(xCenter, yCenter)
        var t = 0.0
        while (t <= TWO_PI) {
            val x = r * (cos(t - rotationDegree) * (1 + curve * cos(sides * t)))
            val y = r * (sin(t - rotationDegree) * (1 + curve * cos(sides * t)))
            lineTo((x + xCenter).toFloat(), (y + yCenter).toFloat())

            t += steps
        }
        val x = r * (cos(t - rotationDegree) * (1 + curve * cos(sides * t)))
        val y = r * (sin(t - rotationDegree) * (1 + curve * cos(sides * t)))
        lineTo((x + xCenter).toFloat(), (y + yCenter).toFloat())

    })

    private fun mapRange(a: Double, b: Double, c: Double, d: Double, x: Double): Double {
        return (x - a) / (b - a) * (d - c) + c
    }
}