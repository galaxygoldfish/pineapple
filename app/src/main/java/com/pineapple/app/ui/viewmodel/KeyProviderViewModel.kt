package com.pineapple.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pineapple.app.ui.state.AuthViewState
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.network.repository.RedditAuthRepository
import com.pineapple.app.network.repository.RedditRepository
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class KeyProviderViewModel @Inject constructor(
    private val repository: RedditAuthRepository,
    val mmkv: MMKV
) : ViewModel() {

    var clientSecretTextFieldValue by mutableStateOf(TextFieldValue())

    private val internalViewState = MutableStateFlow<AuthViewState>(AuthViewState.Idle)
    val viewState: StateFlow<AuthViewState> = internalViewState.asStateFlow()

    /**
     * Attempt to get an authentication token (userless) to see if the provided client ID
     * is a valid one. If the user chose to be a guest, this is the end of the authentication
     * flow, however if they wanted to login, they will need to authenticate via Reddit OAuth next.
     */
    fun submitClientSecret() {
        viewModelScope.launch {
            internalViewState.value = AuthViewState.Loading
            try {
                repository.authenticateUserless(
                    clientId = clientSecretTextFieldValue.text,
                    testingClientID = true
                )
                mmkv.putString(MMKVKey.CLIENT_ID, clientSecretTextFieldValue.text)
                internalViewState.value = AuthViewState.Success
            } catch (_: Exception) {
                internalViewState.value = AuthViewState.Error("Invalid client secret")
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