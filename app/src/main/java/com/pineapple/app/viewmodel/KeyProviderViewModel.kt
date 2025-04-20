package com.pineapple.app.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.pineapple.app.util.getPreferences
import java.util.UUID
import androidx.core.net.toUri
import com.pineapple.app.NavDestination
import com.pineapple.app.network.RedditNetworkProvider

class KeyProviderViewModel : ViewModel() {

    var clientSecretText by mutableStateOf(TextFieldValue())
    var showLoadingState by mutableStateOf(false)
    var invalidClientSecret by mutableStateOf(false)

    /**
     * Verifies the client secret. If an error is thrown, the user will be prompted
     * to try another one. If successful, the user will be redirected to the home page
     */
    suspend fun submitClientSecret(
        parentContext: Activity,
        userGuest: Boolean,
        navController: NavController
    ) {
        showLoadingState = true
        val networkProvider = RedditNetworkProvider(parentContext)
        try {
            networkProvider.tokenVerity(clientSecretText.text)
        } catch (e: Exception) {
            invalidClientSecret = true
            showLoadingState = false
            return
        }
        parentContext.getPreferences().edit(commit = true) {
            putString("CLIENT_SECRET", clientSecretText.text)
            putBoolean("ONBOARDING_COMPLETE", true)
        }
        if (userGuest) {
            navController.navigate(NavDestination.HomePageView)
        } else {
            Intent(Intent.ACTION_VIEW).apply {
                data = ("https://www.reddit.com/api/v1/authorize.compact"
                        + "?client_id=${clientSecretText.text}"
                        + "&response_type=code"
                        + "&state=${UUID.randomUUID()}"
                        + "&redirect_uri=pineapple://login"
                        + "&duration=permanent"
                        + "&scope=identity edit flair history modconfig modflair modlog modposts, modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread").toUri()
                parentContext.startActivity(this)
            }
        }
    }

}