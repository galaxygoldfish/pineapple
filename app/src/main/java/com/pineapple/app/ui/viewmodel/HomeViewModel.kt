@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pineapple.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pineapple.app.R
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.consts.PageDestinationKey
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.network.paging.PagingRepository
import com.pineapple.app.network.repository.RedditRepository
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RedditRepository,
    private val mmkv: MMKV
) : ViewModel() {

    val snackbarState = SnackbarHostState()
    var showPostFilterSheet by mutableStateOf(false)
    var showPostOptionSheet by mutableStateOf(false)
    var currentPostOptionData by mutableStateOf<PostData?>(null)

    var currentNavPage by mutableIntStateOf(PageDestinationKey.BROWSE)

    val isUserless by lazy { mmkv.getBoolean(MMKVKey.USER_GUEST, true) }

    val topSubreddits = repository.observePopularSubreddits()
    val subscribedSubreddits = repository.observeSubscribedSubreddits()

    init {
        viewModelScope.launch {
            repository.refreshPopularSubreddits()
            if (!isUserless) {
                repository.refreshSubscribedSubreddits()
            }
        }
    }

    /**
     * Open the post overflow bottom sheet menu
     */
    fun openPostOptionSheet(postData: PostData) {
        currentPostOptionData = postData
        showPostOptionSheet = true
    }

    /**
     * Open a snackbar notifying the user that the action they are trying to perform requires
     * authentication, with a button allowing them to log in if they want to
     */
    fun encourageUserAuthSnackbar() {
        viewModelScope.launch {
            val result = snackbarState.showSnackbar(
                message = context.getString(R.string.home_snackbar_auth_text),
                actionLabel = context.getString(R.string.home_snackbar_auth_login),
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                launchRedditAuthFlow(context)
            }
        }
    }

    /**
     * Open the reddit authentication flow in the default browser, which will return to the app
     * via a deep link once completed, containing the code in a query parameter. (handled in NavHost)
     */
    fun launchRedditAuthFlow(context: Context) {
        Intent(Intent.ACTION_VIEW).apply {
            data = ("https://www.reddit.com/api/v1/authorize.compact" +
                            "?client_id=${mmkv.decodeString(MMKVKey.CLIENT_ID)}" +
                            "&response_type=code" +
                            "&state=${UUID.randomUUID()}" +
                            "&redirect_uri=pineapple://login" +
                            "&duration=permanent" +
                            "&scope=identity edit flair history modconfig modflair modlog " +
                            "modposts, modwiki mysubreddits privatemessages read report save " +
                            "submit subscribe vote wikiedit wikiread"
                    ).toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }
}
