package com.pineapple.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.pineapple.app.R
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.consts.OnboardingLoginType
import com.pineapple.app.ui.components.TonalActionSectionItem
import com.pineapple.app.ui.components.TonalActionSectionList
import com.pineapple.app.ui.viewmodel.AccountViewModel
import com.pineapple.app.utilities.getBestUserImage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AccountPage(navController: NavController) {
    val viewModel: AccountViewModel = hiltViewModel()
    val userData by viewModel.user.collectAsState()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        if (viewModel.isUserless) {
            GuestState(navController = navController)
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (viewModel.isLoading && userData == null) {
                        CircularProgressIndicator(modifier = Modifier.padding(vertical = 30.dp))
                    } else if (userData != null) {
                        val user = userData!!
                        AsyncImage(
                            model = getBestUserImage(user.snoovatarUrl, user.iconUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = (user.displayName ?: user.name).removePrefix("u_"),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = "u/${user.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        user.createdUtc?.let { created ->
                            val date = remember(created) {
                                DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
                                    .withZone(ZoneId.systemDefault())
                                    .format(Instant.ofEpochSecond(created))
                            }
                            Text(
                                text = stringResource(
                                    R.string.account_karma_joined,
                                    date,
                                    user.totalKarma ?: 0
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            val items = mutableListOf(
                TonalActionSectionItem(
                    text = stringResource(R.string.account_view_profile),
                    icon = painterResource(R.drawable.ic_person),
                    contentDescription = stringResource(R.string.ic_person_cdesc),
                    onCLick = {
                        userData?.let { user ->
                            navController.navigate("${NavDestinationKey.UserView}/${user.name}")
                        }
                    }
                ),
                TonalActionSectionItem(
                    text = stringResource(R.string.account_settings),
                    icon = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.ic_settings_cdesc)
                ),
                TonalActionSectionItem(
                    text = stringResource(R.string.account_logout),
                    icon = painterResource(R.drawable.ic_logout),
                    contentDescription = stringResource(R.string.ic_logout_cdesc),
                    onCLick = { showLogoutConfirm = true }
                )
            )

            TonalActionSectionList(
                items = items,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            )
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(stringResource(R.string.account_logout_confirm_title)) },
            text = { Text(stringResource(R.string.account_logout_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                        navController.navigate(NavDestinationKey.WelcomeView) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text(stringResource(R.string.account_logout_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(stringResource(R.string.account_cancel_button))
                }
            }
        )
    }
}

@Composable
private fun GuestState(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, start = 20.dp, end = 20.dp)
    ) {
        Button(
            onClick = {
                navController.navigate(
                    "${NavDestinationKey.KeyProviderView}/${OnboardingLoginType.RedditAuth}"
                )
            },
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_reddit),
                contentDescription = stringResource(R.string.ic_reddit_cdesc),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.account_sign_in_button),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
