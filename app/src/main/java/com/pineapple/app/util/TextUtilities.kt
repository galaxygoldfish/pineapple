package com.pineapple.app.util

import android.text.format.DateUtils
import android.util.Log
import android.util.Log.e
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.model.reddit.FlairRichItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone.getDefault
import java.util.TimeZone.getTimeZone
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

// https://www.avinsharma.com/android-hackernews-client-jetpack-compose/
fun Long.convertUnixToRelativeTime() : String{
    val longTime = this * 1000
    val currentTime = System.currentTimeMillis()
    val SECOND_MILLIS = 1000;
    val MINUTE_MILLIS = 60 * SECOND_MILLIS;
    val HOUR_MILLIS = 60 * MINUTE_MILLIS;
    val DAY_MILLIS = 24 * HOUR_MILLIS;
    val timeDifference = currentTime - longTime
     return when {
        timeDifference < MINUTE_MILLIS -> "now"
        timeDifference < 50 * MINUTE_MILLIS -> "${timeDifference / MINUTE_MILLIS}m"
        timeDifference < 24 * HOUR_MILLIS -> "${timeDifference / HOUR_MILLIS}h"
        else -> "${timeDifference / DAY_MILLIS}d"
    }
}