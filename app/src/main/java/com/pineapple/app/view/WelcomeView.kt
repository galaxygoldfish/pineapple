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
import com.pineapple.app.R

@Composable
fun WelcomeView(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pineapple_color),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(top = 40.dp)
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
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reddit_logo),
                        contentDescription = stringResource(id = R.string.ic_reddit_logo_content_desc)
                    )
                    Text(
                        text = stringResource(id = R.string.welcome_reddit_log_in_button),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_icon),
                        contentDescription = stringResource(id = R.string.ic_user_icon_content_desc)
                    )
                    Text(
                        text = stringResource(id = R.string.welcome_continue_guest_button),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_welcome_leaves),
                contentDescription = stringResource(id = R.string.ic_welcome_leaves_content_desc),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth(0.75F)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}