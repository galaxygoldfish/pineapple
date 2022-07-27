package com.pineapple.app.viewmodel

import androidx.lifecycle.ViewModel
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkService

class UserViewModel : ViewModel() {

    val redditNetworkService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }

    suspend fun requestUserDetails(user: String) : UserAbout {
        return redditNetworkService.fetchUserInfo(user).data
    }

}