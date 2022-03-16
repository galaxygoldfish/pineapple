package com.pineapple.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import java.lang.String.format
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T
    = ViewModelProvider(this as ComponentActivity).get(type)

fun Context.drawableResource(@DrawableRes id: Int): Drawable
    = resources.getDrawable(id, theme)

fun CombinedLoadStates.isLoading() : Boolean
    = refresh is LoadState.Loading || append is LoadState.Loading

fun Int.prettyNumber() : String {
    if (this < 1000) return "" + this
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return format(Locale.ENGLISH, "%.1f %c",
        (this / 1000.0.pow(exp.toDouble())), "kMGTPE"[exp - 1])
}