package com.pineapple.app.view

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.pineapple.app.BuildConfig
import com.pineapple.app.R
import com.pineapple.app.components.UserAvatarIcon
import com.pineapple.app.model.reddit.AboutAccount
import com.pineapple.app.network.RedditNetworkProvider
import com.pineapple.app.util.getPreferences
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getPreferences()
    val userGuest = sharedPreferences.getBoolean("USER_GUEST", true)
    val showSecretDialog = remember {
        mutableStateOf(false)
    }
    val clientSecret = remember {
        mutableStateOf("")
    }
    val showEmptySecretDialog = remember {
        mutableStateOf(false)
    }
    if (showSecretDialog.value){
        Dialog(
            onDismissRequest = {
                showSecretDialog.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            content = {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Enter Client Secret",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = clientSecret.value,
                            onValueChange = {
                                clientSecret.value = it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = {
                                if (clientSecret.value.isNotEmpty()) {
                                    sharedPreferences.edit()
                                        .putString("CLIENT_SECRET", clientSecret.value).apply()
                                    showSecretDialog.value = false
                                } else {
                                    showSecretDialog.value = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        )
    }

    if (showEmptySecretDialog.value){
        Dialog(
            onDismissRequest = {
                showEmptySecretDialog.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            content = {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Client Secret is Empty",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Please enter a valid client secret",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "You can find your client secret at https://www.reddit.com/prefs/apps",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = {
                                showEmptySecretDialog.value = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        )
    }

    var currentAccountData by remember { mutableStateOf<AboutAccount?>(null) }
    LaunchedEffect(key1 = true) {
        currentAccountData = RedditNetworkProvider(context).fetchAccountInfo()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatarIcon(
                    snoovatarImage = currentAccountData?.snoovatar_img ?: "",
                    iconImage = currentAccountData?.subreddit?.icon_img ?: "",
                    defaultIcon = currentAccountData?.subreddit?.is_default_icon ?: true,
                    onClick = {  },
                    modifier = Modifier
                        .padding(20.dp)
                        .size(80.dp)
                )
                Column {
                    Text(
                        text = if (userGuest) {
                            stringResource(id = R.string.account_unauthenticated_header_text)
                        } else {
                               currentAccountData?.subreddit?.display_name_prefixed.toString()
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (userGuest) {
                        Text(
                            text = stringResource(id = R.string.account_unauthenticated_subtitle_text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        if (userGuest) {
            Button(
                onClick = {
                    showSecretDialog.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ){
                Text(text = "Enter Client Secret")
            }
            FilledTonalButton(
                onClick = {
                    if( navController.context.getPreferences().getString("CLIENT_SECRET", "") == ""){
                        showEmptySecretDialog.value = true
                        return@FilledTonalButton
                    } else {
                        Intent(ACTION_VIEW).apply {
                            data = Uri.parse(
                                "https://www.reddit.com/api/v1/authorize.compact"
                                        + "?client_id="
                                        + navController.context.getPreferences().getString("CLIENT_SECRET","")
                                        + "&response_type=code"
                                        + "&state=${UUID.randomUUID()}"
                                        + "&redirect_uri=pineapple://login"
                                        + "&duration=permanent"
                                        + "&scope=identity edit flair history modconfig modflair modlog modposts, modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"
                            )
                            context.startActivity(this)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_login),
                    contentDescription = stringResource(id = R.string.ic_login_content_desc)
                )
                Text(
                    text = stringResource(id = R.string.welcome_reddit_log_in_button),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
        }
    }
}