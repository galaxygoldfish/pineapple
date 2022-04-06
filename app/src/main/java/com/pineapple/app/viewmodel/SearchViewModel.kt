package com.pineapple.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.RequestResult
import com.pineapple.app.network.NetworkServiceBuilder
import kotlinx.coroutines.flow.flow

class SearchViewModel : ViewModel() {

    private val networkService by lazy { NetworkServiceBuilder.apiService() }
    var currentSearchQuery by mutableStateOf(TextFieldValue())

    suspend fun requestSubredditFlow() = flow {
        emit(RequestResult.Loading(true))
        val response = networkService.fetchTopSubreddits()
        if (response.data.children.isNotEmpty()) {
            emit(RequestResult.Success(response.data.children))
        } else {
            emit(RequestResult.Error("ERROR"))
        }
    }

}