package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.network.GfycatNetworkService
import com.pineapple.app.network.NetworkServiceBuilder
import com.pineapple.app.network.NetworkServiceBuilder.GFYCAT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService

class MediaDetailViewModel : ViewModel() {

    var mediaType by mutableStateOf<String?>(null)
    var url by mutableStateOf<String?>(null)
    val gfycatService by lazy { apiService<GfycatNetworkService>(GFYCAT_BASE_URL) }


}