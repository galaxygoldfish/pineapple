package com.pineapple.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.model.reddit.CommentPreDataNull
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.network.RedditNetworkProvider

class UserViewModel : ViewModel() {

    private lateinit var redditNetworkProvider: RedditNetworkProvider
    var currentlySelectedTab by mutableStateOf(0)
    var userPostList = mutableStateListOf<PostItem>()
    var userCommentList = mutableStateListOf<CommentPreDataNull>()
    var currentContentList = mutableStateListOf<Any>(userPostList)

    fun initNetworkProvider(context: Context) {
        redditNetworkProvider = RedditNetworkProvider(context)
    }

    suspend fun requestUserDetails(user: String) : UserAbout {
        return redditNetworkProvider.fetchUserInfo(user).data
    }

    suspend fun updateUserContent(user: String) {
        redditNetworkProvider.apply {
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