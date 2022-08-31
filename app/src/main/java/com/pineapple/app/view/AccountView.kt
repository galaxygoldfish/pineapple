package com.pineapple.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pineapple.app.R
import com.pineapple.app.components.UserAvatarIcon

@Composable
fun AccountView(navController: NavController) {
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
                    userInfo = null,
                    onClick = {  },
                    modifier = Modifier
                        .padding(20.dp)
                        .size(80.dp)
                )
                Column {
                    Text(
                        text = stringResource(id = R.string.account_unauthenticated_header_text),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(id = R.string.account_unauthenticated_subtitle_text),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        FilledTonalButton(
            onClick = {
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