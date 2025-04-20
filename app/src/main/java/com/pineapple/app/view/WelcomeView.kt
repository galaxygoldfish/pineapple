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
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pineapple_transparent),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(top = 30.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Text(
                    text = stringResource(id = R.string.welcome_slogan_text),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 15.dp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        navController.navigate("${NavDestination.KeyProviderView}/login")
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
                        context.getPreferences().edit(commit = true) {
                            putBoolean("USER_GUEST", true)
                        }
                        navController.navigate("${NavDestination.KeyProviderView}/guest")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
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
            Column(modifier = Modifier.align(Alignment.BottomEnd)) {
                Icon(
                    painter = painterResource(id = R.drawable.leaves_transparent),
                    contentDescription = stringResource(id = R.string.ic_leaves_transparent_content_desc),
                    modifier = Modifier
                        .fillMaxWidth(),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}