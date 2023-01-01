package com.pineapple.app.view

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.util.Log
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
import androidx.navigation.NavController
import com.pineapple.app.BuildConfig
import com.pineapple.app.R
import com.pineapple.app.components.UserAvatarIcon
import com.pineapple.app.model.reddit.AboutAccount
import com.pineapple.app.network.RedditNetworkProvider
import com.pineapple.app.util.getPreferences
import java.util.UUID

@Composable
fun AccountView(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getPreferences()
    val userGuest = sharedPreferences.getBoolean("USER_GUEST", true)
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
            FilledTonalButton(
                onClick = {
                    Intent(ACTION_VIEW).apply {
                        data = Uri.parse(
                            "https://www.reddit.com/api/v1/authorize.compact"
                                    + "?client_id=${BuildConfig.ClientSecret}"
                                    + "&response_type=code"
                                    + "&state=${UUID.randomUUID()}"
                                    + "&redirect_uri=pineapple://login"
                                    + "&duration=permanent"
                                    + "&scope=identity edit flair history modconfig modflair modlog modposts, modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"
                        )
                        context.startActivity(this)
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