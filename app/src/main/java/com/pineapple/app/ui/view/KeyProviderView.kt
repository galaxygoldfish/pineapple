package com.pineapple.app.ui.view

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pineapple.app.ui.state.AuthViewState
import com.pineapple.app.R
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.consts.OnboardingLoginType
import com.pineapple.app.ui.theme.PineappleTheme
import com.pineapple.app.ui.viewmodel.KeyProviderViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KeyProviderView(navController: NavController, loginType: String) {
    val viewModel: KeyProviderViewModel = hiltViewModel()
    val viewState = viewModel.viewState.collectAsState()

    LaunchedEffect(viewState.value) {
        if (viewState.value is AuthViewState.Success) {
            when (loginType) {
                OnboardingLoginType.Guest -> {
                    viewModel.mmkv.encode(MMKVKey.ONBOARDING_COMPLETE, true)
                    navController.navigate(NavDestinationKey.HomeView)
                }
                OnboardingLoginType.RedditAuth -> {
                    viewModel.launchRedditAuthFlow(navController.context)
                }
            }
        }
    }

    PineappleTheme {
        AnimatedContent(viewState.value is AuthViewState.Loading) { loading ->
            if (!loading) {
                Scaffold(
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.provide_key_title_text),
                                    style = MaterialTheme.typography.displaySmall
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_back),
                                        contentDescription = stringResource(R.string.ic_back_cdesc)
                                    )
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                viewModel.submitClientSecret()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_forward),
                                contentDescription = stringResource(R.string.ic_forward_cdesc),
                                modifier = Modifier
                                    .padding(30.dp)
                                    .size(26.dp)
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        Text(
                            text = stringResource(R.string.provide_key_subtitle_text),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                        )
                        TextField(
                            value = viewModel.clientSecretTextFieldValue,
                            onValueChange = {
                                viewModel.clientSecretTextFieldValue = it
                            },
                            label = {
                                Text(stringResource(R.string.provide_key_entry_hint))
                            },
                            modifier = Modifier
                                .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                                .fillMaxWidth(),
                            singleLine = true,
                            supportingText = {
                                if (viewState.value is AuthViewState.Error) {
                                    Text(
                                        text = (viewState.value as AuthViewState.Error).message
                                    )
                                }
                            },
                            isError = viewState.value is AuthViewState.Error
                        )
                        TextButton(
                            onClick = {
                                navController.context.startActivity(
                                    Intent(Intent.ACTION_VIEW, "https://reddit.com/prefs/apps".toUri())
                                )
                            },
                            modifier = Modifier.padding(top = 25.dp, start = 10.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_reddit),
                                contentDescription = stringResource(R.string.ic_reddit_cdesc)
                            )
                            Text(
                                text = stringResource(R.string.provide_key_dev_button),
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                        TextButton(
                            onClick = {
                                // Link to some markdown file in the github
                            },
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_help),
                                contentDescription = stringResource(R.string.ic_help_cdesc)
                            )
                            Text(
                                text = stringResource(R.string.provide_key_what_button),
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoadingIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(100.dp)
                    )
                }
            }
        }
    }
}
