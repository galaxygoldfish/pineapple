package com.pineapple.app.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.R
import com.pineapple.app.model.FlairRichItem
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

fun List<FlairRichItem>.parseFlair() : Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val iconUrlData = mutableListOf<String>()
    val iconImageData = mutableMapOf<String, InlineTextContent>()
    val annotatedString = buildAnnotatedString {
        forEachIndexed { index, flairRichItem ->
            when (flairRichItem.e) {
                "text" -> append(flairRichItem.t!!.replace("&amp;", "&"))
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
                    .placeholder(R.drawable.placeholder_image)
                    .crossfade(true)
                    .build().data,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
    return Pair(first = annotatedString, second = iconImageData)
}