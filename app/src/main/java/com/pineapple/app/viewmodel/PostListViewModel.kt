package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.PostListing
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostListViewModel : ViewModel() {

    var data by mutableStateOf<PostListing?>(null)
    var isRefreshing by mutableStateOf(true)

    private val networkService by lazy { NetworkServiceBuilder.apiService() }

    fun performRequest(name: String, sort: String) {
        isRefreshing = true
        CoroutineScope(Dispatchers.Default).launch {
            val forecastAPICall: Call<PostListing> = networkService.fetchSubreddit(name, sort)
            forecastAPICall.enqueue(object : Callback<PostListing> {
                override fun onResponse(call: Call<PostListing>, response: Response<PostListing>) {
                    if (response.isSuccessful && response.body() != null) {
                        data = response.body()
                        isRefreshing = false
                    }
                }
                override fun onFailure(call: Call<PostListing>, t: Throwable) {
                    isRefreshing = false
                    TODO("Not yet implemented")
                }
            })
        }
    }
}