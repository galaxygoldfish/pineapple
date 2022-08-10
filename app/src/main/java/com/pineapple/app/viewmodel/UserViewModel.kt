package com.pineapple.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.reddit.CommentPreData
import com.pineapple.app.model.reddit.CommentPreDataNull
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.RedditNetworkService

class UserViewModel : ViewModel() {

    private val redditNetworkService by lazy { apiService<RedditNetworkService>(REDDIT_BASE_URL) }
    var currentlySelectedTab by mutableStateOf(0)
    var userPostList = mutableStateListOf<PostItem>()
    var userCommentList = mutableStateListOf<CommentPreDataNull>()

    suspend fun requestUserDetails(user: String) : UserAbout {
        return redditNetworkService.fetchUserInfo(user).data
    }

    suspend fun updateUserContent(user: String) {
        redditNetworkService.apply {
            userPostList.apply {
                clear()
                addAll(getUserPosts(user).data.children)
            }
            userCommentList.apply {
                clear()
                addAll(getUserComments(user).data.children)

                Log.e("DD", getUserComments(user).kind)
            }
        }
    }

}