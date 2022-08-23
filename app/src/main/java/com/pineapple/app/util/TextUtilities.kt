package com.pineapple.app.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.model.reddit.FlairRichItem
import java.util.Locale
import kotlin.math.*


fun Int.prettyNumber() : String {
    if (this < 1000) return "" + this
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return java.lang.String.format(
        Locale.ENGLISH, "%.1f%c",
        (this / 1000.0.pow(exp.toDouble())), "KMGTPE"[exp - 1]
    )
}

fun List<FlairRichItem>.parseFlair(darkTheme: Boolean): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val iconUrlData = mutableListOf<String>()
    val iconImageData = mutableMapOf<String, InlineTextContent>()
    val annotatedString = buildAnnotatedString {
        forEachIndexed { index, flairRichItem ->
            when (flairRichItem.e) {
                "text" -> {
                    val text = flairRichItem.t!!.replace("&amp;", "&")
                    append(text)
                    addStyle(
                        style = SpanStyle(color = if (darkTheme) Color.White else Color.Black),
                        start = 0,
                        end = text.length
                    )
                }
                "emoji" -> {
                    appendInlineContent(id = "icon${index - 1}")
                    iconUrlData.add(flairRichItem.u!!)
                }
            }
        }
    }
    iconUrlData.forEachIndexed { index, url ->
        iconImageData["icon${index}"] = InlineTextContent(
            Placeholder(15.sp, 15.sp, PlaceholderVerticalAlign.TextCenter)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .build().data,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
    return Pair(first = annotatedString, second = iconImageData)
}

fun Long.convertUnixToRelativeTime() : String{
    val longTime = this * 1000
    val currentTime = System.currentTimeMillis()
    val secondMs = 1000;
    val minuteMs = 60 * secondMs
    val hourMs = 60 * minuteMs
    val dayMs = 24 * hourMs
    val timeDifference = currentTime - longTime
     return when {
        timeDifference < minuteMs -> "now"
        timeDifference < 50 * minuteMs -> "${timeDifference / minuteMs}m"
        timeDifference < 24 * hourMs -> "${timeDifference / hourMs}h"
        else -> "${timeDifference / dayMs}d"
    }
}