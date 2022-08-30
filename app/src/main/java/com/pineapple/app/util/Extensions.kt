package com.pineapple.app.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState


fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T
    = ViewModelProvider(this as ComponentActivity).get(type)

fun Context.drawableResource(@DrawableRes id: Int): Drawable
    = resources.getDrawable(id, theme)

fun CombinedLoadStates.isLoading() : Boolean
    = refresh is LoadState.Loading || append is LoadState.Loading

fun View.keyboardIsVisible(): Boolean = WindowInsetsCompat
        .toWindowInsetsCompat(rootWindowInsets)
        .isVisible(WindowInsetsCompat.Type.ime())

fun Int.toDp(context: Context) : Dp
    = (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).dp

fun Int.toPx(context: Context) : Int
    = (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

fun Context.calculateRatioHeight(ratioHeight: Int, ratioWidth: Int, actualWidth: Int) : Dp {
    val ratio = ratioWidth.toDp(this) / ratioHeight.toDp(this)
    return (actualWidth / ratio).dp
}

fun Context.getPreferences() = getSharedPreferences(packageName, MODE_PRIVATE)
