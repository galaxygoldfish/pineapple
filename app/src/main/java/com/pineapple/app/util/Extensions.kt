package com.pineapple.app.util

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T
        = ViewModelProvider(this as ComponentActivity).get(type)