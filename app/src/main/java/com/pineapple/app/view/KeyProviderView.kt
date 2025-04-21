package com.pineapple.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pineapple.app.MainActivity
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.R
import com.pineapple.app.util.getPreferences
import com.pineapple.app.viewmodel.KeyProviderViewModel
import kotlinx.coroutines.launch

@Composable
fun KeyProviderView(
    navController: NavController,
    loginType: String
) {
    val viewModel: KeyProviderViewModel = viewModel()
    val parentContext = navController.context as MainActivity
    val coroutineScope = rememberCoroutineScope()
    parentContext.getPreferences().apply {
        viewModel.clientSecretText = TextFieldValue(getString("CLIENT_SECRET", "") ?: "")
    }
    Surface {
        if (viewModel.showLoadingState) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
                    .systemBarsPadding()

            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.ic_arrow_back_content_desc)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.provide_key_title_text),
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = stringResource(R.string.provide_key_subtitle_text),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.clientSecretText,
                            isError = viewModel.invalidClientSecret,
                            onValueChange = {
                                viewModel.invalidClientSecret = false
                                viewModel.clientSecretText = it
                            },
                            label = {
                                Text(stringResource(R.string.provide_key_field_text))
                            },
                            supportingText = {
                                if (viewModel.invalidClientSecret) {
                                    Text(
                                        text = stringResource(R.string.provide_key_field_error_supporting)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 20.dp)
                        )
                        TextButton(
                            onClick = {
                                /* Taking user to a markdown file that I will upload to the repo that explains the client secret */
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp)
                        ) {
                            Text(stringResource(R.string.provide_key_help_button_text))
                        }
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.submitClientSecret(
                                    parentContext = parentContext,
                                    userGuest = loginType == "guest",
                                    navController = navController
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = stringResource(R.string.ic_check_content_desc),
                                modifier = Modifier.padding(end = 10.dp)
                                    .size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.provide_key_submit_button_text)
                            )
                        }
                    }
                }
            }
        }
    }
}