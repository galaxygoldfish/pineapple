package com.pineapple.app.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import kotlin.math.ln


fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T
    = ViewModelProvider(this as ComponentActivity).get(type)

fun Context.drawableResource(@DrawableRes id: Int): Drawable
    = resources.getDrawable(id, theme)

fun CombinedLoadStates.isLoading() : Boolean
    = refresh is LoadState.Loading || append is LoadState.Loading

fun View.keyboardIsVisible(): Boolean = WindowInsetsCompat
        .toWindowInsetsCompat(rootWindowInsets)
        .isVisible(WindowInsetsCompat.Type.ime())

// From androidx.compose.material3.ColorScheme
fun ColorScheme.surfaceColorAtElevation(elevation: Dp): Color {
    if (elevation == 0.dp) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(surface)
}

fun Int.toDp(context: Context) : Dp
    = (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).dp

fun Int.toPx(context: Context) : Int
    = (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

fun Context.calculateRatioHeight(ratioHeight: Int, ratioWidth: Int, actualWidth: Int) : Dp {
    val ratio = ratioWidth.toDp(this) / ratioHeight.toDp(this)
    return (actualWidth / ratio).dp
}

fun Context.getPreferences() = getSharedPreferences(packageName, MODE_PRIVATE)
