package com.pineapple.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T
    = ViewModelProvider(this as ComponentActivity).get(type)

fun Context.drawableResource(@DrawableRes id: Int): Drawable
    = resources.getDrawable(id, theme)
