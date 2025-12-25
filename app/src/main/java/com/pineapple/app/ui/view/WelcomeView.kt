package com.pineapple.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pineapple.app.R
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.consts.OnboardingLoginType
import com.pineapple.app.ui.theme.PineappleTheme

@Composable
fun WelcomeView(navController: NavController) {
    PineappleTheme {
        Surface {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.systemBarsPadding().fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pineapple_logo),
                        contentDescription = stringResource(R.string.ic_pineapple_logo_cdesc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.welcome_app_name),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = stringResource(R.string.welcome_slogan_text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 30.dp)
                ) {
                    Button(
                        onClick = {
                            navController.navigate(
                                "${NavDestinationKey.KeyProviderView}/${OnboardingLoginType.RedditAuth}"
                            )
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                       Text(
                           text = stringResource(R.string.welcome_sign_in_button),
                           modifier = Modifier.padding(10.dp),
                           style = MaterialTheme.typography.titleMedium
                       )
                    }

                    Button(
                        onClick = {
                            navController.navigate(
                                "${NavDestinationKey.KeyProviderView}/${OnboardingLoginType.Guest}"
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.welcome_continue_as_guest_button),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
