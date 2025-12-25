package com.pineapple.app.utilities

import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

/**
 * Convert a unix timestamp (UTC) to a pretty-formatted time relative to now (e.g., "5m" or "16h")
 */
fun Long.convertUnixToRelativeTime() : String {
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

/**
 * Convert an integer to a social media style pretty-formatted number (e.g., 1500 -> "1.5K")
 */
fun Int.prettyNumber() : String {
    if (this < 1000) return "" + this
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return java.lang.String.format(
        Locale.ENGLISH, "%.1f%c",
        (this / 1000.0.pow(exp.toDouble())), "KMGTPE"[exp - 1]
    )
}