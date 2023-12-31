package com.pineapple.app.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.util.getPreferences
import java.util.UUID

@ExperimentalMaterial3Api
@Composable
fun WelcomeView(navController: NavController) {
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
                        Button(
                            onClick = {
                                navController.context.getPreferences().edit()
                                    .putString("CLIENT_SECRET", clientSecret.value)
                                    .apply()
                                showSecretDialog.value = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Done")
                        }
                        Button(
                            onClick = {
                                showSecretDialog.value = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Cancel")
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
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
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
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pineapple_transparent),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(top = 40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = stringResource(id = R.string.welcome_slogan_text),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 2.dp, top = 5.dp)
                )
                Button(
                    onClick = {
                        showSecretDialog.value = true
                    }
                ){
                    Text(text = "Enter Client Secret")
                }
                FilledTonalButton(
                    onClick = {
                        if (navController.context.getPreferences().getString("CLIENT_SECRET", "") == ""){
                            showEmptySecretDialog.value = true
                            return@FilledTonalButton
                        } else {
                            navController.context.getPreferences().edit()
                                .putBoolean("ONBOARDING_COMPLETE", true)
                                .apply()
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(
                                    "https://www.reddit.com/api/v1/authorize.compact"
                                            + "?client_id="
                                            + navController.context.getPreferences().getString("CLIENT_SECRET", "")
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
                        .padding(top = 30.dp)
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
                OutlinedButton(
                    onClick = {
                        if (navController.context.getPreferences().getString("CLIENT_SECRET", "") == ""){
                            showEmptySecretDialog.value = true
                            return@OutlinedButton
                        } else {
                            navController.apply {
                                context.getPreferences().edit()
                                    .putBoolean("USER_GUEST", true)
                                    .putBoolean("ONBOARDING_COMPLETE", true)
                                    .apply()
                                navigate(NavDestination.HomePageView)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = stringResource(id = R.string.ic_person_content_desc)
                    )
                    Text(
                        text = stringResource(id = R.string.welcome_continue_guest_button),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_leaves_transparent),
                contentDescription = stringResource(id = R.string.ic_leaves_transparent_content_desc),
                modifier = Modifier
                    .fillMaxWidth(0.75F)
                    .align(Alignment.BottomEnd),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}