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
import kotlin.math.ln
import kotlin.math.pow


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
    val longTime = this * 1000L
    val currentTime = System.currentTimeMillis()
    val secondMs = 1000L
    val minuteMs = 60L * secondMs
    val hourMs = 60L * minuteMs
    val dayMs = 24L * hourMs
    val weekMs = 7L * dayMs
    val monthMs = 30L * dayMs
    val yearMs = 12L * monthMs
    val timeDifference = currentTime - longTime
     return when {
         timeDifference < minuteMs -> "${timeDifference / secondMs}s"
         timeDifference < 60L * minuteMs -> "${timeDifference / minuteMs}m"
         timeDifference < 24L * hourMs -> "${timeDifference / hourMs}h"
         timeDifference < 30L * dayMs -> "${timeDifference / dayMs}d"
         timeDifference < 7L * weekMs -> "${timeDifference / weekMs}w"
         timeDifference < 12L * monthMs -> "${timeDifference / monthMs}mo"
         timeDifference > yearMs -> "${timeDifference / yearMs}y"
         else -> ""
    }
}