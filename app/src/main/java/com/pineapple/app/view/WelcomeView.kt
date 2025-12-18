package com.pineapple.app.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.navigation.NavController
import com.pineapple.app.BuildConfig
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.util.getPreferences
import java.util.UUID

@ExperimentalMaterial3Api
@Composable
fun WelcomeView(navController: NavController) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.pineapple_filled),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.padding(top = 30.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(top = 15.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(id = R.string.welcome_slogan_text),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 15.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    modifier = Modifier.padding(bottom = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            navController.navigate("${NavDestination.KeyProviderView}/login")
                        },
                        modifier = Modifier
                            .padding(top = 30.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.welcome_reddit_log_in_button),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            context.getPreferences().edit(commit = true) {
                                putBoolean("USER_GUEST", true)
                            }
                            navController.navigate("${NavDestination.KeyProviderView}/guest")
                        },
                        modifier = Modifier.padding(top = 10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.welcome_continue_guest_button),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
    }
}