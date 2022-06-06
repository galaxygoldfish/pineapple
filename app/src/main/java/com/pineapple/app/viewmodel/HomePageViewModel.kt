package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomePageViewModel : ViewModel() {

    var selectedTabItem by mutableStateOf(0)
    val currentSortType = mutableStateOf("Hot")
    val currentSortTime = mutableStateOf("All time")

}