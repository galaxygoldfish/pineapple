package com.pineapple.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.util.getPreferences
import com.pineapple.app.util.surfaceColorAtElevation

@Composable
fun WelcomeView(navController: NavController) {
    rememberSystemUiController().setSystemBarsColor(MaterialTheme.colorScheme.surface)
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
                FilledTonalButton(
                    onClick = {
                        /**
                        navController.context.getPreferences().edit()
                            .putBoolean("ONBOARDING_COMPLETE", true)
                            .apply()
                        **/
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
                        navController.apply {
                            context.getPreferences().edit()
                                .putBoolean("USER_GUEST", true)
                                .putBoolean("ONBOARDING_COMPLETE", true)
                                .apply()
                            navigate(NavDestination.HomePageView)
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